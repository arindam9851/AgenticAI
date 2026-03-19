package com.example.agenticai.data.source.remote


import android.util.Log
import com.example.agenticai.domain.model.Message
import com.example.agenticai.domain.model.Product
import com.example.agenticai.domain.model.Role
import com.example.agenticai.domain.usecase.AgenticAIUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroqAgentClient @Inject constructor(
    private val useCases: AgenticAIUseCases
) {
    private val _url = "https://api.groq.com/openai/v1/chat/completions"

    private val _models = listOf(
        "llama-3.3-70b-versatile",                   // primary — best quality
        "llama-3.1-8b-instant",                      // fallback 1 — fastest + high limits
        "meta-llama/llama-4-scout-17b-16e-instruct"  // fallback 2 — tool calling supported
    )

    private val http = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val _systemPromt = """
        You are a smart shopping assistant for a Swedish store.
        All prices are in SEK (Swedish Krona).
        Always use tools to get real product data before answering.
        Never guess product names or prices.
        Be friendly and concise.
        When listing products always mention name, price in SEK, rating and stock status.
        Respond in the same language the user uses.
 
        Tool usage guide:
        - "fitness products", "show fitness"           → filter_by_category(category="fitness")
        - "electronics", "show electronics"            → filter_by_category(category="electronics")
        - "clothing", "home", "kitchen", "footwear"    → filter_by_category(category=<that category>)
        - "sold out", "out of stock", "unavailable"    → filter_by_stock(in_stock="false")
        - "in stock", "available", "what's available"  → filter_by_stock(in_stock="true")
        - "lowest rated", "worst rated", "least"       → sort_by_rating(order="asc")
        - "highest rated", "best rated", "top"         → sort_by_rating(order="desc")
        - "cheapest", "lowest price", "budget"         → sort_by_price(order="asc")
        - "most expensive", "highest price"            → sort_by_price(order="desc")
        - "under X SEK", "below X SEK"                → filter_by_price(max_price=X)
        - "search X", "find X", "show me X"           → search_products(keyword=X)
    """.trimIndent()

    private fun buildTools(): JSONArray = JSONArray().apply {

        fun param(type: String, desc: String) = JSONObject().apply {
            put("type", type)
            put("description", desc)
        }

        fun tool(name: String, desc: String, props: JSONObject, required: List<String>) =
            JSONObject().apply {
                put("type", "function")
                put("function", JSONObject().apply {
                    put("name", name)
                    put("description", desc)
                    put("parameters", JSONObject().apply {
                        put("type", "object")
                        put("properties", props)
                        put("required", JSONArray(required))
                    })
                })
            }

        // 1. Search by keyword
        put(tool(
            "search_products",
            "Search products by keyword in name, description or category",
            JSONObject().apply {
                put("keyword", param("string", "Search keyword e.g. 'yoga', 'speaker'"))
            },
            listOf("keyword")
        ))

        // 2. Filter by max price
        put(tool(
            "filter_by_price",
            "Filter products under a max price in SEK with optional category",
            JSONObject().apply {
                put("max_price", param("integer", "Maximum price in SEK"))
                put("category",  param("string",  "Optional category filter"))
            },
            listOf("max_price")
        ))

        // 3. Filter by category
        put(tool(
            "filter_by_category",
            "Get all products in a specific category: electronics, fitness, clothing, footwear, kitchen, home",
            JSONObject().apply {
                put("category", param("string", "Category name"))
            },
            listOf("category")
        ))

        // 4. Filter by stock
        put(tool(
            "filter_by_stock",
            "Filter by stock status. Use in_stock='false' for sold out, in_stock='true' for available",
            JSONObject().apply {
                put("in_stock", param("string", "'true' = available/in stock, 'false' = sold out/unavailable"))
                put("category", param("string", "Optional category filter"))
            },
            listOf("in_stock")
        ))

        // 5. Sort by rating
        put(tool(
            "sort_by_rating",
            "Sort products by rating. order=asc for lowest/worst rated, order=desc for highest/best rated",
            JSONObject().apply {
                put("order",    param("string", "'asc' = lowest rated first, 'desc' = highest rated first"))
                put("category", param("string", "Optional category filter"))
            },
            listOf("order")
        ))

        // 6. Sort by price
        put(tool(
            "sort_by_price",
            "Sort products by price. order=asc for cheapest first, order=desc for most expensive first",
            JSONObject().apply {
                put("order",    param("string", "'asc' = cheapest first, 'desc' = most expensive first"))
                put("category", param("string", "Optional category filter"))
            },
            listOf("order")
        ))

        // 7. Compare two products
        put(tool(
            "compare_products",
            "Compare two products side by side by name",
            JSONObject().apply {
                put("product1", param("string", "First product name"))
                put("product2", param("string", "Second product name"))
            },
            listOf("product1", "product2")
        ))

        // 8. List all categories
        put(tool(
            "get_categories",
            "List all available product categories in the store",
            JSONObject().apply {
                put("properties", JSONObject())
            },
            listOf()
        ))
    }

    private fun executeTool(
        name: String,
        args: JSONObject,
        products: List<Product>
    ): Pair<String, List<Product>> {
        return when (name) {

            "search_products" -> {
                val keyword = args.optString("keyword", "")
                val result  = useCases.searchProducts(products, keyword)
                val text    = if (result.isEmpty()) "No products found for '$keyword'"
                else formatProducts(result)
                Pair(text, result)
            }

            "filter_by_price" -> {
                val maxPrice = args.optInt("max_price", Int.MAX_VALUE)
                val category = args.optString("category", "")
                val result   = useCases.filterByPrice(products, maxPrice, category)
                val text     = if (result.isEmpty()) "No products found under $maxPrice SEK"
                else formatProducts(result)
                Pair(text, result)
            }

            "filter_by_category" -> {
                val category = args.optString("category", "")
                val result   = useCases.filterByCategory(products, category)
                val text     = if (result.isEmpty()) "No products in '$category'"
                else formatProducts(result)
                Pair(text, result)
            }

            "filter_by_stock" -> {
                val inStock = when (val inStockRaw = args.opt("in_stock")) {
                    is Boolean -> inStockRaw
                    is String  -> inStockRaw.trim().lowercase() == "true"
                    else       -> true
                }
                val category = args.optString("category", "")
                val result   = useCases.filterByStock(products, inStock, category)
                val text = when {
                    result.isEmpty() && !inStock -> "No sold out products found."
                    result.isEmpty()             -> "No products found."
                    !inStock -> "Sold out (${result.size}):\n${formatProducts(result)}"
                    else     -> "In stock (${result.size}):\n${formatProducts(result)}"
                }
                Pair(text, result)
            }

            "sort_by_rating" -> {
                val order    = args.optString("order", "desc")
                val category = args.optString("category", "")
                val result   = useCases.sortByRating(
                    products  = products,
                    category  = category,
                    ascending = order == "asc"
                )
                val text = if (result.isEmpty()) "No products found"
                else formatProducts(result)
                Pair(text, result)
            }

            "sort_by_price" -> {
                val order    = args.optString("order", "asc")
                val category = args.optString("category", "")
                val result   = useCases.sortByPrice(
                    products  = products,
                    category  = category,
                    ascending = order == "asc"
                )
                val text = if (result.isEmpty()) "No products found"
                else formatProducts(result)
                Pair(text, result)
            }

            "compare_products" -> {
                val name1    = args.optString("product1", "")
                val name2    = args.optString("product2", "")
                val (p1, p2) = useCases.compareProducts(products, name1, name2)
                val result   = listOfNotNull(p1, p2)
                val text     = if (p1 == null || p2 == null) "One or both products not found"
                else buildCompareText(p1, p2)
                Pair(text, result)
            }

            "get_categories" -> {
                val cats = useCases.getCategories(products)
                Pair("Available categories: ${cats.joinToString(", ")}", emptyList())
            }

            else -> Pair("Unknown tool: $name", emptyList())
        }
    }

    private fun formatProducts(products: List<Product>): String =
        products.joinToString("\n") {
            "• ${it.name} | ${it.price} SEK | Rating: ${it.rating} | " +
                    if (it.inStock) "In Stock" else "Out of Stock"
        }

    private fun buildCompareText(p1: Product, p2: Product) = """
        ${p1.name}: ${p1.price} SEK | Rating: ${p1.rating} | ${if (p1.inStock) "In Stock" else "Out of Stock"}
        ${p2.name}: ${p2.price} SEK | Rating: ${p2.rating} | ${if (p2.inStock) "In Stock" else "Out of Stock"}
    """.trimIndent()

    suspend fun chat(
        apiKey: String,
        history: List<Message>,
        products: List<Product>
    ): Pair<String, List<Product>> = withContext(Dispatchers.IO) {

        for (model in _models) {
            try {
                Log.d("GroqAgent", "Trying model: $model")

                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", _systemPromt)
                    })
                    history.forEach { msg ->
                        if (!msg.isToolCall) {
                            put(JSONObject().apply {
                                put("role", if (msg.role == Role.USER) "user" else "assistant")
                                put("content", msg.content)
                            })
                        }
                    }
                }

                var finalProducts   = emptyList<Product>()
                var lastText        = "Sorry, I could not understand your request. Please try rephrasing."

                for (i in 0 until 6) {
                    val body = JSONObject().apply {
                        put("model",       model)
                        put("messages", messages)
                        put("tools",       buildTools())
                        put("tool_choice", "auto")
                        put("max_tokens",  1024)
                    }.toString()

                    val request = Request.Builder()
                        .url(_url)
                        .post(body.toRequestBody("application/json".toMediaType()))
                        .addHeader("Authorization", "Bearer $apiKey")
                        .addHeader("Content-Type", "application/json")
                        .build()

                    val response     = http.newCall(request).execute()
                    val responseText = response.body.string()
                    val json         = JSONObject(responseText)

                    if (!response.isSuccessful) {
                        throw Exception(
                            json.optJSONObject("error")?.optString("message")
                                ?: "API error ${response.code}"
                        )
                    }

                    val choice       = json.getJSONArray("choices").getJSONObject(0)
                    val message      = choice.getJSONObject("message")
                    val finishReason = choice.getString("finish_reason")

                    messages.put(message)

                    val textContent = message.optString("content", "")
                    if (textContent.isNotBlank()) lastText = textContent

                    when (finishReason) {

                        "stop" -> return@withContext Pair(lastText, finalProducts)

                        "tool_calls" -> {
                            val toolCalls = message.optJSONArray("tool_calls") ?: continue

                            for (t in 0 until toolCalls.length()) {
                                val call     = toolCalls.getJSONObject(t)
                                val toolId   = call.getString("id")
                                val toolName = call.getJSONObject("function").getString("name")
                                val toolArgs = try {
                                    JSONObject(call.getJSONObject("function").getString("arguments"))
                                } catch (_: Exception) {
                                    JSONObject()
                                }

                                Log.d("GroqAgent", "Calling tool: $toolName with $toolArgs")

                                val (result, resultProducts) = executeTool(toolName, toolArgs, products)
                                if (resultProducts.isNotEmpty()) finalProducts = resultProducts

                                messages.put(JSONObject().apply {
                                    put("role", "tool")
                                    put("tool_call_id", toolId)
                                    put("content", result)
                                })
                            }
                        }

                        else -> return@withContext Pair(lastText, finalProducts)
                    }
                }

                return@withContext Pair(lastText, finalProducts)

            } catch (e: Exception) {
                val msg = e.message ?: ""
                Log.d("GroqAgent", "Model $model failed: $msg")

                //Specific rate limit check — won't match real errors like "invalid key"
                val isRateLimited = msg.contains("rate_limit", ignoreCase = true) ||
                        msg.contains("rate limit", ignoreCase = true) ||
                        msg.contains("429", ignoreCase = true) ||
                        msg.contains("exceeded your current quota", ignoreCase = true) ||
                        msg.contains("too many requests", ignoreCase = true)

                if (isRateLimited) {
                    Log.d("GroqAgent", "Rate limited on $model, trying next...")
                    continue
                } else {
                    throw e
                }
            }
        }

        return@withContext Pair(
            "All models are currently busy. Please try again in a minute.",
            emptyList()
        )
    }
}