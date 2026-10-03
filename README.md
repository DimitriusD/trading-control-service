# Trading Control Service

Backend for the trading UI (BFF): the UI talks only to this service. It serves the market catalog and
instrument search by proxying `market-catalog-service`, and manages stream configurations: resolves the
instrument and validates channels against the catalog, then forwards the stream spec to
`market-data-service`, which runs the streams.

The service has no database: the catalog lives in `market-catalog-service`, stream state in `market-data-service`.

Hexagonal layout (ports & adapters), Gradle multi-module.

## Modules

| Module | Purpose |
|--------|---------|
| `application` | Domain models, input ports `StreamService` / `MarketCatalogService`, output ports `MarketCatalogPort` / `MarketDataStreamControlPort`, their implementations and `StreamCommandValidator`. No framework dependencies. |
| `infrastructure/app` | Spring Boot entrypoint and wiring (`InfrastructureConfig`). |
| `infrastructure/rest-api` | `StreamsController`, `CatalogController`, `InstrumentsController` and MapStruct mappers over interfaces generated from the contract. |
| `infrastructure/rest-api/trading-control-service-open-api` | The OpenAPI contract, published as `com.trading.contracts:trading-control-service-openapi`. |
| `infrastructure/market-catalog-client` | `MarketCatalogAdapter`: client generated from `market-catalog-service-openapi`. |
| `infrastructure/market-data-client` | `MarketDataStreamControlAdapter`: client generated from `market-data-service-openapi`. |

## API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/catalog` | Exchanges → markets → channels → param rules (proxied from the catalog) |
| `GET` | `/api/v1/instruments?exchangeCode&marketCode&searchText&baseAssetCode&quoteAssetCode&limit&cursor` | Instrument search; `limit` 1–200 (default 50), pass `nextCursor` back as `cursor` |
| `GET` | `/api/v1/instruments/{instrumentId}` | One instrument; `|` in the id must be URL-encoded (`%7C`) |
| `GET` | `/api/v1/streams` | List configured streams |
| `POST` | `/api/v1/streams` | Create a stream: `instrumentId`, `desiredState` (`ENABLED` / `DISABLED`), `channels` |
| `GET` | `/api/v1/streams/{streamId}` | Get a stream |
| `PATCH` | `/api/v1/streams/{streamId}` | Update the desired spec |
| `DELETE` | `/api/v1/streams/{streamId}` | Delete a stream |

```bash
curl -X POST http://localhost:8095/api/v1/streams -H 'Content-Type: application/json' \
  -d '{"instrumentId":"BINANCE|SPOT|BTC|USDT","desiredState":"ENABLED","channels":[{"code":"TRADE"}]}'
```

Errors use one shape: `{"error", "message", "timestamp"}` (UTC). Unknown instrument → 404, unsupported
channel or invalid param → 400, downstream down or its circuit breaker open → 503.

## Downstream services

| Service | Used for | Resilience4j |
|---------|----------|--------------|
| `market-catalog-service` (8097) | `GET /catalog`, `GET /instruments`, `GET /instruments/{id}`, `GET /markets/{exchange}/{market}/channel-capabilities` | retry `market-catalog-read`, circuit breaker `market-catalog-service` |
| `market-data-service` (8080) | stream CRUD | retry `market-data-read`, circuit breaker `market-data-service` |

Both clients are generated at build time from contract JARs (`openapi` configuration, extracted to
`build/openapi/contracts`, generated into `build/generated`; never committed). Generated classes stay inside
their client module; the application layer only sees the output ports.

| Contract | Version |
|----------|---------|
| `com.trading.contracts:market-catalog-service-openapi` | `0.1.0-SNAPSHOT` |
| `com.trading.contracts:market-data-service-openapi` | `1.0.0-SNAPSHOT` |

Contracts resolve from Maven Local first, then GitHub Packages (`dimitriusd/trading-contracts`), which needs
a PAT classic with `read:packages` in `~/.gradle/gradle.properties` (never commit it):

```properties
gpr.user=DimitriusD
gpr.key=<PAT>
```

After a contract change in a downstream service, publish it there and refresh here:

```bash
./gradlew :infrastructure:rest-api:market-catalog-service-open-api:publishToMavenLocal   # in market-catalog-service
./gradlew build --refresh-dependencies                                                  # here
```

To move to a new contract version, bump it in the `openapi(...)` dependency of the client module.

## Run locally

Whole stack from published images (Postgres + catalog + control), from `C:\Users\User\Trading`:

```bash
docker compose up -d
```

market-data-service is not part of that stack; control calls it on the host at `http://host.docker.internal:8080`.

From sources, with `market-catalog-service` and `market-data-service` running:

```bash
./gradlew build
./gradlew :infrastructure:app:bootRun
```

## Publishing the contract

```bash
./gradlew :infrastructure:rest-api:trading-control-service-open-api:publishToMavenLocal   # local consumers
./gradlew :infrastructure:rest-api:trading-control-service-open-api:publish               # GitHub Packages (gpr.user / gpr.key)
```

`trading-ui` generates its API client from this contract.

## Container image

`.github/workflows/ci.yml` runs `./gradlew build`, then builds the image and pushes it to
`ghcr.io/dimitriusd/trading-control-service`:

| Trigger | Tags |
|---|---|
| push to `master` | `latest`, `sha-<short>` |
| tag `v1.2.3` | `1.2.3`, `1.2`, `sha-<short>` |
| pull request | built only, not pushed |

The build needs the contracts from GitHub Packages, so the repository has a `GPR_TOKEN` secret
(PAT classic, `read:packages`). Contracts must be published with `publish`; Maven Local is not visible in
CI or Docker. Local image build passes the token as a BuildKit secret (it does not end up in image layers):

```bash
GPR_USER=DimitriusD GPR_KEY=<PAT> docker build \
  --secret id=gpr_user,env=GPR_USER --secret id=gpr_key,env=GPR_KEY \
  -t trading-control-service:local .
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `APP_PORT` | `8095` | HTTP port |
| `MARKET_CATALOG_SERVICE_BASE_URL` | `http://localhost:8097` | market-catalog-service base URL |
| `MARKET_CATALOG_SERVICE_CONNECT_TIMEOUT` / `_READ_TIMEOUT` | `2s` / `5s` | catalog client timeouts |
| `MARKET_DATA_SERVICE_BASE_URL` | `http://localhost:8080` | market-data-service base URL |
| `MARKET_DATA_SERVICE_CONNECT_TIMEOUT` / `_READ_TIMEOUT` | `2s` / `5s` | market data client timeouts |
