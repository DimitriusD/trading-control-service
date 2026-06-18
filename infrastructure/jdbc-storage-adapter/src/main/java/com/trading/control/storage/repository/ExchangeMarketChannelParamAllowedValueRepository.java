package com.trading.control.storage.repository;

import com.trading.control.storage.entity.ExchangeMarketChannelParamAllowedValueEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface ExchangeMarketChannelParamAllowedValueRepository
        extends ListCrudRepository<ExchangeMarketChannelParamAllowedValueEntity, Long> {

    List<ExchangeMarketChannelParamAllowedValueEntity> findByChannelParamId(Long channelParamId);
}
