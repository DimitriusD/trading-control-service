package com.trading.control.storage.catalog;

import com.trading.control.application.domain.model.catalog.Exchange;
import com.trading.control.application.domain.model.catalog.ExchangeMarketLink;
import com.trading.control.application.domain.model.catalog.MarketType;
import com.trading.control.application.port.output.CatalogStorePort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JDBC-backed implementation of {@link CatalogStorePort}, reading the catalog
 * from the {@code exchanges}, {@code market_types} and {@code exchange_markets} tables.
 */
@Repository
public class JdbcCatalogStore implements CatalogStorePort {

    private static final RowMapper<Exchange> EXCHANGE_MAPPER = (rs, rowNum) -> Exchange.builder()
            .code(rs.getString("code"))
            .name(rs.getString("name"))
            .enabled(rs.getBoolean("enabled"))
            .build();

    private static final RowMapper<MarketType> MARKET_TYPE_MAPPER = (rs, rowNum) -> MarketType.builder()
            .code(rs.getString("code"))
            .name(rs.getString("name"))
            .build();

    private static final RowMapper<ExchangeMarketLink> LINK_MAPPER = (rs, rowNum) -> ExchangeMarketLink.builder()
            .exchangeCode(rs.getString("exchange_code"))
            .marketTypeCode(rs.getString("market_type_code"))
            .enabled(rs.getBoolean("enabled"))
            .build();

    private final JdbcTemplate jdbc;

    public JdbcCatalogStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Exchange> findExchanges() {
        return jdbc.query("SELECT code, name, enabled FROM exchanges ORDER BY code", EXCHANGE_MAPPER);
    }

    @Override
    public List<MarketType> findMarketTypes() {
        return jdbc.query("SELECT code, name FROM market_types ORDER BY code", MARKET_TYPE_MAPPER);
    }

    @Override
    public List<ExchangeMarketLink> findExchangeMarkets() {
        return jdbc.query(
                """
                SELECT e.code  AS exchange_code,
                       mt.code AS market_type_code,
                       em.enabled
                FROM exchange_markets em
                         JOIN exchanges e ON e.id = em.exchange_id
                         JOIN market_types mt ON mt.id = em.market_type_id
                ORDER BY e.code, mt.code
                """,
                LINK_MAPPER);
    }
}
