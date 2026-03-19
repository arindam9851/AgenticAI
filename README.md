# AgenticAI — Android Shopping Assistant

An AI-powered shopping assistant Android app built with **Clean Architecture + MVI**, using **Groq (Llama 3.3)** as the free AI brain, **Firebase Firestore** for product data, and **Cloudinary** for product images.

---

## What This App Does

Users can chat naturally to find products:

- *"Show me electronics under 500 SEK"*
- *"Give me sold out products"*
- *"Best rated fitness gear"*
- *"Compare Yoga Mat and Dumbbell Set"*
- *"Cheapest products in clothing"*

The AI automatically decides which tool to call, queries your real Firestore database, and replies with matching products and images.

---

## Tech Stack

| Layer | Technology | Cost |
|---|---|---|
| AI Brain | Groq API — Llama 3.3 70B | Free (14,400 req/day) |
| Product Data | Firebase Firestore | Free (50k reads/day) |
| Product Images | Cloudinary | Free (25 GB) |
| API Key Storage | Firebase Remote Config | Free |
| UI | Jetpack Compose + Material3 | Free |
| DI | Hilt | Free |
| Architecture | Clean Architecture + MVI | — |

**Total cost: $0**

---

## Project Structure

```
com.example.agenticai/
│
├── domain/                           ← Pure Kotlin, no Android deps
│   ├── model/
│   │   ├── Product.kt                ← Product data model
│   │   └── Message.kt                ← Chat message + Role enum
│   ├── repository/
│   │   └── Repositories.kt           ← Repository interfaces
│   └── usecase/
│       ├── AgenticAIUseCases.kt      ← Data class bundling all 9 use cases
│       ├── GetProductsUseCase.kt
│       ├── SearchProductsUseCase.kt
│       ├── FilterByPriceUseCase.kt
│       ├── FilterByCategoryUseCase.kt
│       ├── FilterByStockUseCase.kt
│       ├── SortByRatingUseCase.kt
│       ├── SortByPriceUseCase.kt
│       ├── CompareProductsUseCase.kt
│       └── GetCategoriesUseCase.kt
│
├── data/                             ← Data sources and implementations
│   ├── model/
│   │   └── ProductDto.kt             ← Firestore DTO with domain mapper
│   ├── source/remote/
│   │   ├── FirestoreProductSource.kt
│   │   ├── RemoteConfigSource.kt
│   │   └── GroqAgentClient.kt        ← Groq API + agentic loop
│   └── repository/
│       └── RepositoryImpl.kt
│
├── presentation/                     ← UI layer
│   ├── chat/
│   │   ├── ChatContract.kt           ← MVI: State, Intent, Effect
│   │   ├── ChatViewModel.kt
│   │   └── ChatScreen.kt
│   ├── component/
│   │   └── Components.kt             ← ChatBubble, ProductCard, Chips
│   └── theme/
│       └── Theme.kt
│
└── core/
    └── di/
        └── AppModule.kt              ← Hilt dependency injection
```

---

## MVI Architecture Flow

```
User types message
        ↓
ChatIntent.SendMessage
        ↓
ChatViewModel.onIntent()
        ↓
GroqAgentClient.chat()
        ↓
Groq API (Llama 3.3) decides which tool to call
        ↓
AgenticAIUseCases executes tool against Firestore data
        ↓
Result returned to Groq → Groq forms final reply
        ↓
ChatState updated → UI re-renders
        ↓
ChatEffect.ScrollToBottom (one-time event)
```

---

## Setup Guide

### Step 1 — Clone the project

```bash
git clone https://github.com/arindam9851/AgenticAI.git
cd AgenticAI
```

---

### Step 2 — Gradle Configuration

#### `libs.versions.toml` — use these exact versions:

```toml
[versions]
agp                      = "9.0.1"
kotlin                   = "2.1.20"
ksp                      = "2.1.20-1.0.32"
hiltAndroid              = "2.51.1"
hiltNavigationCompose    = "1.3.0"
firebaseBom              = "34.0.0"
composeBom               = "2024.06.00"
coilCompose              = "2.7.0"
okhttp                   = "4.12.0"
googleServices           = "4.4.2"
kotlinxCoroutinesAndroid = "1.8.1"

[plugins]
android-application = { id = "com.android.application",             version.ref = "agp" }
kotlin-android      = { id = "org.jetbrains.kotlin.android",        version.ref = "kotlin" }
kotlin-compose      = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp                 = { id = "com.google.devtools.ksp",             version.ref = "ksp" }
hilt                = { id = "com.google.dagger.hilt.android",      version.ref = "hiltAndroid" }
google-services     = { id = "com.google.gms.google-services",      version.ref = "googleServices" }
```



> This is required for AGP 9.0 + KSP compatibility.

---

### Step 3 — Firebase Setup

#### 3a. Create Firebase project
1. Go to **console.firebase.google.com**
2. Click **Add Project** → follow the wizard

#### 3b. Add Android app
1. Firebase Console → **Add App** → Android
2. Enter package name: `com.example.agenticai`
3. Download `google-services.json`
4. Place it in the `app/` folder

#### 3c. Enable Firestore
1. Firebase Console → **Firestore Database**
2. Click **Create Database** → Production mode
3. Select region e.g. `europe-west1` for Sweden

#### 3d. Enable Remote Config
1. Firebase Console → **Remote Config**
2. Click **Add parameter** and add:

| Key | Value |
|---|---|
| `groq_api_key` | `gsk_your_groq_key_here` |

3. Click **Publish changes**

> **Important:** Make sure you click **Publish** — unpublished changes are not fetched by the app.

---

### Step 4 — Get Groq API Key (Free)

