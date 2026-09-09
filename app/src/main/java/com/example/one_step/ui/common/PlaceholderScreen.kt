package com.example.one_step.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepText
import com.example.one_step.ui.theme.OneStepTextMuted

@Composable
fun PlaceholderScreen(title: String, description: String, onBack: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기", tint = OneStepText)
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(color = OneStepBlueSoft, shape = MaterialTheme.shapes.extraLarge) {
                Text("한걸음", modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp), color = OneStepBlue, fontWeight = FontWeight.Bold)
            }
            Text(title, style = MaterialTheme.typography.titleLarge, color = OneStepText, modifier = Modifier.padding(top = 16.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
