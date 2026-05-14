package com.jsbs.finanzas_api.transaction;

import com.jsbs.finanzas_api.category.Category;
import com.jsbs.finanzas_api.category.CategoryRepository;
import com.jsbs.finanzas_api.category.CategoryType;
import com.jsbs.finanzas_api.common.exception.CategoryNotFoundException;
import com.jsbs.finanzas_api.common.exception.TransactionNotFoundException;
import com.jsbs.finanzas_api.security.CurrentUserService;
import com.jsbs.finanzas_api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    public TransactionResponse createTransaction(TransactionRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->new CategoryNotFoundException(request.categoryId()));

        Transaction transaction = Transaction.builder()
                .amount(request.amount())
                .description(request.description())
                .date(request.date())
                .category(category)
                .user(currentUser)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return toResponse(savedTransaction);
    }

    private TransactionResponse toResponse(Transaction transaction) {

        Category category = transaction.getCategory();

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                category.getId(),
                category.getName(),
                category.getType()
        );
    }

    public List<TransactionResponse> getAllTransactions() {

        User currentUser = currentUserService.getCurrentUser();
        return transactionRepository.findByUser(currentUser)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse getTransactionById(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        return toResponse(transaction);
    }

    @Transactional
    public void deleteTransactionById(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transactionRepository.delete(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setDate(request.date());
        transaction.setCategory(category);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return toResponse(updatedTransaction);
    }

    public TransactionSummaryResponse getSummary(LocalDateTime start, LocalDateTime end) {

        User currentUser = currentUserService.getCurrentUser();

        List<Transaction> transactions = transactionRepository.findByUserAndDateBetween(currentUser, start, end);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getCategory().getType() == CategoryType.INCOME) {
                income = income.add(transaction.getAmount());
            } else if (transaction.getCategory().getType() == CategoryType.EXPENSE) {
                expense = expense.add(transaction.getAmount());
            }
        }

        BigDecimal balance = income.subtract(expense);

        return new TransactionSummaryResponse(
                income,
                expense,
                balance
        );
    }

    public Page<TransactionResponse> getAllTransactions(Pageable pageable) {

        User currentUser = currentUserService.getCurrentUser();

        return transactionRepository.findByUser(currentUser, pageable).map(this::toResponse);
    }
}
