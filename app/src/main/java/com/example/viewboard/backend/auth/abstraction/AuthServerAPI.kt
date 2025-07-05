package com.example.viewboard.backend.auth.abstraction

abstract class AuthServerAPI () {
    public abstract fun getUid(): String?
    public abstract fun getEmail(): String?
    public abstract fun getDisplayName(): String?
    public abstract fun isLoggedIn(): Boolean
    public abstract fun register(name: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit)
}