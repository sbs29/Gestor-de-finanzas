package com.jsbs.finanzas_api.config;

import com.jsbs.finanzas_api.category.Category;
import com.jsbs.finanzas_api.category.CategoryRepository;
import com.jsbs.finanzas_api.category.CategoryType;
import com.jsbs.finanzas_api.transaction.Transaction;
import com.jsbs.finanzas_api.transaction.TransactionRepository;
import com.jsbs.finanzas_api.user.Role;
import com.jsbs.finanzas_api.user.User;
import com.jsbs.finanzas_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail("demo@test.com")) {
            return;
        }

        User user = User.builder()
                .name("Demo User")
                .email("demo@test.com")
                .password(passwordEncoder.encode("123456"))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        Category salary = createCategory("Salario", CategoryType.INCOME, savedUser);
        Category freelance = createCategory("Freelance", CategoryType.INCOME, savedUser);
        Category food = createCategory("Comida", CategoryType.EXPENSE, savedUser);
        Category transport = createCategory("Transporte", CategoryType.EXPENSE, savedUser);
        Category rent = createCategory("Alquiler", CategoryType.EXPENSE, savedUser);
        Category entertainment = createCategory("Ocio", CategoryType.EXPENSE, savedUser);

        categoryRepository.save(salary);
        categoryRepository.save(freelance);
        categoryRepository.save(food);
        categoryRepository.save(transport);
        categoryRepository.save(rent);
        categoryRepository.save(entertainment);

        transactionRepository.save(createTransaction(new BigDecimal("1800.00"), "Nómina mayo", LocalDateTime.of(2026, 5, 1, 9, 0), salary, savedUser));
        transactionRepository.save(createTransaction(new BigDecimal("250.00"), "Proyecto freelance", LocalDateTime.of(2026, 5, 5, 18, 30), freelance, savedUser));
        transactionRepository.save(createTransaction(new BigDecimal("650.00"), "Alquiler mayo", LocalDateTime.of(2026, 5, 2, 10, 0), rent, savedUser));
        transactionRepository.save(createTransaction(new BigDecimal("45.80"), "Supermercado", LocalDateTime.of(2026, 5, 3, 20, 15), food, savedUser));
        transactionRepository.save(createTransaction(new BigDecimal("23.50"), "Metro", LocalDateTime.of(2026, 5, 4, 8, 45), transport, savedUser));
        transactionRepository.save(createTransaction(new BigDecimal("38.90"), "Cine", LocalDateTime.of(2026, 5, 7, 21, 0), entertainment, savedUser));

        transactionRepository.save(createTransaction(new BigDecimal("1800.00"), "Nómina junio", LocalDateTime.of(2026, 6, 1, 9, 0), salary, savedUser));
        transactionRepository.save(createTransaction(new BigDecimal("720.00"), "Alquiler junio", LocalDateTime.of(2026, 6, 2, 10, 0), rent, savedUser));
        transactionRepository.save(createTransaction(new BigDecimal("61.20"), "Compra semanal", LocalDateTime.of(2026, 6, 3, 19, 20), food, savedUser));
    }

    private Category createCategory(String name, CategoryType type, User user) {
        return Category.builder()
                .name(name)
                .type(type)
                .user(user)
                .build();
    }

    private Transaction createTransaction(BigDecimal amount, String description, LocalDateTime date, Category category, User user) {
        return Transaction.builder()
                .amount(amount)
                .description(description)
                .date(date)
                .category(category)
                .user(user)
                .build();
    }
}