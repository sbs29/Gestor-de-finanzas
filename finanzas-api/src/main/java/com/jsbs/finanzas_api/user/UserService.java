package com.jsbs.finanzas_api.user;

import com.jsbs.finanzas_api.auth.AuthResponse;
import com.jsbs.finanzas_api.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {

        userRepository.existsByEmail(request.email());

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        return null;
    }

}
