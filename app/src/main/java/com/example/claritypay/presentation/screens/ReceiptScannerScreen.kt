package com.example.claritypay.presentation.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
import com.example.claritypay.domain.models.ScannedReceipt
import com.example.claritypay.domain.receipt.ReceiptParser
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.ReceiptScannerViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScannerScreenRoute(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val app = LocalContext.current.applicationContext as ClarityPayApp
    val viewModel: ReceiptScannerViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var scannerSession by remember { mutableIntStateOf(0) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearError()
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onSaved()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Escanear ticket") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when {
                state.draft != null -> {
                    ReceiptReviewContent(
                        receipt = state.draft!!,
                        onSave = viewModel::saveReceipt,
                        onRetake = {
                            scannerSession += 1
                            viewModel.resetScanner()
                        }
                    )
                }

                hasCameraPermission -> {
                    ReceiptCameraContent(
                        sessionKey = scannerSession,
                        isProcessing = state.isProcessing,
                        onTicketDetected = viewModel::onReceiptDetected
                    )
                }

                else -> {
                    PermissionContent(
                        onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReceiptCameraContent(
    sessionKey: Int,
    isProcessing: Boolean,
    onTicketDetected: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ReceiptCameraPreview(
            sessionKey = sessionKey,
            onTicketDetected = onTicketDetected,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.84f)
                .height(360.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.height(10.dp))
            }
            Text(
                text = if (isProcessing) "Leyendo ticket..." else "Coloca el ticket dentro del marco",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun ReceiptReviewContent(
    receipt: ScannedReceipt,
    onSave: (String, Double, String, String) -> Unit,
    onRetake: () -> Unit
) {
    var title by remember(receipt) { mutableStateOf(receipt.title) }
    var amount by remember(receipt) { mutableStateOf(formatAmount(receipt.amount)) }
    var category by remember(receipt) { mutableStateOf(receipt.category) }
    var dateLabel by remember(receipt) { mutableStateOf(receipt.dateLabel) }
    val categories = listOf("Casa", "Servicios", "Movilidad", "Personal", "Entretenimiento", "Otro")
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Datos detectados", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "${receipt.ticketType} • ${currencyFormatter.format(receipt.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
        )
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Comercio o concepto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Total") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = dateLabel,
            onValueChange = { dateLabel = it },
            label = { Text("Fecha visible") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text("Categoria", style = MaterialTheme.typography.labelLarge)
        categories.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { item ->
                    FilterChip(
                        selected = category == item,
                        onClick = { category = item },
                        label = { Text(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = {
                onSave(title, amount.replace(',', '.').toDoubleOrNull() ?: 0.0, category, dateLabel)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Aceptar gasto")
        }
        TextButton(
            onClick = onRetake,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Borrar y escanear otra")
        }
    }
}

@Composable
private fun PermissionContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("La camara esta desactivada", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Activa el permiso para escanear tickets.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(onClick = onRequestPermission) {
            Text("Permitir camara")
        }
    }
}

@Composable
private fun ReceiptCameraPreview(
    sessionKey: Int,
    onTicketDetected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember(sessionKey) { Executors.newSingleThreadExecutor() }
    val captureInFlight = remember(sessionKey) { AtomicBoolean(false) }
    val previewView = remember(sessionKey) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )

    DisposableEffect(sessionKey, lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val mainExecutor = ContextCompat.getMainExecutor(context)
        var analyzer: ReceiptTextAnalyzer? = null
        val listener = Runnable {
            if (cameraExecutor.isShutdown) return@Runnable

            runCatching {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                analyzer = ReceiptTextAnalyzer detector@{ rawText ->
                    if (!captureInFlight.compareAndSet(false, true)) return@detector
                    imageCapture.takePicture(
                        cameraExecutor,
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                image.close()
                                onTicketDetected(rawText)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                onTicketDetected(rawText)
                            }
                        }
                    )
                }
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis -> analyzer?.let { analysis.setAnalyzer(cameraExecutor, it) } }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
            }
        }

        cameraProviderFuture.addListener(listener, mainExecutor)

        onDispose {
            analyzer?.close()
            runCatching { cameraProviderFuture.get().unbindAll() }
            cameraExecutor.shutdown()
        }
    }
}

private class ReceiptTextAnalyzer(
    private val onTicketDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val isAnalyzing = AtomicBoolean(false)
    private val hasDetected = AtomicBoolean(false)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (hasDetected.get() || !isAnalyzing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            isAnalyzing.set(false)
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val rawText = visionText.text
                if (ReceiptParser.looksLikeReceipt(rawText)) {
                    hasDetected.set(true)
                    onTicketDetected(rawText)
                }
            }
            .addOnCompleteListener {
                isAnalyzing.set(false)
                imageProxy.close()
            }
    }

    fun close() {
        recognizer.close()
    }
}

private fun formatAmount(amount: Double): String =
    if (amount == 0.0) "" else "%.2f".format(Locale.US, amount)
