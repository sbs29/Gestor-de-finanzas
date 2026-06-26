package com.jsbs.finanzas_api.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsbs.finanzas_api.auth.LoginRequest;
import com.jsbs.finanzas_api.auth.LoginResponse;
import com.jsbs.finanzas_api.category.Category;
import com.jsbs.finanzas_api.category.CategoryRepository;
import com.jsbs.finanzas_api.category.CategoryType;
import com.jsbs.finanzas_api.user.Role;
import com.jsbs.finanzas_api.user.User;
import com.jsbs.finanzas_api.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@SpringBootTest
@ActiveProfiles("local")
@AutoConfigureMockMvc
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {

    }

    @Test
    void shouldReturnTransactionsFromDatabase() throws Exception {

        User user = User.builder()
                .name("Demo User")
                .email("demo@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .name("Comida")
                .type(CategoryType.EXPENSE)
                .user(savedUser)
                .build();

        Category savedCategory = categoryRepository.save(category);

        Transaction transaction = Transaction.builder()
                .amount(new BigDecimal("25.50"))
                .description("Supermercado")
                .date(LocalDateTime.now())
                .category(savedCategory)
                .user(savedUser)
                .build();

        transactionRepository.save(transaction);

        mockMvc.perform(get("/api/transactions")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        savedUser,
                                        null,
                                        List.of()
                                )
                        ))
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].description").value("Supermercado"))
                .andExpect(jsonPath("$.content[0].amount").value(25.50))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldCreateTransactionAndPersistInDatabase() throws Exception {

        User user = User.builder()
                .name("Demo user")
                .email("demo@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .name("Comida")
                .type(CategoryType.EXPENSE)
                .user(savedUser)
                .build();

        Category savedCategory = categoryRepository.save(category);

        TransactionRequest request = new TransactionRequest(
                new BigDecimal("50.00"),
                "Compra real",
                LocalDateTime.now(),
                savedCategory.getId()
        );

        mockMvc.perform(post("/api/transactions")
                .with(csrf())
                .with(authentication(
                        new UsernamePasswordAuthenticationToken(
                                savedUser,
                                null,
                                List.of()
                        )
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Compra real"));

        List<Transaction> transactions =transactionRepository.findAll();

        assertThat(transactions).hasSize(1);

        Transaction savedTransaction = transactions.get(0);

        assertThat(savedTransaction.getDescription()).isEqualTo("Compra real");

        assertThat(savedTransaction.getAmount()).isEqualByComparingTo("50.00");

    }

    @Test
    void shouldCreateAndRetrieveTransaction()
            throws Exception {

        User user = User.builder()
                .name("Demo User")
                .email("demo@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .name("Comida")
                .type(CategoryType.EXPENSE)
                .user(savedUser)
                .build();

        Category savedCategory = categoryRepository.save(category);

        TransactionRequest request = new TransactionRequest(
                new BigDecimal("75.00"),
                "Restaurante",
                LocalDateTime.now(),
                savedCategory.getId()
        );

        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        savedUser,
                                        null,
                                        List.of()
                                )
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/transactions")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        savedUser,
                                        null,
                                        List.of()
                                )
                        ))
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description")
                        .value("Restaurante"))
                .andExpect(jsonPath("$.content[0].amount")
                        .value(75.00))
                .andExpect(jsonPath("$.totalElements")
                        .value(1));
    }

    @Test
    void shouldNotReturnTransactionsFromAnotherUser()
            throws Exception {

        User userA = User.builder()
                .name("User A")
                .email("usera@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        User savedUserA = userRepository.save(userA);

        User userB = User.builder()
                .name("User B")
                .email("userb@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        User savedUserB = userRepository.save(userB);

        Category category = Category.builder()
                .name("Comida")
                .type(CategoryType.EXPENSE)
                .user(savedUserA)
                .build();

        Category savedCategory = categoryRepository.save(category);

        Transaction transaction = Transaction.builder()
                .amount(new BigDecimal("99.99"))
                .description("Privada")
                .date(LocalDateTime.now())
                .category(savedCategory)
                .user(savedUserA)
                .build();

        transactionRepository.save(transaction);

        mockMvc.perform(get("/api/transactions")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        savedUserB,
                                        null,
                                        List.of()
                                )
                        ))
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void shouldReturnTransactionById()
            throws Exception {

        User user = User.builder()
                .name("Demo User")
                .email("demo@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .name("Comida")
                .type(CategoryType.EXPENSE)
                .user(savedUser)
                .build();

        Category savedCategory = categoryRepository.save(category);

        Transaction transaction = Transaction.builder()
                .amount(new BigDecimal("45.00"))
                .description("Cena")
                .date(LocalDateTime.now())
                .category(savedCategory)
                .user(savedUser)
                .build();

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        mockMvc.perform(get("/api/transactions/" +
                        savedTransaction.getId())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        savedUser,
                                        null,
                                        List.of()
                                )
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(savedTransaction.getId()))
                .andExpect(jsonPath("$.description")
                        .value("Cena"))
                .andExpect(jsonPath("$.amount")
                        .value(45.00));
    }

    @Test
    void shouldNotReturnTransactionFromAnotherUser()
            throws Exception {

        User userA = User.builder()
                .name("User A")
                .email("usera@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        User savedUserA = userRepository.save(userA);

        User userB = User.builder()
                .name("User B")
                .email("userb@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        User savedUserB = userRepository.save(userB);

        Category category = Category.builder()
                .name("Privada")
                .type(CategoryType.EXPENSE)
                .user(savedUserA)
                .build();

        Category savedCategory = categoryRepository.save(category);

        Transaction transaction = Transaction.builder()
                .amount(new BigDecimal("100.00"))
                .description("Secreta")
                .date(LocalDateTime.now())
                .category(savedCategory)
                .user(savedUserA)
                .build();

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        mockMvc.perform(get("/api/transactions/" +
                        savedTransaction.getId())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        savedUserB,
                                        null,
                                        List.of()
                                )
                        )))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteOwnTransaction() throws Exception {

        User user = User.builder()
                .name("Demo User")
                .email("demo@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .name("Comida")
                .type(CategoryType.EXPENSE)
                .user(savedUser)
                .build();

        Category savedCategory = categoryRepository.save(category);

        Transaction transaction = Transaction.builder()
                .amount(new BigDecimal("30.00"))
                .description("Compra a borrar")
                .date(LocalDateTime.now())
                .category(savedCategory)
                .user(savedUser)
                .build();

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        mockMvc.perform(delete("/api/transactions/" + savedTransaction.getId())
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        savedUser,
                                        null,
                                        List.of()
                                )
                        )))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(transactionRepository.findById(savedTransaction.getId()))
                .isEmpty();
    }

    @Test
    void shouldUpdateOwnTransaction() throws Exception {

        User user = User.builder()
                .name("Demo User")
                .email("demo@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        Category category = Category.builder()
                .name("Comida")
                .type(CategoryType.EXPENSE)
                .user(savedUser)
                .build();

        Category savedCategory = categoryRepository.save(category);

        Transaction transaction = Transaction.builder()
                .amount(new BigDecimal("20.00"))
                .description("Compra original")
                .date(LocalDateTime.now())
                .category(savedCategory)
                .user(savedUser)
                .build();

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        TransactionRequest request = new TransactionRequest(
                new BigDecimal("45.00"),
                "Compra actualizada",
                LocalDateTime.now(),
                savedCategory.getId()
        );

        mockMvc.perform(put("/api/transactions/" + savedTransaction.getId())
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        savedUser,
                                        null,
                                        List.of()
                                )
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Compra actualizada"))
                .andExpect(jsonPath("$.amount").value(45.00));

        Transaction updatedTransaction =
                transactionRepository.findById(savedTransaction.getId())
                        .orElseThrow();

        assertThat(updatedTransaction.getDescription())
                .isEqualTo("Compra actualizada");

        assertThat(updatedTransaction.getAmount())
                .isEqualByComparingTo("45.00");
    }


    @Test
    void shouldAccessProtectedEndpointWithRealJwtToken()
            throws Exception {

        User user = User.builder()
                .name("Demo User")
                .email("demo@test.com")
                .password(passwordEncoder.encode("123456"))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(
                "demo@test.com",
                "123456"
        );

        String responseBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginResponse loginResponse =
                objectMapper.readValue(
                        responseBody,
                        LoginResponse.class
                );

        mockMvc.perform(get("/api/transactions")
                        .header("Authorization",
                                "Bearer " + loginResponse.token())
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenAccessingProtectedEndpointWithoutToken() throws Exception {

        mockMvc.perform(get("/api/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectInvalidJwtToken() throws Exception {

        mockMvc.perform(get("/api/transactions")
                        .header(
                                "Authorization",
                                "Bearer invalid-token"
                        )
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid()
            throws Exception {

        LoginRequest request = new LoginRequest(
                "wrong@test.com",
                "wrong-password"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status")
                        .value(401))
                .andExpect(jsonPath("$.message")
                        .value("Invalid Credentials"));
    }

    @Test
    void shouldReturnBadRequestWhenLoginRequestIsInvalid()
            throws Exception {

        LoginRequest request = new LoginRequest(
                "",
                ""
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());
    }

}
