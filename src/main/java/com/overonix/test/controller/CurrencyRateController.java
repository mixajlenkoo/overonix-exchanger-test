package com.overonix.test.controller;

import com.overonix.test.model.dto.CurrencyHistoryDto;
import com.overonix.test.model.dto.CurrencyRateHistoryFilterDto;
import com.overonix.test.model.dto.ExchangeRateDto;
import com.overonix.test.service.CurrencyRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static com.overonix.test.constants.Url.ALL_CURRENCY_CODES;
import static com.overonix.test.constants.Url.CONVERT;
import static com.overonix.test.constants.Url.CURRENCIES;
import static com.overonix.test.constants.Url.CURRENCY_HISTORY;

@RestController
@RequiredArgsConstructor
@RequestMapping(CURRENCIES)
public class CurrencyRateController {

  private final CurrencyRateService currencyRateService;

  @PostMapping(CONVERT)
  @Operation(summary = "Convert entered rates for currencies")
  public ExchangeRateDto convertCurrencies(
      @Parameter(name = "from", description = "It's currency to convert") @RequestParam String from,
      @Parameter(name = "to", description = "It's currency to be converted to") @RequestParam
          String to,
      @Parameter(name = "amount", description = "It's amount we convert") @RequestParam
          String amount) {
    return currencyRateService.getExchangeCurrencyRateWithSave(from, to, amount);
  }

  @GetMapping(ALL_CURRENCY_CODES)
  @Operation(summary = "It returns all exists currency codes")
  public Set<String> getAllCurrencies() {
    return currencyRateService.getAllCurrencies();
  }

  @GetMapping(CURRENCY_HISTORY)
  @Operation(
      summary =
          "It return exchange rates history by criteria parameters like: date range, currency code or amount")
  public CurrencyHistoryDto getCurrencyHistory(
      @Parameter(name = "filters", description = "Filters that will be applied to the search")
          CurrencyRateHistoryFilterDto filters) {
    return currencyRateService.getCurrencyRateHistoryByFilter(filters);
  }
}
