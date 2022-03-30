# Library for Supabase Auth
## Install
### With BOM
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ferhatwi/supabase-kt-bom.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ferhatwi%22%20AND%20a:%22supabase-kt-bom%22)
```groovy  
dependencies {  
 implementation platform("io.github.ferhatwi:supabase-kt-bom:{BOM_VERSION}")
 implementation "io.github.ferhatwi:supabase-auth-kt"
}  
```  
### Without BOM
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ferhatwi/supabase-auth-kt.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ferhatwi%22%20AND%20a:%22supabase-auth-kt%22)
#### NOTICE: BOM is strongly recommended to prevent conflicts.
```groovy 
dependencies {  
 implementation "io.github.ferhatwi:supabase-auth-kt:{AUTH_VERSION}"
}  
```  
## How to use?
### REMINDER
Supabase should be initialized before using this library. Instructions are [here](https://github.com/ferhatwi/supabase-kt).
#
```kotlin
val auth = Supabase.auth()  
```  
#### Sign Up With Email
```kotlin
auth.signUpWithEmail("SOME@EMAIL.com", "PASSWORD")
    .catch {

    }.collect {

    }
```
#### Sign In With Email
```kotlin
auth.signInWithEmail("SOME@EMAIL.com", "PASSWORD")
    .catch {

    }.collect {

    }
```
#### Sign Up With Phone
```kotlin
auth.signUpWithPhone("PHONE_NUMBER", "PASSWORD")
    .catch {

    }.collect {

    }
```
#### Sign In With Phone
```kotlin
auth.signInWithPhone("PHONE_NUMBER", "PASSWORD")
    .catch {

    }.collect {

    }
```
#### Sign In With Third Part Provider
```kotlin
auth.signInWithThirdPartyProvider(
            "ID_TOKEN",
            "UNENCRYPTED_NONCE",
            "CLIENT_KEY",
            PROVIDER)
    .catch {

    }.collect {

    }
```
#### Send Magic Link Email
```kotlin
auth.sendMagicLinkEmail("SOME@EMAIL.com")
    .catch {

    }.collect {

    }
```
#### Send Mobile OTP
```kotlin
auth.sendMobileOTP("PHONE_NUMBER")
    .catch {

    }.collect {

    }
```
#### Sign Out
```kotlin
auth.signOut()
    .catch {

    }.collect {

    }
```
#### Verify Mobile OTP
```kotlin
auth.verifyMobileOTP("PHONE_NUMBER", "TOKEN")
    .catch {

    }.collect {

    }
```
#### Reset Password for Email
```kotlin
auth.resetPasswordForEmail("SOME@EMAIL.com")
    .catch {

    }.collect {

    }
```
#### Refresh Access Token
```kotlin
auth.refreshAccessToken("REFRESH_TOKEN")
    .catch {

    }.collect {

    }
```
#### Get User
```kotlin
auth.getUser()
    .catch {

    }.collect {

    }
```
#### Update User
```kotlin
auth.updateUser(
            "SOME@EMAIL.com",
            "PHONE_NUMBER",
            "PASSWORD",
            "EMAIL_CHANGE_TOKEN",
            DATA)
    .catch {

    }.collect {

    }
```
#### Listen Auth State Changes
```kotlin
auth.setOnAuthStateChanged(object : OnAuthStateChanged {
        override fun onStateChange(user: User?) {
            TODO("Not yet implemented")
        }

        override fun onSignIn(user: User) {
            super.onSignIn(user) //Do not remove
        }

        override fun onSignOut() {
            super.onSignOut() //Do not remove
        }
    })
```
## Improvements and Bugs
Feel free to improve, upgrade, fix or report bugs!
