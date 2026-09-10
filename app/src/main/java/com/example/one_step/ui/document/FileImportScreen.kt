package com.example.one_step.ui.document

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepSurface
import com.example.one_step.ui.theme.OneStepText
import com.example.one_step.ui.theme.OneStepTextMuted

@Composable
fun FileImportScreen(
    onBack: () -> Unit,
    onDocumentText: (String) -> Unit,
    viewModel: DocumentInputViewModel = viewModel(key = "file-import"),
) {
    val context = LocalContext.current
    val state = viewModel.state
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val name = uri?.let {
            context.contentResolver.query(it, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getString(0) else null
            }
        }
        viewModel.importUri(uri, name)
    }

    LaunchedEffect(Unit) {
        picker.launch(arrayOf("application/pdf", "image/jpeg", "image/png"))
    }
    LaunchedEffect(state) {
        val success = state as? DocumentInputState.Success
        if (success != null) {
            onDocumentText(success.documentText)
            viewModel.reset()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(OneStepBackground).statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로 가기", tint = OneStepText) }
            Text("파일 불러오기", style = MaterialTheme.typography.titleLarge, color = OneStepText)
        }
        Card(colors = CardDefaults.cardColors(containerColor = OneStepSurface), shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(14.dp), modifier = Modifier.size(56.dp)) {
                    Icon(Icons.Default.FolderOpen, null, tint = OneStepBlue, modifier = Modifier.padding(14.dp))
                }
                Text("안내문 파일을 선택해 주세요", style = MaterialTheme.typography.titleMedium, color = OneStepText)
                Text("PDF, JPG, JPEG, PNG 파일을 지원해요. 이미지 파일은 기존 OCR로 글자를 읽습니다.", style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted)
                Button(onClick = { picker.launch(arrayOf("application/pdf", "image/jpeg", "image/png")) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.FolderOpen, null)
                    Spacer(Modifier.width(8.dp))
                    Text("파일 선택")
                }
            }
        }
        when (val current = state) {
            is DocumentInputState.Loading -> LoadingCard(current.fileName)
            is DocumentInputState.Error -> ErrorCard(current.message) { viewModel.reset(); picker.launch(arrayOf("application/pdf", "image/jpeg", "image/png")) }
            else -> Unit
        }
    }
}

@Composable
private fun LoadingCard(fileName: String?) {
    Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.CircularProgressIndicator(color = OneStepBlue, modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("파일 내용을 읽고 있어요", style = MaterialTheme.typography.titleMedium, color = OneStepText)
                Text(fileName ?: "잠시만 기다려 주세요", style = MaterialTheme.typography.bodySmall, color = OneStepTextMuted)
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Surface(color = Color(0xFFFFF1F1), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, null, tint = OneStepBlue)
                Spacer(Modifier.width(8.dp))
                Text(message, color = OneStepText, style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedButton(onClick = onRetry) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(6.dp))
                Text("다시 선택")
            }
        }
    }
}
