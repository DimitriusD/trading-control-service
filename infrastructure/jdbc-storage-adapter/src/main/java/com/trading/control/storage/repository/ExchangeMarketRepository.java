package com.trading.control.storage.repository;

import com.trading.control.storage.entity.ExchangeMarketEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeMarketRepository extends ListCrudRepository<ExchangeMarketEntity, Long> {

    Optional<ExchangeMarketEntity> findByExchangeIdAndMarketTypeId(Long exchangeId, Long marketTypeId);

    List<ExchangeMarketEntity> findByExchangeId(Long exchangeId);
}
