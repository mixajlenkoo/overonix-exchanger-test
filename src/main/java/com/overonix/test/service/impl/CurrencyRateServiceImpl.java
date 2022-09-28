package com.overonix.test.service.impl;

import com.overonix.test.converter.ExchangeRateConverter;
import com.overonix.test.exception.InvalidCurrencyRateException;
import com.overonix.test.model.ExchangeRate;
import com.overonix.test.model.dto.ExchangeRateDto;
import com.overonix.test.repository.ExchangeRateRepository;
import com.overonix.test.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.emptySet;

@Slf4j
@Service
@PropertySource("classpath:exchangeApi.properties")
@RequiredArgsConstructor
public class CurrencyRateServiceImpl implements CurrencyRateService {

  private static final String QUERY = "query";
  private static final String FROM = "from";
  private static final String TO = "to";
  private static final String AMOUNT = "amount";
  private static final String INFO = "info";
  private static final String RATE = "rate";
  private static final String RESULT = "result";
  private static final String DATE = "date";
  private static final String API_KEY = "apikey";
  private static final String GET = "GET";
  private static final String SYMBOLS = "symbols";
  private static final String INVALID_CURRENCY_RATE_MESSAGE =
      "Currency rate is not valid. Check entered data.";
  private static final String NOT_RESPONSE = "API doesn't response. Try again later.";

  private final ExchangeRateRepository exchangeRateRepository;
  private final ExchangeRateConverter exchangeRateConverter;

  @Value("${api.key}")
  private String apiKey;

  @Value("${api.convert.url}")
  private String convertUrl;

  @Value("${api.all.currencies.url}")
  private String allCurrenciesURL;

  @Override
  public ExchangeRateDto getExchangeCurrencyRateWithSave(String from, String to, String amount) {

    ExchangeRate exchangeRateResult = getRateFromApiAndSaveToDb(from, to, amount);

    return exchangeRateConverter.toDto(exchangeRateResult);
  }

  @Override
  public Set<String> getAllCurrencies() {
    Set<String> currencies = emptySet();
    JSONObject jsonObject;
    Response response;

    OkHttpClient client = new OkHttpClient().newBuilder().build();
    Request request = createRequestToApiCurrencies();

    try {
      response = client.newCall(request).execute();
      jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
      currencies = jsonObject.getJSONObject(SYMBOLS).keySet();
    } catch (IOException e) {
      log.error(NOT_RESPONSE);
    }

    return currencies;
  }

  @NotNull
  private Request createRequestToApiCurrencies() {
    return new Request.Builder()
        .url(allCurrenciesURL)
        .addHeader(API_KEY, apiKey)
        .method(GET, null)
        .build();
  }

  @NotNull
  private ExchangeRate getRateFromApiAndSaveToDb(String from, String to, String amount) {
    Response response;
    JSONObject jsonObject;
    ExchangeRate exchangeRateResult = new ExchangeRate();

    OkHttpClient client = new OkHttpClient().newBuilder().build();
    Request request = createRequestToApiConverter(from, to, amount);

    try {
      response = client.newCall(request).execute();
      jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());

      exchangeRateResult = createExchangeRateEntity(jsonObject);
      exchangeRateRepository.save(exchangeRateResult);
    } catch (IOException e) {
      log.error(NOT_RESPONSE);
    }

    return exchangeRateResult;
  }

  @NotNull
  private Request createRequestToApiConverter(String from, String to, String amount) {
    return new Request.Builder()
        .url(String.format(convertUrl, to, from, amount))
        .addHeader(API_KEY, apiKey)
        .method(GET, null)
        .build();
  }

  private ExchangeRate createExchangeRateEntity(JSONObject jsonObject) {
    if (jsonObject.isEmpty()) {
      throw new InvalidCurrencyRateException(INVALID_CURRENCY_RATE_MESSAGE);
    }
    return ExchangeRate.builder()
        .fromCurrency(jsonObject.getJSONObject(QUERY).getString(FROM))
        .toCurrency(jsonObject.getJSONObject(QUERY).getString(TO))
        .amount(jsonObject.getJSONObject(QUERY).getInt(AMOUNT))
        .rate(jsonObject.getJSONObject(INFO).getDouble(RATE))
        .result(jsonObject.getDouble(RESULT))
        .date(LocalDate.parse(jsonObject.getString(DATE)))
        .build();
  }
}
