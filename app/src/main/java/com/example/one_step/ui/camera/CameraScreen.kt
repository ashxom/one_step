package com.example.one_step.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.one_step.data.camera.CameraXController
import com.example.one_step.ui.theme.OneStepBackground
import com.example.one_step.ui.theme.OneStepBlue
import com.example.one_step.ui.theme.OneStepBlueSoft
import com.example.one_step.ui.theme.OneStepSurface
import com.example.one_step.ui.theme.OneStepText
import com.example.one_step.ui.theme.OneStepTextMuted

@Composable
fun CameraScreen(
    onBack: () -> Unit,
    viewModel: CameraViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.uiState
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPermission = granted
        if (granted) viewModel.reset() else viewModel.onCameraError("카메라 권한이 거부되었습니다.")
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) viewModel.onImageSelectionCancelled() else viewModel.recognize(uri)
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val controller = remember(context) { CameraXController(context) }
    DisposableEffect(controller) {
        onDispose { controller.unbind() }
    }

    when {
        state.ocrState is OcrState.Loading -> OcrLoadingOverlay((state.ocrState as OcrState.Loading).step)
        state.ocrState is OcrState.Success -> OcrSuccessScreen((state.ocrState as OcrState.Success).text, viewModel::reset)
        !hasCameraPermission -> PermissionScreen(
            errorMessage = (state.ocrState as? OcrState.Error)?.message,
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
            onPickGallery = { galleryLauncher.launch("image/*") },
            onBack = onBack,
        )
        else -> CameraContent(
            state = state,
            controller = controller,
            lifecycleOwner = lifecycleOwner,
            onReady = viewModel::onCameraReady,
            onCameraError = { viewModel.onCameraError("카메라를 시작하지 못했습니다.") },
            onCapture = { controller.capture(viewModel::recognize) { viewModel.onCaptureFailed("사진을 촬영하지 못했습니다.") } },
            onPickGallery = { galleryLauncher.launch("image/*") },
            onToggleFlash = { viewModel.toggleFlash(controller::setFlashEnabled) },
            onRetry = viewModel::reset,
            onBack = onBack,
        )
    }
}

@Composable
private fun CameraContent(
    state: CameraUiState,
    controller: CameraXController,
    lifecycleOwner: LifecycleOwner,
    onReady: (Boolean) -> Unit,
    onCameraError: () -> Unit,
    onCapture: () -> Unit,
    onPickGallery: () -> Unit,
    onToggleFlash: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        CameraPreview(controller, lifecycleOwner, onReady, onCameraError)
        if (state.ocrState is OcrState.Idle || state.ocrState is OcrState.Error) {
            GuideFrame()
            CameraControls(state.hasFlash, state.flashEnabled, state.cameraReady, onBack, onCapture, onPickGallery, onToggleFlash)
        }
        when (val ocrState = state.ocrState) {
            is OcrState.Loading -> OcrLoadingOverlay(ocrState.step)
            is OcrState.Error -> OcrErrorOverlay(ocrState.message, onRetry)
            else -> Unit
        }
    }
}

@Composable
private fun CameraPreview(
    controller: CameraXController,
    lifecycleOwner: LifecycleOwner,
    onReady: (Boolean) -> Unit,
    onError: () -> Unit,
) {
    val context = LocalContext.current
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    AndroidView(
        factory = {
            PreviewView(it).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                previewView = this
            }
        },
        modifier = Modifier.fillMaxSize(),
    )
    LaunchedEffect(previewView, lifecycleOwner) {
        previewView?.let { view -> controller.bind(view, lifecycleOwner, onReady, { onError() }) }
    }
}

