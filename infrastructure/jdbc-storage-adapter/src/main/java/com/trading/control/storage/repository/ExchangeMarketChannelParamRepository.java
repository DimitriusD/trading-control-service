package com.trading.control.storage.repository;

import com.trading.control.storage.entity.ExchangeMarketChannelParamEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface ExchangeMarketChannelParamRepository extends ListCrudRepository<ExchangeMarketChannelParamEntity, Long> {

    List<ExchangeMarketChannelParamEntity> findByExchangeMarketChannelId(Long exchangeMarketChannelId);
}
