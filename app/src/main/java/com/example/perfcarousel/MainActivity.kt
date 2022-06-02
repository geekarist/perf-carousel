package com.example.perfcarousel

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxSize()
            ) {
                CustomLazyRow()
            }
        }
    }
}

@Composable
private fun CustomLazyRow() {
    val scrollState = rememberLazyListState()

    LazyRow(
        state = scrollState,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(
            items = (1..20).toList()
        ) { index, item ->
            var isFocused by remember { mutableStateOf(false) }
            Text(
                text = "Item $item",
                modifier = Modifier
                    .dpadNavigation(scrollState, index)
                    .width(156.dp)
                    .aspectRatio(4 / 3F)
                    .onFocusChanged { isFocused = it.isFocused }
                    .focusable()
                    .border(if (isFocused) 4.dp else Dp.Hairline, Color.Black)
            )
        }
    }
}

fun Modifier.dpadNavigation(
    scrollState: LazyListState,
    index: Int
) = composed {
    val focusManager = LocalFocusManager.current
    var focusDirectionToMove by remember { mutableStateOf<FocusDirection?>(null) }
    val scope = rememberCoroutineScope()

    onKeyEvent {
        if (it.type == KeyEventType.KeyDown) {
            when (it.nativeKeyEvent.keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> focusDirectionToMove = FocusDirection.Left
                KeyEvent.KEYCODE_DPAD_RIGHT -> focusDirectionToMove = FocusDirection.Right
            }
            if (focusDirectionToMove != null) {
                scope.launch {
                    if (focusDirectionToMove == FocusDirection.Left && index > 0) {
                        scrollState.animateScrollToItem(index - 1)
                        focusManager.moveFocus(FocusDirection.Left)
                    }
                    if (focusDirectionToMove == FocusDirection.Right) {
                        scrollState.animateScrollToItem(index + 1)
                        focusManager.moveFocus(FocusDirection.Right)
                    }
                }
            }
        }
        true
    }
}