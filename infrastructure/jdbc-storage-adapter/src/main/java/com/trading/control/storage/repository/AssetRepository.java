package com.trading.control.storage.repository;

import com.trading.control.storage.entity.AssetEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface AssetRepository extends ListCrudRepository<AssetEntity, Long> {

    Optional<AssetEntity> findByCode(String code);
}
