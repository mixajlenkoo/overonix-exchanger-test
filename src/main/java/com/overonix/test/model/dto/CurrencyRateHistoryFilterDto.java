package com.overonix.test.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class CurrencyRateHistoryFilterDto {

  private String from;
  private String to;
  private Integer amount;
  private Set<String> fromCurrencyCodes;
  private Set<String> toCurrencyCodes;
}
