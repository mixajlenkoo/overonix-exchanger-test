package com.overonix.test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRateHistoryFilterDto {

  private String from;
  private String to;
  private String currencySystem;
  private Set<String> currencyCodes;
}
