package com.dd3boh.outertune

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object AiRepository {
    suspend fun fetchRecommendations(prompt: String, apiKey: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val jsonBody = """
                {
                  "contents": [{
                    "parts":[{"text": "Suggest 5 songs matching this prompt: '$prompt'. Return ONLY track title and artist pairs, one per line, without numbers or markdown."}]
                  }]
                }
            """.trimIndent()

            conn.outputStream.use { os ->
                os.write(jsonBody.toByteArray(Charsets.UTF_8))
            }

            conn.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            ""
        }
    }
}
