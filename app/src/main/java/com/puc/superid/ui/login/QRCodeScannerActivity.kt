package com.puc.superid.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.puc.superid.data.datasource.confirmarLoginViaQRCode
import com.puc.superid.ui.theme.SuperidTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class QRCodeScannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SuperidTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    QRCodeScannerScreen(
                        onCodeScanned = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}


@SuppressLint("UnsafeOptInUsageError")
@Composable
fun QRCodeScannerScreen(onCodeScanned: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                .build().apply {
                    setAnalyzer(ContextCompat.getMainExecutor(ctx), { imageProxy ->
                        processQRCodeFromImageProxy(imageProxy, ctx) { code ->
                            Log.d("QRCode", "Código escaneado: $code")
                            coroutineScope.launch(Dispatchers.IO) {
                                // Chamando a função para confirmar o login via QR Code
                                confirmarLoginViaQRCode(code, context)
                            }
                            onCodeScanned()  // Notifica que o QR Code foi escaneado
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                ctx as androidx.lifecycle.LifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )

        }, ContextCompat.getMainExecutor(ctx))

        previewView
    }, modifier = Modifier.fillMaxSize())
}

@OptIn(ExperimentalGetImage::class)
private fun processQRCodeFromImageProxy(
    imageProxy: ImageProxy,
    context: Context,
    onCodeFound: (String) -> Unit
) {
    val mediaImage = imageProxy.image ?: return imageProxy.close()

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    val scanner = BarcodeScanning.getClient(options)

    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
                barcode.rawValue?.let {
                    onCodeFound(it)  // Envia o código QR encontrado para a função de login
                }
            }
        }
        .addOnFailureListener {
            Log.e("QRCode", "Erro ao ler código: ${it.message}")
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}