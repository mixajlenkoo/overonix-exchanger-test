package com.overonix.test.service;

import com.overonix.test.model.dto.CurrencyHistoryDto;
import com.overonix.test.model.dto.CurrencyRateHistoryFilterDto;
import com.overonix.test.model.dto.ExchangeRateDto;

import java.util.Set;

public interface CurrencyRateService {

  ExchangeRateDto getExchangeCurrencyRateWithSave(String from, String to, String amount);

  Set<String> getAllCurrencies();

  CurrencyHistoryDto getCurrencyRateHistoryByFilter(CurrencyRateHistoryFilterDto filters);
}
