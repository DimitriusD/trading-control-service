package com.trading.control.storage.repository;

import com.trading.control.storage.entity.ExchangeEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface ExchangeRepository extends ListCrudRepository<ExchangeEntity, Long> {

    Optional<ExchangeEntity> findByCode(String code);
}
