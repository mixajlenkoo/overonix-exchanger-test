package com.overonix.test.service;

import com.overonix.test.model.dto.ExchangeRateDto;

import java.util.Set;

public interface CurrencyRateService {

  ExchangeRateDto getExchangeCurrencyRateWithSave(String from, String to, String amount);

  Set<String> getAllCurrencies();
}
