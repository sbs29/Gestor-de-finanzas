package com.jsbs.finanzas_api.transaction;

import com.jsbs.finanzas_api.category.Category;
import com.jsbs.finanzas_api.category.CategoryRepository;
import com.jsbs.finanzas_api.common.exception.CategoryNotFoundException;
import com.jsbs.finanzas_api.common.exception.TransactionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionResponse createTransaction(TransactionRequest request) {

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->new CategoryNotFoundException(request.categoryId()));

        Transaction transaction = Transaction.builder()
                .amount(request.amount())
                .description(request.description())
                .date(request.date())
                .category(category)
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
        return transactionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse getTransactionById(Long id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        return toResponse(transaction);
    }

    @Transactional
    public void deleteTransactionById(Long id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transactionRepository.delete(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {

        Transaction transaction = transactionRepository.findById(id)
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
}
