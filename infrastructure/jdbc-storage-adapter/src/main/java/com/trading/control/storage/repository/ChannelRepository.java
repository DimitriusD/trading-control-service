package com.trading.control.storage.repository;

import com.trading.control.storage.entity.ChannelEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface ChannelRepository extends ListCrudRepository<ChannelEntity, Long> {

    Optional<ChannelEntity> findByCode(String code);
}
