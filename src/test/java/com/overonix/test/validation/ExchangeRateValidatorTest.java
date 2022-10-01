package com.overonix.test.validation;

import com.overonix.test.exception.InvalidCurrencyCodeException;
import com.overonix.test.validation.impl.ExchangeRateValidatorImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeRateValidatorTest {

  private static final String FROM = "USD";
  private static final String TO = "UAH";
  private static final String INVALID_TO = "USDT";
  private static final String AMOUNT = "1000";
  private static final String INVALID_AMOUNT = "one thousand";
  private static final Set<String> CURRENCIES = Set.of("USD", "UAH");

  @InjectMocks private ExchangeRateValidatorImpl exchangeRateValidator;

  @Test
  public void shouldThrowExceptionWhenCurrencyCodeIsNotExists() {
    // WHEN
    Throwable thrown =
        catchThrowable(
            () -> exchangeRateValidator.validateExchangeData(FROM, INVALID_TO, AMOUNT, CURRENCIES));

    // THEN
    assertThat(thrown).isInstanceOf(InvalidCurrencyCodeException.class);
    assertThat(thrown.getMessage()).isNotBlank();
  }

  @Test
  public void shouldThrowExceptionWhenAmountIsInvalid() {
    // WHEN
    Throwable thrown =
        catchThrowable(
            () -> exchangeRateValidator.validateExchangeData(FROM, TO, INVALID_AMOUNT, CURRENCIES));

    // THEN
    assertThat(thrown).isInstanceOf(NumberFormatException.class);
    assertThat(thrown.getMessage()).isNotBlank();
  }
}
