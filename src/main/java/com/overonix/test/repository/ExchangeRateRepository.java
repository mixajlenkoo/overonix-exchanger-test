package com.overonix.test.repository;

import com.overonix.test.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, String> {

  @Query(
      "SELECT er FROM ExchangeRate er"
          + " WHERE (:from IS NULL AND :to IS NULL OR er.date BETWEEN :from AND :to)"
          + " AND (:fromCurrencies IS NULL OR er.fromCurrency IN :fromCurrencies)"
          + " AND (:toCurrencies IS NULL OR er.toCurrency IN :toCurrencies)"
          + " AND (:amount IS NULL OR er.amount =:amount)")
  List<ExchangeRate> findAllByFilter(
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("amount") Integer amount,
      @Param("fromCurrencies") Set<String> fromCurrencies,
      @Param("toCurrencies") Set<String> toCurrencies);
}
