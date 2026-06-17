package com.trading.control.storage.catalog;

import com.trading.control.application.domain.model.Asset;
import com.trading.control.application.domain.model.Channel;
import com.trading.control.application.domain.model.ChannelParam;
import com.trading.control.application.domain.model.ChannelParamValue;
import com.trading.control.application.domain.model.ExchangeMarket;
import com.trading.control.application.domain.model.Instrument;
import com.trading.control.application.domain.model.MarketCatalog;
import com.trading.control.application.domain.model.MarketInstruments;
import com.trading.control.application.domain.model.MarketType;
import com.trading.control.application.port.output.CatalogStorePort;
import com.trading.control.storage.entity.AssetEntity;
import com.trading.control.storage.entity.ChannelEntity;
import com.trading.control.storage.entity.ExchangeEntity;
import com.trading.control.storage.entity.ExchangeMarketChannelEntity;
import com.trading.control.storage.entity.ExchangeMarketChannelParamAllowedValueEntity;
import com.trading.control.storage.entity.ExchangeMarketChannelParamEntity;
import com.trading.control.storage.entity.ExchangeMarketEntity;
import com.trading.control.storage.entity.InstrumentEntity;
import com.trading.control.storage.entity.MarketTypeEntity;
import com.trading.control.storage.repository.AssetRepository;
import com.trading.control.storage.repository.ChannelRepository;
import com.trading.control.storage.repository.ExchangeMarketChannelParamAllowedValueRepository;
import com.trading.control.storage.repository.ExchangeMarketChannelParamRepository;
import com.trading.control.storage.repository.ExchangeMarketChannelRepository;
import com.trading.control.storage.repository.ExchangeMarketRepository;
import com.trading.control.storage.repository.ExchangeRepository;
import com.trading.control.storage.repository.InstrumentRepository;
import com.trading.control.storage.repository.MarketTypeRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class CatalogStore implements CatalogStorePort {

    private final ExchangeRepository exchanges;
    private final MarketTypeRepository marketTypes;
    private final ExchangeMarketRepository exchangeMarkets;
    private final ChannelRepository channels;
    private final ExchangeMarketChannelRepository exchangeMarketChannels;
    private final ExchangeMarketChannelParamRepository channelParams;
    private final ExchangeMarketChannelParamAllowedValueRepository channelParamValues;
    private final InstrumentRepository instruments;
    private final AssetRepository assets;

    public CatalogStore(ExchangeRepository exchanges,
                        MarketTypeRepository marketTypes,
                        ExchangeMarketRepository exchangeMarkets,
                        ChannelRepository channels,
                        ExchangeMarketChannelRepository exchangeMarketChannels,
                        ExchangeMarketChannelParamRepository channelParams,
                        ExchangeMarketChannelParamAllowedValueRepository channelParamValues,
                        InstrumentRepository instruments,
                        AssetRepository assets) {
        this.exchanges = exchanges;
        this.marketTypes = marketTypes;
        this.exchangeMarkets = exchangeMarkets;
        this.channels = channels;
        this.exchangeMarketChannels = exchangeMarketChannels;
        this.channelParams = channelParams;
        this.channelParamValues = channelParamValues;
        this.instruments = instruments;
        this.assets = assets;
    }

    @Override
    @Transactional(readOnly = true)
    public MarketCatalog getMarkets() {
        Map<Long, MarketTypeEntity> marketTypeById = byId(marketTypes.findAll(), MarketTypeEntity::getId);
        Map<Long, ChannelEntity> channelById = byId(channels.findAll(), ChannelEntity::getId);

        Map<Long, List<ExchangeMarketEntity>> marketsByExchange = exchangeMarkets.findAll().stream()
                .collect(Collectors.groupingBy(ExchangeMarketEntity::getExchangeId));
        Map<Long, List<ExchangeMarketChannelEntity>> channelsByMarket = exchangeMarketChannels.findAll().stream()
                .collect(Collectors.groupingBy(ExchangeMarketChannelEntity::getExchangeMarketId));
        Map<Long, List<ExchangeMarketChannelParamEntity>> paramsByChannel = channelParams.findAll().stream()
                .collect(Collectors.groupingBy(ExchangeMarketChannelParamEntity::getExchangeMarketChannelId));
        Map<Long, List<ExchangeMarketChannelParamAllowedValueEntity>> valuesByParam = channelParamValues.findAll().stream()
                .collect(Collectors.groupingBy(ExchangeMarketChannelParamAllowedValueEntity::getChannelParamId));

        MarketCatalog.MarketCatalogBuilder catalog = MarketCatalog.builder();

        exchanges.findAll().stream()
                .sorted(Comparator.comparing(ExchangeEntity::getCode))
                .forEach(exchange -> {
                    ExchangeMarket.ExchangeMarketBuilder market = ExchangeMarket.builder()
                            .code(exchange.getCode())
                            .displayName(exchange.getName())
                            .enabled(exchange.isEnabled());

                    marketsByExchange.getOrDefault(exchange.getId(), List.of()).stream()
                            .sorted(Comparator.comparing(em -> marketTypeById.get(em.getMarketTypeId()).getCode()))
                            .forEach(em -> {
                                MarketTypeEntity mt = marketTypeById.get(em.getMarketTypeId());
                                MarketType.MarketTypeBuilder marketType = MarketType.builder()
                                        .code(mt.getCode())
                                        .displayName(mt.getName())
                                        .enabled(em.isEnabled());

                                channelsByMarket.getOrDefault(em.getId(), List.of()).stream()
                                        .sorted(Comparator.comparing(emc -> channelById.get(emc.getChannelId()).getCode()))
                                        .forEach(emc -> marketType.channel(
                                                toChannel(emc, channelById, paramsByChannel, valuesByParam)));

                                market.marketType(marketType.build());
                            });

                    catalog.exchange(market.build());
                });

        return catalog.build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MarketInstruments> getInstruments(String exchange, String marketType) {
        Optional<ExchangeEntity> exchangeEntity = exchanges.findByCode(exchange);
        Optional<MarketTypeEntity> marketTypeEntity = marketTypes.findByCode(marketType);
        if (exchangeEntity.isEmpty() || marketTypeEntity.isEmpty()) {
            return Optional.empty();
        }

        Optional<ExchangeMarketEntity> exchangeMarket = exchangeMarkets.findByExchangeIdAndMarketTypeId(
                exchangeEntity.get().getId(), marketTypeEntity.get().getId());
        if (exchangeMarket.isEmpty()) {
            return Optional.empty();
        }

        Map<Long, AssetEntity> assetById = byId(assets.findAll(), AssetEntity::getId);

        MarketInstruments.MarketInstrumentsBuilder builder = MarketInstruments.builder()
                .exchange(exchange)
                .marketType(marketType);

        instruments.findByExchangeMarketId(exchangeMarket.get().getId()).stream()
                .sorted(Comparator.comparing(InstrumentEntity::getExchangeSymbol))
                .forEach(instrument -> builder.instrument(toInstrument(instrument, assetById)));

        return Optional.of(builder.build());
    }

    private static Channel toChannel(ExchangeMarketChannelEntity emc,
                                     Map<Long, ChannelEntity> channelById,
                                     Map<Long, List<ExchangeMarketChannelParamEntity>> paramsByChannel,
                                     Map<Long, List<ExchangeMarketChannelParamAllowedValueEntity>> valuesByParam) {
        ChannelEntity channel = channelById.get(emc.getChannelId());
        Channel.ChannelBuilder builder = Channel.builder()
                .code(channel.getCode())
                .name(channel.getName())
                .enabled(emc.isEnabled());

        paramsByChannel.getOrDefault(emc.getId(), List.of()).stream()
                .sorted(Comparator.comparing(ExchangeMarketChannelParamEntity::getParamKey))
                .forEach(param -> {
                    ChannelParam.ChannelParamBuilder paramBuilder = ChannelParam.builder().key(param.getParamKey());
                    valuesByParam.getOrDefault(param.getId(), List.of()).stream()
                            .sorted(Comparator.comparing(ExchangeMarketChannelParamAllowedValueEntity::getSortOrder))
                            .forEach(value -> paramBuilder.value(ChannelParamValue.builder()
                                    .value(value.getValue())
                                    .displayName(value.getDisplayName())
                                    .isDefault(value.isDefault())
                                    .build()));
                    builder.param(paramBuilder.build());
                });

        return builder.build();
    }

    private static Instrument toInstrument(InstrumentEntity instrument, Map<Long, AssetEntity> assetById) {
        return Instrument.builder()
                .instrumentId(instrument.getInstrumentId())
                .baseAsset(toAsset(assetById.get(instrument.getBaseAssetId())))
                .quoteAsset(toAsset(assetById.get(instrument.getQuoteAssetId())))
                .displaySymbol(instrument.getDisplaySymbol())
                .enabled(instrument.isEnabled())
                .build();
    }

    private static Asset toAsset(AssetEntity asset) {
        return asset == null ? null : Asset.builder()
                .code(asset.getCode())
                .name(asset.getName())
                .build();
    }

    private static <T> Map<Long, T> byId(List<T> entities, Function<T, Long> idGetter) {
        return entities.stream().collect(Collectors.toMap(idGetter, Function.identity()));
    }
}
