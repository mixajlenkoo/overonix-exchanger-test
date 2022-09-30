package com.overonix.test.exception;

public class InvalidCurrencyCodeException extends RuntimeException {

  public InvalidCurrencyCodeException(String message) {
    super(message);
  }
}
