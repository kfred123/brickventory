
package com.example.brickapp.data.api

/**
 * Simple in-memory token manager.
 * For production, use platform-specific secure storage (Keychain, EncryptedSharedPrefs, etc.)
 */
object TokenManager {
    private var _token: String? = null
    private var _userId: String? = null
    private var _displayName: String? = null

    val isLoggedIn: Boolean get() = _token != null

    fun saveSession(token: String, userId: String, displayName: String) {
        _token = token
        _userId = userId
        _displayName = displayName
    }

    fun getToken(): String? = _token
    fun getUserId(): String? = _userId
    fun getDisplayName(): String? = _displayName

    fun clearSession() {
        _token = null
        _userId = null
        _displayName = null
    }
}
