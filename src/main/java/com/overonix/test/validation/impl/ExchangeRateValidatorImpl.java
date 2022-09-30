package com.overonix.test.validation.impl;

import com.overonix.test.exception.InvalidCurrencyCodeException;
import com.overonix.test.validation.ExchangeRateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExchangeRateValidatorImpl implements ExchangeRateValidator {

  private static final String INVALID_CURRENCY_CODE =
      "Entered invalid currency code. Check entered data: %s, %s";
  private static final String INVALID_AMOUNT = "Entered invalid amount: %s. Check index: %d";

  @Override
  public void validateExchangeData(String from, String to, String amount, Set<String> currencies) {
    validateCurrencyCode(from, to, currencies);
    validateAmount(amount);
  }

  private void validateCurrencyCode(String from, String to, Set<String> currencies) {
    if (!currencies.contains(from) || !currencies.contains(to)) {
      throw new InvalidCurrencyCodeException(String.format(INVALID_CURRENCY_CODE, from, to));
    }
  }

  private void validateAmount(String amount) {
    for (int i = 0; i < amount.length(); i++) {
      if (!Character.isDigit(amount.charAt(i))) {
        throw new NumberFormatException(String.format(INVALID_AMOUNT, amount, i + 1));
      }
    }
  }
}
