package com.overonix.test.repository;

import com.overonix.test.model.ExchangeRate;
import org.springframework.data.repository.CrudRepository;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, String> {}