@Composable
private fun GuideFrame() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(.82f)
                    .aspectRatio(.72f)
                    .border(2.dp, Color.White.copy(alpha = .9f), RoundedCornerShape(18.dp)),
            )
            Spacer(Modifier.size(16.dp))
            Surface(color = Color.Black.copy(alpha = .55f), shape = CircleShape) {
                Text("문서를 프레임 안에 맞춰주세요", color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun CameraControls(
    hasFlash: Boolean,
    flashEnabled: Boolean,
    cameraReady: Boolean,
    onBack: () -> Unit,
    onCapture: () -> Unit,
    onPickGallery: () -> Unit,
    onToggleFlash: () -> Unit,
) {
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.Close, "촬영 취소", tint = Color.White) }
            Text("안내문 촬영", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(Modifier.weight(1f))
            if (hasFlash) {
                IconButton(onClick = onToggleFlash) {
                    Icon(if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff, "플래시 전환", tint = Color.White)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Surface(
                color = Color.Black.copy(alpha = .55f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.clickable(onClick = onPickGallery),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)) {
                    Icon(Icons.Default.PhotoLibrary, "갤러리에서 선택", tint = Color.White)
                    Text("갤러리", color = Color.White, style = MaterialTheme.typography.labelLarge)
                }
            }
            Surface(
                color = if (cameraReady) Color.White else Color.Gray,
                shape = CircleShape,
                modifier = Modifier.size(76.dp).clickable(enabled = cameraReady, onClick = onCapture),
            ) {
                Icon(Icons.Default.CameraAlt, "사진 촬영", tint = OneStepBlue, modifier = Modifier.padding(22.dp))
            }
            Spacer(Modifier.width(82.dp))
        }
    }
}

@Composable
private fun OcrLoadingOverlay(step: OcrStep) {
    Surface(color = Color.Black.copy(alpha = .82f), modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.Center) {
            Surface(color = OneStepSurface, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Text("안내문을 분석하고 있어요", style = MaterialTheme.typography.titleLarge, color = OneStepText)
                    Text("잠시만 기다려 주세요", style = MaterialTheme.typography.bodyMedium, color = OneStepTextMuted)
                    OcrStep.entries.forEach { item ->
                        val completed = item.ordinal < step.ordinal
                        val current = item == step
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            when {
                                completed -> Icon(Icons.Default.CheckCircle, null, tint = OneStepBlue, modifier = Modifier.size(24.dp))
                                current -> CircularProgressIndicator(color = OneStepBlue, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                                else -> Icon(Icons.Default.RadioButtonUnchecked, null, tint = OneStepTextMuted, modifier = Modifier.size(24.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(item.label, color = if (current || completed) OneStepText else OneStepTextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OcrErrorOverlay(message: String, onRetry: () -> Unit) {
    Surface(color = Color.Black.copy(alpha = .82f), modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.Center) {
            Surface(color = OneStepSurface, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Close, null, tint = OneStepBlue, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.size(12.dp))
                    Text(message, color = OneStepText, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.size(20.dp))
                    Button(onClick = onRetry) {
                        Icon(Icons.Default.Refresh, null)
                        Spacer(Modifier.width(8.dp))
                        Text("다시 시도")
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionScreen(
    errorMessage: String?,
    onRequestPermission: () -> Unit,
    onPickGallery: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(OneStepBackground).statusBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로 가기", tint = OneStepText) }
        Icon(Icons.Default.CameraAlt, null, tint = OneStepBlue, modifier = Modifier.size(56.dp))
        Spacer(Modifier.size(20.dp))
        Text("카메라 권한이 필요해요", style = MaterialTheme.typography.titleLarge, color = OneStepText)
        Text(errorMessage ?: "안내문을 촬영하려면 카메라 접근을 허용해 주세요.", color = OneStepTextMuted, modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.size(24.dp))
        Button(onClick = onRequestPermission) { Text("카메라 권한 허용") }
        OutlinedButton(onClick = onPickGallery) { Text("갤러리에서 선택") }
    }
}

@Composable
private fun OcrSuccessScreen(text: String, onRetake: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(OneStepBackground).statusBarsPadding().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, null, tint = OneStepBlue, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(8.dp))
            Text("안내문을 읽었어요", style = MaterialTheme.typography.titleLarge, color = OneStepText)
        }
        Text("인식된 전체 텍스트", style = MaterialTheme.typography.titleMedium, color = OneStepText, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
        Surface(color = OneStepSurface, shape = RoundedCornerShape(16.dp), modifier = Modifier.weight(1f).fillMaxWidth()) {
            Text(text, color = OneStepText, modifier = Modifier.padding(18.dp).verticalScroll(rememberScrollState()))
        }
        Button(onClick = onRetake, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Icon(Icons.Default.CameraAlt, null)
            Spacer(Modifier.width(8.dp))
            Text("다시 촬영하기")
        }
    }
}
