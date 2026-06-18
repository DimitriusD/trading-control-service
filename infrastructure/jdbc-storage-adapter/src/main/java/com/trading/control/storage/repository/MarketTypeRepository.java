package com.trading.control.storage.repository;

import com.trading.control.storage.entity.MarketTypeEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface MarketTypeRepository extends ListCrudRepository<MarketTypeEntity, Long> {

    Optional<MarketTypeEntity> findByCode(String code);
}
