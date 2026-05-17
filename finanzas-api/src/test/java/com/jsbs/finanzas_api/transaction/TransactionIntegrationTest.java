package com.jsbs.finanzas_api.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@SpringBootTest
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

}
