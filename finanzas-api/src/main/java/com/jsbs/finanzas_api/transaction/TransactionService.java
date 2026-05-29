package com.jsbs.finanzas_api.transaction;

import com.jsbs.finanzas_api.auth.PagedResponse;
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
import org.springframework.data.jpa.domain.Specification;
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
    private final TransactionMapper transactionMapper;

    public TransactionResponse createTransaction(TransactionRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Category category = categoryRepository.findByIdAndUser(
                request.categoryId(),
                currentUser
        ).orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Transaction transaction = Transaction.builder()
                .amount(request.amount())
                .description(request.description())
                .date(request.date())
                .category(category)
                .user(currentUser)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toResponse(savedTransaction);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new TransactionNotFoundException(id));

        return transactionMapper.toResponse(transaction);
    }

    @Transactional
    public void deleteTransactionById(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transactionRepository.delete(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new TransactionNotFoundException(id));

        Category category = categoryRepository.findByIdAndUser(
                request.categoryId(),
                currentUser
        ).orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setDate(request.date());
        transaction.setCategory(category);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toResponse(updatedTransaction);
    }

    @Transactional(readOnly = true)
    public TransactionSummaryResponse getSummary(LocalDateTime start, LocalDateTime end) {

        User currentUser = currentUserService.getCurrentUser();

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(currentUser.getId(), start, end);

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

    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> getAllTransactions(
            Pageable pageable,
            CategoryType type,
            Long categoryId,
            LocalDateTime start,
            LocalDateTime end) {

        User currentUser = currentUserService.getCurrentUser();

        Specification<Transaction> spec =
                TransactionSpecification.belongsToUser(currentUser);

        if (type != null) {
            spec = spec.and(TransactionSpecification.hasCategoryType(type));
        }

        if (categoryId != null) {
            spec = spec.and(TransactionSpecification.hasCategoryId(categoryId));
        }

        if (start != null && end != null) {
            spec = spec.and(TransactionSpecification.hasDateBetween(start, end));
        }

        Page<Transaction> transactionsPage = transactionRepository.findAll(spec, pageable);

        List<TransactionResponse> content = transactionsPage.getContent()
                .stream()
                .map(transactionMapper::toResponse)
                .toList();

        return new PagedResponse<>(
                content,
                transactionsPage.getNumber(),
                transactionsPage.getSize(),
                transactionsPage.getTotalElements(),
                transactionsPage.getTotalPages(),
                transactionsPage.isLast()
        );
    }


}
