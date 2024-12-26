package com.example.projectbwah.utils

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.CropState
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.rememberImagePicker
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.launch


@Composable
fun ImageCropper(
    aspectRatio: Float,
    context: Context,
    onImage: (ImageBitmap) -> Unit,
    onOnPickImage: (()->Unit) -> Unit
) {
    val imageCropper = rememberImageCropper()

    var cropResult by rememberSaveable { mutableStateOf<CropResult?>(null) }
    var alreadyCropped by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val imagePicker = rememberImagePicker(onImage = { uri ->
        scope.launch {
            alreadyCropped = false
            cropResult = imageCropper.crop(uri, context)
        }
    })

    onOnPickImage { imagePicker.pick() }


    val cropState = imageCropper.cropState
    var isFirstLoad by rememberSaveable { mutableStateOf(true) }

    if (cropState == null) isFirstLoad = true

    cropState?.let { state ->
        if (isFirstLoad) {
            state.region = calculateRegion(state.src.size, aspectRatio)
            state.aspectLock = true
            isFirstLoad = false
        }
        ImageCropperDialog(
            state = state,
            cropControls = { },
            topBar = { CustomTopBar(it) },
        )
    }

    if(!alreadyCropped) {
        when (cropResult) {
            CropResult.Cancelled -> {}
            is CropResult.Success -> {
                val bitmap = (cropResult as CropResult.Success).bitmap
                onImage(bitmap)
                alreadyCropped = true
            }

            CropError.LoadingError -> {}
            CropError.SavingError -> {}
            null -> {}
        }
    }
}

@Composable
fun CustomTopBar(state: CropState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { state.done(accept = false) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        }
        IconButton(onClick = { state.done(accept = true) }, enabled = !state.accepted) {
            Icon(Icons.Default.Done, contentDescription = null)
        }
    }
}

private fun calculateRegion(imageSize: IntSize, aspectRatio: Float): Rect {
    val width = imageSize.width.toFloat()
    val height = imageSize.height.toFloat()
    val newWidth: Float
    val newHeight: Float

    if (width / height > aspectRatio) {
        newWidth = height * aspectRatio
        newHeight = height
    } else {
        newWidth = width
        newHeight = width / aspectRatio
    }

    return Rect(
        offset = Offset((width - newWidth) / 2, (height - newHeight) / 2),
        size = Size(newWidth, newHeight)
    )
}