package com.overonix.test.converter;

import com.overonix.test.model.ExchangeRate;
import com.overonix.test.model.dto.ExchangeRateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExchangeRateConverter {

  @Mapping(target = "from", expression = "java(exchangeRate.getFromCurrency())")
  @Mapping(target = "to", expression = "java(exchangeRate.getToCurrency())")
  @Mapping(target = "currencyRate", expression = "java(exchangeRate.getRate())")
  ExchangeRateDto toDto(ExchangeRate exchangeRate);
}