1. Go to **console.groq.com**
2. Sign up free — no credit card needed
3. Click **API Keys → Create API Key**
4. Copy key (starts with `gsk_...`)
5. Paste into Firebase Remote Config as `groq_api_key`

**Free limits:** 14,400 requests/day

#### Active models used (with fallback):
```kotlin
private val MODELS = listOf(
    "llama-3.3-70b-versatile",                   // primary
    "llama-3.1-8b-instant",                      // fallback 1
    "meta-llama/llama-4-scout-17b-16e-instruct"  // fallback 2
)
```

> **Note:** `gemma2-9b-it` was deprecated August 2025. `mixtral-8x7b-32768` was deprecated March 2025. Do not use these.

---

### Step 5 — Get Cloudinary Account (Free)

1. Go to **cloudinary.com**
2. Click **Sign Up Free** — no credit card needed
3. From your Dashboard note down:
   - `Cloud Name`
   - `API Key`
   - `API Secret`

**Free limits:** 25 GB storage, 25 GB bandwidth/month

---

### Step 6 — Upload Products to Firebase

#### 6a. Prepare your folder

```
firebase_upload/
  ├── upload_products.py
  ├── products.csv
  └── serviceAccountKey.json
```

**Get `serviceAccountKey.json`:**
1. Firebase Console → ⚙️ **Project Settings**
2. **Service Accounts** → **Generate new private key**
3. Save as `serviceAccountKey.json`

#### 6b. Edit `upload_products.py`

```python
CLOUDINARY_CLOUD   = "your_cloud_name"
CLOUDINARY_API_KEY = "your_api_key"
CLOUDINARY_SECRET  = "your_api_secret"
```

#### 6c. Install dependencies (Mac/Linux)

```bash
pip3 install firebase-admin pandas requests cloudinary
```

#### 6d. Run the script

```bash
cd ~/Desktop/firebase_upload
python3 upload_products.py
```

The script auto-downloads free images from Picsum Photos, uploads them to Cloudinary, and saves everything to Firestore. Takes 3–5 minutes.

---

### Step 7 — Remote Config during Development




> Upload you key to remote config.

---

### Step 8 — Run the App

1. Open project in **Android Studio**
2. Place `google-services.json` in `app/` folder
3. Click **Sync Now**
4. Add to `gradle.properties`: `android.disallowKotlinSourceSets=false`
5. Run on device or emulator

---

## AI Tools Available

The AI agent has 8 tools it automatically decides to call:

| Tool | What it does | Example query |
|---|---|---|
| `search_products` | Search by keyword | *"Show me earbuds"* |
| `filter_by_price` | Filter by max price | *"Under 300 SEK"* |
| `filter_by_category` | List a category | *"Show fitness products"* |
| `filter_by_stock` | In stock or sold out | *"Give me sold out items"* |
| `sort_by_rating` | Sort best/worst rated | *"Least ranking products"* |
| `sort_by_price` | Sort cheap/expensive | *"Cheapest products"* |
| `compare_products` | Side by side compare | *"Compare X and Y"* |
| `get_categories` | List all categories | *"What categories do you have?"* |

> **Note:** `filter_by_stock` uses `string` type (`"true"`/`"false"`) not `boolean` in the tool schema. This is intentional — Groq LLMs sometimes pass boolean values as strings, and using string type with safe parsing handles both cases correctly.

---

## UI Behaviour

- **On launch** — suggestion chips shown centered in the screen with a welcome message
- **After first message** — chips move to a persistent scrollable row above the input field and stay there for the entire conversation

---

## AgenticAIUseCases

All use cases are bundled into one data class for clean Hilt injection:

```kotlin
data class AgenticAIUseCases @Inject constructor(
    val getProducts: GetProductsUseCase,
    val searchProducts: SearchProductsUseCase,
    val filterByPrice: FilterByPriceUseCase,
    val filterByCategory: FilterByCategoryUseCase,
    val filterByStock: FilterByStockUseCase,
    val sortByRating: SortByRatingUseCase,
    val sortByPrice: SortByPriceUseCase,
    val compareProducts: CompareProductsUseCase,
    val getCategories: GetCategoriesUseCase
)
```

---

## Product Categories

| Category | Products |
|---|---|
| electronics | 10 products |
| fitness | 8 products |
| footwear | 7 products |
| clothing | 8 products |
| kitchen | 8 products |
| home | 9 products |

---

## Free Tier Limits

| Service | Free Limit | What happens when exceeded |
|---|---|---|
| Groq API | 14,400 req/day | Returns 429 — resets next day, app auto-switches model |
| Firestore reads | 50,000/day | Returns permission error |
| Firestore writes | 20,000/day | Returns permission error |
| Cloudinary storage | 25 GB | Upload fails |
| Cloudinary bandwidth | 25 GB/month | Images stop loading |
| Firebase Remote Config | Unlimited | Never exceeded |

---



## Adding New Products

1. Add rows to `products.csv`
2. Run `python3 upload_products.py` again
3. Script overwrites existing docs and uploads new ones
4. Restart the app — new products load on next launch

---

## Built With

- [Jetpack Compose](https://developer.android.com/compose) — UI
- [Hilt](https://dagger.dev/hilt/) — Dependency Injection
- [Groq](https://console.groq.com) — Free LLM API (Llama 3.3)
- [Firebase Firestore](https://firebase.google.com/products/firestore) — Product database
- [Firebase Remote Config](https://firebase.google.com/products/remote-config) — Secure API key storage
- [Cloudinary](https://cloudinary.com) — Product image hosting
- [Coil](https://coil-kt.github.io/coil/) — Image loading in Compose
- [OkHttp](https://square.github.io/okhttp/) — HTTP client for Groq API

---

## License

Free to use and modify. Happy Coding! 😄