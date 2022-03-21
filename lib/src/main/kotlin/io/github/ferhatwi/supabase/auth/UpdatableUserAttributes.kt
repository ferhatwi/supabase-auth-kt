package io.github.ferhatwi.supabase.auth


class UpdatableUserAttributes {
    class Email(val value: String?)
    class Phone(val value: String?)
    class Password(val value: String?)
    class EmailChangeToken(val value: String?)
    class Data(val value: Any?)
}