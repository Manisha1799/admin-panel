package com.admin.exception;

public class CountryRestrictedException extends RuntimeException {
    public CountryRestrictedException(String message) {
        super(message);
    }
} 