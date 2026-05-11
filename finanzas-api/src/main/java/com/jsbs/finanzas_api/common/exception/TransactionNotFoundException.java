package com.jsbs.finanzas_api.common.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("Transaction with id " + id + " not found");
    }
}
