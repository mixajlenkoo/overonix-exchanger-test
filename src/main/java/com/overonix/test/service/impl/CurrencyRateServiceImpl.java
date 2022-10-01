package com.overonix.test.service.impl;

import com.overonix.test.converter.ExchangeRateConverter;
import com.overonix.test.model.ExchangeRate;
import com.overonix.test.model.dto.CurrencyHistoryDto;
import com.overonix.test.model.dto.CurrencyRateHistoryFilterDto;
import com.overonix.test.model.dto.ExchangeRateDto;
import com.overonix.test.repository.ExchangeRateRepository;
import com.overonix.test.service.CurrencyRateService;
import com.overonix.test.validation.ExchangeRateValidator;
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
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

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
  private static final String NOT_RESPONSE = "API doesn't response. Try again later.";

  private final ExchangeRateRepository exchangeRateRepository;
  private final ExchangeRateConverter exchangeRateConverter;
  private final ExchangeRateValidator exchangeRateValidator;

  @Value("${api.layer.key}")
  private String apiKey;

  @Value("${api.layer.convert.url}")
  private String convertUrl;

  @Value("${api.layer.all.currencies.url}")
  private String allCurrenciesURL;

  @Override
  public ExchangeRateDto getExchangeCurrencyRateWithSave(String from, String to, String amount) {
    exchangeRateValidator.validateExchangeData(from, to, amount, getAllCurrencies());
    ExchangeRate exchangeRateResult = getRateFromApiAndSaveToDb(from, to, amount);
    log.info("Exchange currency rate is received.");
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
      jsonObject = new JSONObject(requireNonNull(response.body()).string());
      currencies = jsonObject.getJSONObject(SYMBOLS).keySet();
    } catch (IOException e) {
      log.error(NOT_RESPONSE);
    }
    log.info("All existing currencies are received");

    return currencies;
  }

  @Override
  public CurrencyHistoryDto getCurrencyRateHistoryByFilter(CurrencyRateHistoryFilterDto filters) {
    if (isFilterExists(filters)) {
      return getCurrencyRateHistoryByDateRange(filters);
    }

    List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();
    LocalDate firstRate = getFirstRateDateIfExists(exchangeRates);
    LocalDate lastRate = getLastRateDateIfExists(exchangeRates);
    List<ExchangeRateDto> exchangeRateDtos = getExchangeRateDtos(exchangeRates);
    log.info("Currency rate history is received");

    return CurrencyHistoryDto.builder()
        .from(firstRate)
        .to(lastRate)
        .exchangeRateDtos(exchangeRateDtos)
        .build();
  }

  private boolean isFilterExists(CurrencyRateHistoryFilterDto filters) {
    return !(isNull(filters.getFrom())
        && isNull(filters.getTo())
        && isNull(filters.getFromCurrencyCodes())
        && isNull(filters.getToCurrencyCodes())
        && isNull(filters.getAmount()));
  }

  private LocalDate getFirstRateDateIfExists(List<ExchangeRate> exchangeRates) {
    LocalDate firstRate = LocalDate.now();
    if (exchangeRates.stream().findFirst().isPresent()) {
      firstRate = exchangeRates.get(0).getDate();
    }

    return firstRate;
  }

  private LocalDate getLastRateDateIfExists(List<ExchangeRate> exchangeRates) {
    LocalDate lastRate = LocalDate.now();
    if (!exchangeRates.isEmpty()) {
      lastRate = exchangeRates.get(exchangeRates.size() - 1).getDate();
    }

    return lastRate;
  }

  private CurrencyHistoryDto getCurrencyRateHistoryByDateRange(
      CurrencyRateHistoryFilterDto filters) {
    LocalDate from = null;
    LocalDate to = null;
    if (nonNull(filters.getFrom()) && nonNull(filters.getTo())) {
      from = LocalDate.parse(filters.getFrom());
      to = LocalDate.parse(filters.getTo());
    }

    List<ExchangeRate> exchangeRates =
        exchangeRateRepository.findAllByFilter(
            from,
            to,
            filters.getAmount(),
            filters.getFromCurrencyCodes(),
            filters.getToCurrencyCodes());

    List<ExchangeRateDto> exchangeRateDtos = getExchangeRateDtos(exchangeRates);
    log.info("Currency rate history with filters is received");

    return CurrencyHistoryDto.builder()
        .from(from)
        .to(to)
        .exchangeRateDtos(exchangeRateDtos)
        .build();
  }

  @NotNull
  private List<ExchangeRateDto> getExchangeRateDtos(List<ExchangeRate> exchangeRates) {
    return exchangeRates.stream().map(exchangeRateConverter::toDto).collect(toList());
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
      jsonObject = new JSONObject(requireNonNull(response.body()).string());

      exchangeRateResult = createExchangeRateEntity(jsonObject);
      exchangeRateRepository.save(exchangeRateResult);
      log.info("The operation with the exchange rate was saved to DB.");

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
