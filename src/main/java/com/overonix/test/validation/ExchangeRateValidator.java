package com.overonix.test.validation;

import java.util.Set;

public interface ExchangeRateValidator {

  void validateExchangeData(String from, String to, String amount, Set<String> currencies);
}
