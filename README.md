
# 🐱 Cat Lovers

A modern, offline-first Android application designed to explore cat breeds, filter with ease, and manage favorites. This project demonstrates best practices in modern Android development using **Jetpack Compose**, **Clean Architecture**, and **Reactive Streams**.

---

## 📸 Preview
<p style="text-align: center;">
  <img src="https://github.com/user-attachments/assets/7e252470-f855-4917-8d0a-bc27d8a75b1b" width="300" alt="App Demo">
</p>

---

## 🚀 Key Features
* **🐾 Breed Explorer**: Browse an extensive list of cat breeds with high-quality images.
* **🔍 Smart Search**: Instant local-first search with case-insensitive filtering.
* **❤️ Favorites System**: Mark cats as favorites with reactive UI updates.
* **📡 Offline Support**: Full access to previously loaded data thanks to local caching.
* **🌙 Modern UI**: Built entirely with Material 3 and adaptive layouts.

---

## 🏗 Architecture & Strategies

### 🏛 Layered Design
* **Data Layer**: Handles API communication and Room persistence.
* **Domain Layer**: Contains business logic and repository interfaces (Clean Architecture).
* **UI Layer**: Uses MVVM with **StateFlow** and **Jetpack Compose** for a unidirectional data flow (UDF).

### 📦 Data & Caching
* **Paging 3 + RemoteMediator**: Implemented `CatBreedRemoteMediator` to coordinate remote API calls with a Room local cache, ensuring a seamless scroll experience even while offline.
* **Result Wrapper**: Custom `Result<T>` (Success/Error/NetworkError) to distinguish error types and provide specific user feedback.
* **AuthHeaderInterceptor**: Injects the `x-api-key` header globally via an OkHttp interceptor for clean network calls.

### ⚡ Search & Performance
* **Local-First Search**: Uses Room `PagingSource` with `LIKE COLLATE NOCASE` to avoid unnecessary API calls and provide instant results.
* **Efficient Favorites**: SQL `LEFT JOIN` in `CatBreedsDao` includes the `isFavorite` flag in a single query, eliminating N+1 lookup performance issues.

### ⚛️ Reactive UI
* **Kotlin Flow**: Utilizes `Flow<PagingData>` for lists and `flatMapLatest` for reactive detail fetching.
* **Retry Pattern**: Built-in retry logic using `MutableStateFlow` triggers to recover from network failures gracefully.
* **Composable Separation**: Distinction between **Container** (stateful) and **Content** (stateless) composables for better testability and Preview support.

---

## 🛠 Tech Stack
* **Language**: Kotlin
* **UI**: Jetpack Compose (Material 3)
* **Dependency Injection**: Hilt
* **Database**: Room
* **Network**: Retrofit + OkHttp
* **Pagination**: Paging 3
* **Image Loading**: Coil
* **Navigation**: Type-safe Jetpack Navigation

---

## ⚖️ Key Trade-offs
* **Local-First Search**: Prioritized UX speed and API quota conservation. This is balanced by a refresh behavior that ensures data remains up-to-date.
* **RemoteMediator**: Leveraged the official Paging 3 mediator to handle complex edge cases in database-backed pagination that custom solutions often miss.

---

## 🛠 Setup
1. Clone the repository: `git clone https://github.com/alexnavarro/cat-lovers`
2. Get your API Key at [The Cat API](https://thecatapi.com/).
3. (Optional) Add your key to `local.properties` or update the Interceptor.
4. Build and Run!

---
**Developed by [Alexandre Navarro](https://github.com/alexnavarro)**