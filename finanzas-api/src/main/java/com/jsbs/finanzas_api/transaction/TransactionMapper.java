package com.jsbs.finanzas_api.transaction;

import com.jsbs.finanzas_api.category.Category;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {

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

}
