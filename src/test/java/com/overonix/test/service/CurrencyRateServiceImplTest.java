package com.overonix.test.service;

import com.overonix.test.converter.ExchangeRateConverter;
import com.overonix.test.model.ExchangeRate;
import com.overonix.test.model.dto.CurrencyHistoryDto;
import com.overonix.test.model.dto.CurrencyRateHistoryFilterDto;
import com.overonix.test.model.dto.ExchangeRateDto;
import com.overonix.test.repository.ExchangeRateRepository;
import com.overonix.test.service.impl.CurrencyRateServiceImpl;
import com.overonix.test.validation.ExchangeRateValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Set;

import static com.overonix.test.utils.TestUtils.getTestFixtureAsObject;
import static com.overonix.test.utils.TestUtils.getTestFixtureAsSetOfObjects;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyRateServiceImplTest {

  private static final String UAH = "UAH";
  private static final String USD = "USD";
  private static final String AMOUNT = "2000";
  private static final String API_KEY = "apiKey";
  private static final String CONVERT_URL_FIELD_NAME = "convertUrl";
  private static final String ALL_CURRENCIES_URL_FIELD_NAME = "allCurrenciesURL";
  private static final String TEST_ALL_CURRENCIES_URL =
      "https://api.apilayer.com/exchangerates_data/symbols";
  private static final String TEST_CONVERT_URL =
      "https://api.apilayer.com/exchangerates_data/convert?to=%s&from=%s&amount=%s";
  private static final String TEST_KEY = "XIY1Sm5zX8gduJGC4sc8DfUMox4tCStb";
  private static final String JSON_EXCHANGE_RATE_DTO_PATH = "json/ExchangeRateDto.json";
  private static final String JSON_EXCHANGE_RATE_PATH = "json/ExchangeRate.json";
  private static final String JSON_CURRENCIES_PATH = "json/AllCurrencies.json";
  private static final String JSON_CURRENCY_HISTORY_DTO_PATH = "json/CurrencyHistoryDto.json";
  private static final String JSON_CURRENCY_RATE_HISTORY_FILTER_DTO_PATH =
      "json/CurrencyRateHistoryFilterDto.json";

  @InjectMocks private CurrencyRateServiceImpl currencyRateService;
  @Mock private ExchangeRateRepository exchangeRateRepository;
  @Mock private ExchangeRateConverter exchangeRateConverter;
  @Mock private ExchangeRateValidator exchangeRateValidator;

  private ExchangeRateDto exchangeRateDto;
  private ExchangeRate exchangeRate;
  private Set<String> currencies;
  private CurrencyRateHistoryFilterDto currencyRateHistoryFilterDto;
  private CurrencyHistoryDto currencyHistoryDto;

  @Before
  public void init() {
    exchangeRateDto = getTestFixtureAsObject(JSON_EXCHANGE_RATE_DTO_PATH, ExchangeRateDto.class);
    exchangeRate = getTestFixtureAsObject(JSON_EXCHANGE_RATE_PATH, ExchangeRate.class);
    setField(currencyRateService, API_KEY, TEST_KEY);
    setField(currencyRateService, CONVERT_URL_FIELD_NAME, TEST_CONVERT_URL);
    setField(currencyRateService, ALL_CURRENCIES_URL_FIELD_NAME, TEST_ALL_CURRENCIES_URL);
    currencies = getTestFixtureAsSetOfObjects(JSON_CURRENCIES_PATH, String.class);
    currencyRateHistoryFilterDto =
        getTestFixtureAsObject(
            JSON_CURRENCY_RATE_HISTORY_FILTER_DTO_PATH, CurrencyRateHistoryFilterDto.class);
    currencyHistoryDto =
        getTestFixtureAsObject(JSON_CURRENCY_HISTORY_DTO_PATH, CurrencyHistoryDto.class);
  }

  @Test
  public void shouldGetExchangeCurrencyRateWithSaveTest() {
    // GIVEN
    doNothing()
        .when(exchangeRateValidator)
        .validateExchangeData(anyString(), anyString(), anyString(), any());
    when(exchangeRateRepository.save(any())).thenReturn(exchangeRate);
    when(exchangeRateConverter.toDto(any())).thenReturn(exchangeRateDto);

    // WHEN
    ExchangeRateDto actual = currencyRateService.getExchangeCurrencyRateWithSave(USD, UAH, AMOUNT);

    // THEN
    assertEquals(exchangeRateDto, actual);
  }

  @Test
  public void shouldGetAllCurrenciesTest() {
    // WHEN
    Set<String> actual = currencyRateService.getAllCurrencies();

    // THEN
    assertEquals(currencies, actual);
  }

  @Test
  public void shouldGetCurrencyRateHistoryByFilterWithFiltersTest() {
    // GIVEN
    when(exchangeRateRepository.findAllByFilter(any(), any(), anyInt(), any(), any()))
        .thenReturn(singletonList(exchangeRate));
    when(exchangeRateConverter.toDto(any())).thenReturn(exchangeRateDto);

    // WHEN
    CurrencyHistoryDto actual =
        currencyRateService.getCurrencyRateHistoryByFilter(currencyRateHistoryFilterDto);

    // THEN
    assertEquals(currencyHistoryDto, actual);
  }

  @Test
  public void shouldGetCurrencyRateHistoryByFilterWithoutFiltersTest() {
    // GIVEN
    currencyHistoryDto.setFrom(LocalDate.now());
    currencyHistoryDto.setTo(LocalDate.now());
    when(exchangeRateRepository.findAll()).thenReturn(singletonList(exchangeRate));
    when(exchangeRateConverter.toDto(any())).thenReturn(exchangeRateDto);

    // WHEN
    CurrencyHistoryDto actual =
        currencyRateService.getCurrencyRateHistoryByFilter(new CurrencyRateHistoryFilterDto());

    // THEN
    assertEquals(currencyHistoryDto, actual);
  }
}
