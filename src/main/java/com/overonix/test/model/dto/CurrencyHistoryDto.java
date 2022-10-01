package com.overonix.test.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyHistoryDto {

  private LocalDate from;
  private LocalDate to;
  private List<ExchangeRateDto> exchangeRateDtos;
}
