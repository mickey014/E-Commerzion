package com.codewithkirk.productService.Exception;

public class ProductOrderServiceUnavailableException extends RuntimeException{
    public ProductOrderServiceUnavailableException(String message) {
        super(message);
    }

    public ProductOrderServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
