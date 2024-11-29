package com.codewithkirk.sellerService.Exception;

public class SellerNotFoundException extends RuntimeException{

    public SellerNotFoundException(String message) {
        super(message);
    }

    public SellerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
