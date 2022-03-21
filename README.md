# Library for Supabase Auth
## Install
### With BOM
```groovy  
dependencies {  
 implementation platform("io.github.ferhatwi:supabase-kt-bom:0.1.0")
 implementation "io.github.ferhatwi:supabase-auth-kt"
}  
```  
### Without BOM
#### NOTICE: BOM is strongly recommended to prevent conflicts.
```groovy 
dependencies {  
 implementation "io.github.ferhatwi:supabase-auth-kt:0.1.0"
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
auth.signUpWithEmail(
            "SOME@EMAIL.com",
            "PASSWORD",
            onFailure = {

            },
            onSuccess = { session, user ->

            }
        )
```
#### Sign In With Email
```kotlin
auth.signInWithEmail(
            "SOME@EMAIL.com",
            "PASSWORD",
            onFailure = {

            },
            onSuccess = { session, user ->

            }
        )
```
#### Sign Up With Phone
```kotlin
auth.signUpWithPhone(
            "PHONE_NUMBER",
            "PASSWORD",
            onFailure = {

            },
            onSuccess = { session, user ->

            }
        )
```
#### Sign In With Phone
```kotlin
auth.signInWithPhone(
            "PHONE_NUMBER",
            "PASSWORD",
            onFailure = {

            },
            onSuccess = { session, user ->

            }
        )
```
#### Sign In With Third Part Provider
```kotlin
auth.signInWithThirdPartyProvider(
            "ID_TOKEN",
            "UNENCRYPTED_NONCE",
            "CLIENT_KEY",
            PROVIDER,
            onFailure = {

            },
            onSuccess = { session, user ->

            }
        )
```
#### Send Magic Link Email
```kotlin
auth.sendMagicLinkEmail(
            "SOME@EMAIL.com",
            onFailure = {

            },
            onSuccess = {

            }
        )
```
#### Send Mobile OTP
```kotlin
auth.sendMobileOTP(
            "PHONE_NUMBER",
            onFailure = {

            },
            onSuccess = {

            }
        )
```
#### Sign Out
```kotlin
auth.signOut(
            onFailure = {

            },
            onSuccess = {

            }
        )
```
#### Verify Mobile OTP
```kotlin
auth.verifyMobileOTP(
            "PHONE_NUMBER",
            "TOKEN",
            onFailure = {

            },
            onSuccess = { session, user ->

            }
        )
```
#### Reset Password for Email
```kotlin
auth.resetPasswordForEmail(
            "SOME@EMAIL.com",
            onFailure = {

            },
            onSuccess = {

            }
        )
```
#### Refresh Access Token
```kotlin
auth.refreshAccessToken(
            "REFRESH_TOKEN",
            onFailure = {

            },
            onSuccess = {

            }
        )
```
#### Get User
```kotlin
auth.getUser(
            onFailure = {

            },
            onSuccess = { session, user ->

            }
        )
```
#### Update User
```kotlin
auth.updateUser(
            "SOME@EMAIL.com",
            "PHONE_NUMBER",
            "PASSWORD",
            "EMAIL_CHANGE_TOKEN"
            DATA
            onFailure = {

            },
            onSuccess = { session, user ->

            }
        )
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