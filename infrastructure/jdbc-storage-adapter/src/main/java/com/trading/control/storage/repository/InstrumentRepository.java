package com.trading.control.storage.repository;

import com.trading.control.storage.entity.InstrumentEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface InstrumentRepository extends ListCrudRepository<InstrumentEntity, Long> {

    Optional<InstrumentEntity> findByInstrumentId(String instrumentId);

    List<InstrumentEntity> findByExchangeMarketId(Long exchangeMarketId);
}
