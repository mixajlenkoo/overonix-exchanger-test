package com.overonix.test.controller;

import com.overonix.test.model.dto.CurrencyHistoryDto;
import com.overonix.test.model.dto.ExchangeRateDto;
import com.overonix.test.service.CurrencyRateService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static com.overonix.test.constants.Url.ALL_CURRENCY_CODES;
import static com.overonix.test.constants.Url.CONVERT;
import static com.overonix.test.constants.Url.CURRENCIES;
import static com.overonix.test.constants.Url.CURRENCY_HISTORY;
import static com.overonix.test.utils.TestUtils.getTestFixtureAsObject;
import static com.overonix.test.utils.TestUtils.getTestFixtureAsSetOfObjects;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CurrencyRateController.class)
public class CurrencyRateControllerIT {

  private static final String UAH = "UAH";
  private static final String USD = "USD";
  private static final String AMOUNT = "2000";
  private static final String AMOUNT_PARAM_NAME = "amount";
  private static final String FROM_PARAM_NAME = "from";
  private static final String TO_PARAM_NAME = "to";
  private static final String TO_CURRENCY_PARAM_NAME = "toCurrencyCodes";
  private static final String FROM_CURRENCY_PARAM_NAME = "fromCurrencyCodes";
  private static final String TEST_DATE = "2022-09-29";
  private static final String JSON_EXCHANGE_RATE_DTO_PATH = "json/ExchangeRateDto.json";
  private static final String JSON_CURRENCIES_PATH = "json/AllCurrencies.json";
  private static final String JSON_CURRENCY_HISTORY_DTO_PATH = "json/CurrencyHistoryDto.json";

  @MockBean private CurrencyRateService currencyRateService;
  @Autowired private MockMvc mockMvc;

  private ExchangeRateDto exchangeRateDto;
  private Set<String> currencies;
  private CurrencyHistoryDto currencyHistoryDto;

  @Before
  public void init() {
    exchangeRateDto = getTestFixtureAsObject(JSON_EXCHANGE_RATE_DTO_PATH, ExchangeRateDto.class);
    currencies = getTestFixtureAsSetOfObjects(JSON_CURRENCIES_PATH, String.class);
    currencyHistoryDto =
        getTestFixtureAsObject(JSON_CURRENCY_HISTORY_DTO_PATH, CurrencyHistoryDto.class);
  }

  @Test
  public void convertCurrenciesTest_OK() throws Exception {
    // GIVEN
    when(currencyRateService.getExchangeCurrencyRateWithSave(USD, UAH, AMOUNT))
        .thenReturn(exchangeRateDto);

    // WHEN
    String actual =
        mockMvc
            .perform(
                post(CURRENCIES + CONVERT)
                    .param(FROM_PARAM_NAME, USD)
                    .param(TO_PARAM_NAME, UAH)
                    .param(AMOUNT_PARAM_NAME, AMOUNT))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    // THEN
    verify(currencyRateService)
        .getExchangeCurrencyRateWithSave(anyString(), anyString(), anyString());
    assertTrue(actual.contains(String.valueOf(exchangeRateDto.getAmount())));
  }

  @Test
  public void getAllCurrenciesTest_OK() throws Exception {
    // GIVEN
    when(currencyRateService.getAllCurrencies()).thenReturn(currencies);

    // WHEN
    String actual =
        mockMvc
            .perform(get(CURRENCIES + ALL_CURRENCY_CODES))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    // THEN
    verify(currencyRateService).getAllCurrencies();
    assertTrue(actual.contains(USD));
  }

  @Test
  public void getCurrencyHistoryTest_OK() throws Exception {
    // GIVEN
    when(currencyRateService.getCurrencyRateHistoryByFilter(any())).thenReturn(currencyHistoryDto);

    // WHEN
    String actual =
        mockMvc
            .perform(
                get(CURRENCIES + CURRENCY_HISTORY)
                    .param(FROM_PARAM_NAME, TEST_DATE)
                    .param(TO_PARAM_NAME, TEST_DATE)
                    .param(AMOUNT_PARAM_NAME, AMOUNT)
                    .param(FROM_CURRENCY_PARAM_NAME, USD)
                    .param(TO_CURRENCY_PARAM_NAME, UAH))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    // THEN
    verify(currencyRateService).getCurrencyRateHistoryByFilter(any());
    assertTrue(actual.contains(USD));
    assertTrue(actual.contains(UAH));
  }
}
