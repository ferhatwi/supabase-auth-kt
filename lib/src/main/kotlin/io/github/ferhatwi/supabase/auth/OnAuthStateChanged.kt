package io.github.ferhatwi.supabase.auth

interface OnAuthStateChanged {
    fun onStateChange(user: User?)
    fun onSignIn(user: User) {
        onStateChange(user)
    }
    fun onSignOut() {
        onStateChange(null)
    }
}