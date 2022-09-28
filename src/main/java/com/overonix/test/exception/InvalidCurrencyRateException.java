package com.overonix.test.exception;

public class InvalidCurrencyRateException extends RuntimeException {

  public InvalidCurrencyRateException(String message) {
    super(message);
  }
}
