# Cat Lovers — Development Strategies

## Architecture
- **Layered design**: Data, Domain, UI layers for testability and separation of concerns.
- **Dependency Injection**: Hilt modules for database, DAOs, and repository bindings.

## Data & Caching
- **Paging 3 + RemoteMediator**: `CatBreedRemoteMediator` coordinates remote API calls with Room local cache for offline support.
- **Result wrapper**: `Result<T>` (Success/Error/NetworkError) to distinguish error types in ViewModels.
- **AuthHeaderInterceptor**: Injects `x-api-key` header globally via OkHttp interceptor.

## Search & Performance
- **Local-first search**: Room `PagingSource` with `LIKE COLLATE NOCASE` avoids unnecessary API calls.
- **Efficient favorites**: SQL `LEFT JOIN` in `CatBreedsDao` includes `isFavorite` flag in single query (no N+1 lookups).

## Reactive UI
- **Kotlin Flow**: `Flow<PagingData<>>` for reactive pagination, suspend functions for single-shot detail fetches.
- **Retry pattern**: `MutableStateFlow` trigger + `flatMapLatest` re-fetches detail data on user retry.
- **Composable separation**: Container (Scaffold, state) vs Content (stateless UI) for testability.
- **Type-safe navigation**: Sealed `AppNavKey` with serializable routes.

## Key Trade-offs
- Local-first search improves UX and reduces API quota; mitigated by refresh behavior.
- `RemoteMediator` leverages battle-tested pagination over custom solutions.