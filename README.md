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

The AI automatically decides which tool to call, queries your real product database, and replies with matching products + images.

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
├── domain/                          ← Pure Kotlin, no Android deps
│   ├── model/
│   │   ├── Product.kt               ← Product data model
│   │   └── Message.kt               ← Chat message + Role enum
│   ├── repository/
│   │   └── Repositories.kt          ← Repository interfaces
│   └── usecase/
│       ├── AgenticAIUseCases.kt     ← Data class bundling all use cases
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
├── data/                            ← Data sources and implementations
│   ├── model/
│   │   └── ProductDto.kt            ← Firestore DTO with domain mapper
│   ├── source/remote/
│   │   ├── FirestoreProductSource.kt
│   │   ├── RemoteConfigSource.kt
│   │   └── GroqAgentClient.kt       ← Groq API + agentic loop
│   └── repository/
│       └── RepositoryImpl.kt
│
├── presentation/                    ← UI layer
│   ├── chat/
│   │   ├── ChatContract.kt          ← MVI: State, Intent, Effect
│   │   ├── ChatViewModel.kt
│   │   └── ChatScreen.kt
│   ├── component/
│   │   └── Components.kt            ← ChatBubble, ProductCard, Chips
│   └── theme/
│       └── Theme.kt
│
└── core/
    └── di/
        └── AppModule.kt             ← Hilt dependency injection
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
git clone https://github.com/yourusername/AgenticAI.git
cd AgenticAI
```

---

### Step 2 — Firebase Setup

#### 2a. Create Firebase project
1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Click **Add Project**
3. Follow the setup wizard

#### 2b. Add Android app to Firebase
1. In Firebase Console → click **Add App** → choose Android
2. Enter package name: `com.example.agenticai`
3. Download `google-services.json`
4. Place it in the `app/` folder of the project

#### 2c. Enable Firestore
1. Firebase Console → **Firestore Database**
2. Click **Create Database**
3. Choose **Production mode**
4. Select a region close to you (e.g. `europe-west1` for Sweden)
5. Click **Done**

#### 2d. Enable Remote Config
1. Firebase Console → **Remote Config**
2. Click **Add parameter**
3. Add this parameter:

| Parameter Key | Value |
|---|---|
| `groq_api_key` | `gsk_your_groq_key_here` |

4. Click **Publish changes**

---

### Step 3 — Get Groq API Key (Free)

1. Go to [console.groq.com](https://console.groq.com)
2. Sign up with Google — no credit card needed
3. Click **API Keys → Create API Key**
4. Copy the key (starts with `gsk_...`)
5. Paste it as the value of `groq_api_key` in Firebase Remote Config

**Free limits:** 14,400 requests/day, 6,000 tokens/minute

---

### Step 4 — Get Cloudinary Account (Free)

1. Go to [cloudinary.com](https://cloudinary.com)
2. Click **Sign Up Free** — no credit card needed
3. After signup go to your **Dashboard**
4. Note down:
   - `Cloud Name`
   - `API Key`
   - `API Secret`

**Free limits:** 25 GB storage, 25 GB bandwidth/month

---

### Step 5 — Upload Products to Firebase

You need Python installed (comes pre-installed on Mac).

#### 5a. Prepare your files

Create a folder on your Desktop called `firebase_upload` with:

```
firebase_upload/
  ├── upload_products.py      ← upload script
  ├── products.csv            ← 50 product CSV file
  └── serviceAccountKey.json  ← Firebase service account key
```

**Get `serviceAccountKey.json`:**
1. Firebase Console → ⚙️ **Project Settings**
2. Click **Service Accounts** tab
3. Click **Generate new private key**
4. Save as `serviceAccountKey.json` in the folder

#### 5b. Edit `upload_products.py`

Open the script and fill in your Cloudinary credentials:

```python
CLOUDINARY_CLOUD   = "your_cloud_name"    # from Cloudinary dashboard
CLOUDINARY_API_KEY = "your_api_key"       # from Cloudinary dashboard
CLOUDINARY_SECRET  = "your_api_secret"    # from Cloudinary dashboard
```

#### 5c. Install dependencies

Open Terminal and run:

```bash
pip3 install firebase-admin pandas requests cloudinary
```

#### 5d. Run the script

```bash
cd ~/Desktop/firebase_upload
python3 upload_products.py
```

The script will:
1. Read all 50 products from `products.csv`
2. Download a free image for each product from Picsum Photos
3. Upload each image to Cloudinary
4. Save product data + image URL to Firestore

Takes about 3–5 minutes. You'll see live progress:

```
[1/50] Samsung Galaxy Buds 2
  Image downloaded ✓
  Uploaded to Cloudinary ✓
  Saved to Firestore ✓
```

---

### Step 6 — Firestore Data Structure

After the script runs, your Firestore will look like this:

```
products/
  samsung_galaxy_buds_2/
    name:        "Samsung Galaxy Buds 2"
    category:    "electronics"
    price:       449
    description: "Wireless earbuds with active noise cancellation"
    imageUrl:    "https://res.cloudinary.com/.../samsung_galaxy_buds2.jpg"
    inStock:     true
    rating:      4.3
    createdAt:   timestamp
```

---

### Step 7 — Run the App

1. Open the project in **Android Studio**
2. Make sure `google-services.json` is in the `app/` folder
3. Click **Sync Now** when prompted
4. Connect your Android device or start an emulator
5. Click **Run ▶**

---

## AI Tools Available

The AI agent has 8 tools it can call automatically:

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

---

## Free Tier Limits Summary

| Service | Free Limit | What happens when exceeded |
|---|---|---|
| Groq API | 14,400 req/day | Returns 429 error — resets next day |
| Firestore reads | 50,000/day | Returns permission error |
| Firestore writes | 20,000/day | Returns permission error |
| Cloudinary storage | 25 GB | Upload fails |
| Cloudinary bandwidth | 25 GB/month | Images stop loading |
| Firebase Remote Config | Unlimited | Never exceeded |

---

## Adding New Products

1. Add new rows to `products.csv`
2. Run `python3 upload_products.py` again
3. It will skip existing products and only upload new ones
4. Restart the app — new products load automatically

---

## Built With

- [Jetpack Compose](https://developer.android.com/compose) — UI
- [Hilt](https://dagger.dev/hilt/) — Dependency Injection
- [Groq](https://console.groq.com) — Free LLM API
- [Firebase Firestore](https://firebase.google.com/products/firestore) — Database
- [Firebase Remote Config](https://firebase.google.com/products/remote-config) — API key storage
- [Cloudinary](https://cloudinary.com) — Image hosting
- [Coil](https://coil-kt.github.io/coil/) — Image loading
- [OkHttp](https://square.github.io/okhttp/) — HTTP client

---

## License

Free to use and modify. Happy Coding 😄
