package io.github.ferhatwi.supabase.auth

sealed class Provider {
    override fun toString(): String {
        return when (this) {
            Apple -> "apple"
            Azure -> "azure"
            Bitbucket -> "bitbucket"
            Discord -> "discord"
            Facebook -> "facebook"
            GitHub -> "gitHub"
            GitLab -> "gitLab"
            Google -> "google"
            LinkedIn -> "linkedIn"
            Notion -> "notion"
            Twitch -> "twitch"
            Twitter -> "twitter"
            Slack -> "slack"
            Spotify -> "spotify"
        }
    }

    object Apple : Provider()
    object Azure : Provider()
    object Bitbucket : Provider()
    object Discord : Provider()
    object Facebook : Provider()
    object GitHub : Provider()
    object GitLab : Provider()
    object Google : Provider()
    object LinkedIn : Provider()
    object Notion : Provider()
    object Twitch : Provider()
    object Twitter : Provider()
    object Slack : Provider()
    object Spotify : Provider()
}
