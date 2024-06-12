package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InfiniteCraftApp()
        }
    }
}

@Composable
fun InfiniteCraftApp() {
    val viewModel: GameViewModel = viewModel()
    val elements = viewModel.elements
    var scrollOffset by remember { mutableStateOf(Offset.Zero) }
    val scrollState = rememberScrollableState { delta ->
        // Actualizar el desplazamiento en ambas direcciones
        scrollOffset = Offset(scrollOffset.x + delta, scrollOffset.y + delta)
        delta
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scrollOffset += dragAmount
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(scrollOffset.x.toInt(), scrollOffset.y.toInt()) }
        ) {
            elements.forEach { element ->
                ElementBox(element, viewModel)
            }
        }
    }
}

@Composable
fun ElementBox(element: Element, viewModel: GameViewModel) {
    var offset by remember { mutableStateOf(viewModel.elementOffsets[element.name] ?: Offset.Zero) }

    Box(
        modifier = Modifier
            .size(64.dp)
            .offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
            .background(color = element.color, shape = RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        viewModel.onElementDrop(element, offset)

                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                        viewModel.updateElementOffset(element.name, offset)
                    }
                )
            }
    )
}

data class Element(val name: String, val color: Color)

class GameViewModel : ViewModel() {
    var elements by mutableStateOf(List(100) { index ->
        Element("Element $index", Color(android.graphics.Color.HSVToColor(floatArrayOf(index.toFloat() % 360, 1f, 1f))))
    })

    var elementOffsets by mutableStateOf(mutableMapOf<String, Offset>())

    fun onElementDrop(element: Element, offset: Offset) {

    }

    fun updateElementOffset(name: String, offset: Offset) {
        elementOffsets[name] = offset
    }
}
