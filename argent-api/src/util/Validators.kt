package argent.util

object YoutubeUrlValidator{
    private val patterns = Pair(
        Regex("""^https?://(www\.)?youtube\.com/watch/?\?v=([^&]+?)(&.*)?${'$'}"""),
        Regex("""^https?://(www\.)?youtu\.be/([^/]+?)/?(\?.*)?${'$'}""")
    )
    fun getVideoId(url: String): String? =  patterns.first.find(url)?.groups?.get(2)?.value ?: patterns.second.find(url)?.groups?.get(2)?.value
}

