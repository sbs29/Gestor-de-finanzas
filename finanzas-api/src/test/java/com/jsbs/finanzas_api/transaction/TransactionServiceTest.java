package com.jsbs.finanzas_api.transaction;

import com.jsbs.finanzas_api.category.Category;
import com.jsbs.finanzas_api.category.CategoryType;
import com.jsbs.finanzas_api.common.exception.TransactionNotFoundException;
import com.jsbs.finanzas_api.security.CurrentUserService;
import com.jsbs.finanzas_api.user.Role;
import com.jsbs.finanzas_api.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldCalculateSummaryCorrectly() {
        // arrange

        User user = User.builder()
                .id(1L)
                .name("Demo User")
                .email("demo@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        Category salary = Category.builder()
                .id(1L)
                .name("Salario")
                .type(CategoryType.INCOME)
                .user(user)
                .build();

        Category food = Category.builder()
                .id(2L)
                .name("Comida")
                .type(CategoryType.EXPENSE)
                .user(user)
                .build();

        List<Transaction> transactions = List.of(
                Transaction.builder()
                        .id(1L)
                        .amount(new BigDecimal("1000.00"))
                        .description("Nómina")
                        .date(LocalDateTime.now())
                        .category(salary)
                        .user(user)
                        .build(),

                Transaction.builder()
                        .id(2L)
                        .amount(new BigDecimal("200.00"))
                        .description("Supermercado")
                        .date(LocalDateTime.now())
                        .category(food)
                        .user(user)
                        .build()
        );

        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByUserAndDateBetween(user, start, end))
                .thenReturn(transactions);

        // act

        TransactionSummaryResponse result =
                transactionService.getSummary(start, end);

        // assert

        assertThat(result.income()).isEqualByComparingTo("1000.00");
        assertThat(result.expense()).isEqualByComparingTo("200.00");
        assertThat(result.balance()).isEqualByComparingTo("800.00");

        verify(currentUserService).getCurrentUser();

        verify(transactionRepository)
                .findByUserAndDateBetween(user, start, end);
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        // arrange

        Long transactionId = 99L;

        User user = User.builder()
                .id(1L)
                .name("Demo User")
                .email("demo@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByIdAndUser(transactionId, user))
                .thenReturn(Optional.empty());

        // act + assert

        assertThatThrownBy(() ->
                transactionService.getTransactionById(transactionId)
        ).isInstanceOf(TransactionNotFoundException.class);

        verify(currentUserService).getCurrentUser();
        verify(transactionRepository).findByIdAndUser(transactionId, user);
    }
}
