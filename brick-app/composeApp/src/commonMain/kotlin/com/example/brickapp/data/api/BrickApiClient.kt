
package com.example.brickapp.data.api

import com.example.brickapp.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class BrickApiClient(private val baseUrl: String) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
    }

    private fun HttpRequestBuilder.withAuth() {
        TokenManager.getToken()?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    // ---- Auth ----

    suspend fun login(email: String, password: String): Result<AuthResponse> = runCatching {
        val response = client.post("$baseUrl/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }
        if (response.status == HttpStatusCode.OK) {
            val auth = response.body<AuthResponse>()
            TokenManager.saveSession(auth.token, auth.userId, auth.displayName)
            auth
        } else {
            throw Exception("Login failed: ${response.status}")
        }
    }

    suspend fun register(email: String, password: String, displayName: String): Result<AuthResponse> = runCatching {
        val response = client.post("$baseUrl/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(email, password, displayName))
        }
        if (response.status == HttpStatusCode.OK) {
            val auth = response.body<AuthResponse>()
            TokenManager.saveSession(auth.token, auth.userId, auth.displayName)
            auth
        } else {
            throw Exception("Registration failed: ${response.status}")
        }
    }

    // ---- Bricks ----

    suspend fun getBricks(search: String? = null): Result<List<BrickDetailResponse>> = runCatching {
        client.get("$baseUrl/api/bricks") {
            withAuth()
            search?.let { parameter("search", it) }
        }.body()
    }

    suspend fun getBrick(brickId: String): Result<BrickDetailResponse> = runCatching {
        client.get("$baseUrl/api/bricks/$brickId") {
            withAuth()
        }.body()
    }

    fun getBrickImageUrl(imageId: String): String = "$baseUrl/api/bricks/images/$imageId"

    // ---- Sets ----

    suspend fun getSets(year: Int? = null): Result<List<BrickSetResponse>> = runCatching {
        client.get("$baseUrl/api/sets") {
            withAuth()
            year?.let { parameter("year", it) }
        }.body()
    }

    suspend fun getSet(setId: String): Result<BrickSetDetailResponse> = runCatching {
        client.get("$baseUrl/api/sets/$setId") {
            withAuth()
        }.body()
    }

    // ---- User Collection: Bricks ----

    suspend fun getMyBricks(): Result<List<UserBrickResponse>> = runCatching {
        client.get("$baseUrl/api/user/bricks") {
            withAuth()
        }.body()
    }

    suspend fun addBrick(brickId: String, quantity: Int = 1, notes: String? = null): Result<UserBrickResponse> = runCatching {
        client.post("$baseUrl/api/user/bricks") {
            withAuth()
            contentType(ContentType.Application.Json)
            setBody(AddBrickRequest(brickId, quantity, notes))
        }.body()
    }

    suspend fun updateBrick(brickId: String, quantity: Int, notes: String? = null): Result<UserBrickResponse> = runCatching {
        client.put("$baseUrl/api/user/bricks/$brickId") {
            withAuth()
            contentType(ContentType.Application.Json)
            setBody(UpdateCollectionRequest(quantity, notes))
        }.body()
    }

    suspend fun removeBrick(brickId: String): Result<Unit> = runCatching {
        client.delete("$baseUrl/api/user/bricks/$brickId") {
            withAuth()
        }
        Unit
    }

    // ---- User Collection: Sets ----

    suspend fun getMySets(): Result<List<UserSetResponse>> = runCatching {
        client.get("$baseUrl/api/user/sets") {
            withAuth()
        }.body()
    }

    suspend fun addSet(setId: String, quantity: Int = 1, notes: String? = null): Result<UserSetResponse> = runCatching {
        client.post("$baseUrl/api/user/sets") {
            withAuth()
            contentType(ContentType.Application.Json)
            setBody(AddSetRequest(setId, quantity, notes))
        }.body()
    }

    suspend fun updateSet(setId: String, quantity: Int, notes: String? = null): Result<UserSetResponse> = runCatching {
        client.put("$baseUrl/api/user/sets/$setId") {
            withAuth()
            contentType(ContentType.Application.Json)
            setBody(UpdateCollectionRequest(quantity, notes))
        }.body()
    }

    suspend fun removeSet(setId: String): Result<Unit> = runCatching {
        client.delete("$baseUrl/api/user/sets/$setId") {
            withAuth()
        }
        Unit
    }

    fun logout() {
        TokenManager.clearSession()
    }
}
