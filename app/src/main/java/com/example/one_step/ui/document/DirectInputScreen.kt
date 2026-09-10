package com.example.one_step.ui.document

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepText
import com.example.one_step.ui.theme.OneStepTextMuted

@Composable
fun DirectInputScreen(
    onBack: () -> Unit,
    onDocumentText: (String) -> Unit,
    viewModel: DocumentInputViewModel = viewModel(key = "direct-input"),
) {
    var text by rememberSaveable { mutableStateOf("") }
    val state = viewModel.state
    LaunchedEffect(state) {
        val success = state as? DocumentInputState.Success
        if (success != null) {
            onDocumentText(success.documentText)
            viewModel.reset()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(OneStepBackground).statusBarsPadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로 가기", tint = OneStepText) }
            Text("직접 입력하기", style = MaterialTheme.typography.titleLarge, color = OneStepText)
        }
        Surface(color = OneStepBlueSoft, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = OneStepBlue)
                Spacer(Modifier.width(10.dp))
                Text("안내문 전체 내용을 붙여넣거나 직접 입력해 주세요.", style = MaterialTheme.typography.bodyMedium, color = OneStepText)
            }
        }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth().height(360.dp),
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("여기에 안내문 내용을 입력하세요", color = OneStepTextMuted) },
            label = { Text("안내문 내용") },
            leadingIcon = { Icon(Icons.Default.EditNote, null, tint = OneStepBlue) },
            isError = state is DocumentInputState.Error,
        )
        if (state is DocumentInputState.Error) {
            Text(state.message, color = Color(0xFFB3261E), style = MaterialTheme.typography.bodySmall)
        }
        Button(onClick = { viewModel.submitText(text) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.AutoAwesome, null)
            Spacer(Modifier.width(8.dp))
            Text("분석하기")
        }
    }
}
