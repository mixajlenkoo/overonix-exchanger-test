package com.overonix.test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateDto {

  private String from;
  private String to;
  private double currencyRate;
  private double result;
  private int amount;
}
