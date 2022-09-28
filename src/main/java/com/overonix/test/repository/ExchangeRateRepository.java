package com.overonix.test.repository;

import com.overonix.test.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, String> {

  //  @Query("SELECT er FROM ExchangeRate er" + " WHERE er.date BETWEEN :from AND :to")
  List<ExchangeRate> findAllByDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
  //  List<ExchangeRate> findByParams(
  //      @Param("from") LocalDate from,
  //      @Param("to") LocalDate to,
  //      @Param("api") String api,
  //      @Param("currency") String currencies);
}
