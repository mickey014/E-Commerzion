package com.codewithkirk.sellerService.Exception;

public class SellerException extends RuntimeException{

    public SellerException(String message) {
        super(message);
    }

    public SellerException(String message, Throwable cause) {
        super(message, cause);
    }
}
