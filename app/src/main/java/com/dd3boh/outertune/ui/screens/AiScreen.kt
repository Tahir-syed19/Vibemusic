package com.dd3boh.outertune.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dd3boh.outertune.AiRepository
import com.dd3boh.outertune.utils.rememberPreference
import kotlinx.coroutines.launch

data class AiVibeCategory(
    val title: String,
    val subtitle: String,
    val vibePrompt: String
)

val vibeCategories = listOf(
    AiVibeCategory("Daily Vibe Mix", "Tailored to your current mood", "A personalized blend of relaxed and upbeat track recommendations"),
    AiVibeCategory("Late Night Chill", "Soft, atmospheric, ambient tracks", "Mellow, lofi, atmospheric acoustic and chill songs for late night listening"),
    AiVibeCategory("Energy & Focus", "High tempo beats to stay productive", "Upbeat instrumental, electronic, and high-energy productivity music"),
    AiVibeCategory("Deep Discovery", "Hidden gems based on your taste", "Underrated indie and alternative songs matching a modern music lover's vibe")
)

@Composable
fun AiScreen(
    navController: NavController
) {
    val apiKey = rememberPreference(stringPreferencesKey("gemini_api_key"), defaultValue = "")
    var activeVibe by remember { mutableStateOf<AiVibeCategory?>(null) }
    var generatedPlaylist by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "AI Playlists",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // API Key Field (Stored in preferences)
        if (apiKey.isEmpty()) {
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("Enter Gemini API Key once to enable AI Playlists") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // Vibe Grid Section
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(260.dp)
        ) {
            items(vibeCategories) { vibe ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .height(110.dp)
                        .clickable {
                            if (apiKey.isNotBlank()) {
                                activeVibe = vibe
                                isLoading = true
                                scope.launch {
                                    generatedPlaylist = AiRepository.fetchRecommendations(vibe.vibePrompt, apiKey)
                                    isLoading = false
                                }
                            }
                        }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = vibe.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = vibe.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Playlist Output Area
        activeVibe?.let { vibe ->
            Text(
                text = vibe.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (generatedPlaylist.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = generatedPlaylist,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

