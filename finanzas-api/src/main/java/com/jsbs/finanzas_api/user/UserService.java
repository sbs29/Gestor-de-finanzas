package com.jsbs.finanzas_api.user;

import com.jsbs.finanzas_api.auth.AuthResponse;
import com.jsbs.finanzas_api.auth.LoginRequest;
import com.jsbs.finanzas_api.auth.LoginResponse;
import com.jsbs.finanzas_api.auth.RegisterRequest;
import com.jsbs.finanzas_api.common.exception.EmailAlreadyExistsException;
import com.jsbs.finanzas_api.common.exception.InvalidCredentialsException;
import com.jsbs.finanzas_api.security.CurrentUserService;
import com.jsbs.finanzas_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final CurrentUserService currentUserService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.email());
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        return userMapper.toAuthResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token);
    }

    public UserProfileResponse getCurrentUserProfile() {
        User user = currentUserService.getCurrentUser();
        return userMapper.toUserProfileResponse(user);
    }

}
