package com.areeba.cms.cmsmircoservice.exception;

public class TransactionRejectedException extends RuntimeException {
    public TransactionRejectedException(String message) {
        super(message);
    }
}
