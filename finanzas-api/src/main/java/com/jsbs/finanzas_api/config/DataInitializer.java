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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail("demo@finanzas.com")) {
            return;
        }

        User user = new User();
        user.setName("Usuario Demo");
        user.setEmail("demo@finanzas.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole(Role.USER);

        userRepository.save(user);

        Category salary = new Category();
        salary.setName("Nómina");
        salary.setType(CategoryType.INCOME);
        salary.setUser(user);

        Category food = new Category();
        food.setName("Comida");
        food.setType(CategoryType.EXPENSE);
        food.setUser(user);

        Category transport = new Category();
        transport.setName("Transporte");
        transport.setType(CategoryType.EXPENSE);
        transport.setUser(user);

        Category entertainment = new Category();
        entertainment.setName("Ocio");
        entertainment.setType(CategoryType.EXPENSE);
        entertainment.setUser(user);

        Category freelance = new Category();
        freelance.setName("Freelance");
        freelance.setType(CategoryType.INCOME);
        freelance.setUser(user);

        categoryRepository.saveAll(
                List.of(salary, food, transport, entertainment, freelance)
        );

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(
                createTransaction(
                        new BigDecimal("2200.00"),
                        "Nómina enero",
                        LocalDateTime.now().minusDays(90),
                        salary,
                        user
                )
        );

        transactions.add(
                createTransaction(
                        new BigDecimal("2200.00"),
                        "Nómina febrero",
                        LocalDateTime.now().minusDays(60),
                        salary,
                        user
                )
        );

        transactions.add(
                createTransaction(
                        new BigDecimal("2200.00"),
                        "Nómina marzo",
                        LocalDateTime.now().minusDays(30),
                        salary,
                        user
                )
        );

        transactions.add(
                createTransaction(
                        new BigDecimal("350.00"),
                        "Proyecto freelance",
                        LocalDateTime.now().minusDays(15),
                        freelance,
                        user
                )
        );

        transactions.add(
                createTransaction(
                        new BigDecimal("54.30"),
                        "Supermercado",
                        LocalDateTime.now().minusDays(25),
                        food,
                        user
                )
        );

        transactions.add(
                createTransaction(
                        new BigDecimal("23.10"),
                        "Comida fuera",
                        LocalDateTime.now().minusDays(20),
                        food,
                        user
                )
        );

        transactions.add(
                createTransaction(
                        new BigDecimal("61.25"),
                        "Combustible",
                        LocalDateTime.now().minusDays(18),
                        transport,
                        user
                )
        );

        transactions.add(
                createTransaction(
                        new BigDecimal("12.99"),
                        "Streaming",
                        LocalDateTime.now().minusDays(14),
                        entertainment,
                        user
                )
        );

        transactions.add(
                createTransaction(
                        new BigDecimal("45.00"),
                        "Cena amigos",
                        LocalDateTime.now().minusDays(10),
                        entertainment,
                        user
                )
        );

        transactions.add(
                createTransaction(
                        new BigDecimal("18.50"),
                        "Transporte público",
                        LocalDateTime.now().minusDays(7),
                        transport,
                        user
                )
        );

        Random random = new Random();

        List<Category> expenseCategories = List.of(
                food,
                transport,
                entertainment
        );

        List<Category> incomeCategories = List.of(
                salary,
                freelance
        );

        for (int i = 1; i <= 40; i++) {

            boolean income = random.nextBoolean();

            Category category = income
                    ? incomeCategories.get(random.nextInt(incomeCategories.size()))
                    : expenseCategories.get(random.nextInt(expenseCategories.size()));

            BigDecimal amount = income
                    ? BigDecimal.valueOf(500 + random.nextInt(2500))
                    : BigDecimal.valueOf(5 + random.nextInt(200));

            Transaction transaction = createTransaction(
                    amount,
                    generateDescription(category, i),
                    LocalDateTime.now().minusDays(random.nextInt(120)),
                    category,
                    user
            );

            transactions.add(transaction);
        }

        transactionRepository.saveAll(transactions);
    }

    private Transaction createTransaction(
            BigDecimal amount,
            String description,
            LocalDateTime date,
            Category category,
            User user
    ) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDate(date);
        transaction.setCategory(category);
        transaction.setUser(user);

        return transaction;
    }

    private String generateDescription(Category category, int index) {

        return switch (category.getName()) {

            case "Comida" -> "Compra supermercado #" + index;

            case "Transporte" -> "Gasolina #" + index;

            case "Ocio" -> "Ocio fin de semana #" + index;

            case "Nómina" -> "Ingreso nómina #" + index;

            case "Freelance" -> "Proyecto freelance #" + index;

            default -> "Movimiento #" + index;
        };
    }

}