package com.overonix.test.controller;

import com.overonix.test.model.dto.ExchangeRateDto;
import com.overonix.test.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class CurrencyRateController {

  private final CurrencyRateService currencyRateService;

  @PostMapping("/convert")
  public ExchangeRateDto convertCurrencies(
      @RequestParam String from, @RequestParam String to, @RequestParam String amount) {

    return currencyRateService.getExchangeCurrencyRateWithSave(from, to, amount);
  }

  @GetMapping("/allCurrencies")
  public Set<String> getAllCurrencies() {
    return currencyRateService.getAllCurrencies();
  }
}
