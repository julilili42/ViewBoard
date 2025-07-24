package com.example.viewboard.ui.screens.issue

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.example.viewboard.stateholder.IssueViewModel

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun DraggableScreen(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { DragTargetInfo() }
    var rootOffset by remember { mutableStateOf(Offset.Zero) }
    CompositionLocalProvider(
        LocalDragTargetInfo provides state
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned { cords ->
                    rootOffset = cords.localToWindow(Offset.Zero)
                }
        ) {
            content()
            if (state.isDragging) {
                var targetSize by remember { mutableStateOf(IntSize.Zero) }
                Box(
                    modifier = Modifier.graphicsLayer {
                        val raw = state.dragPosition + state.dragOffset
                        val corrected = raw - rootOffset
                        scaleX = 0.8f
                        scaleY = 0.8f
                        alpha = if (targetSize == IntSize.Zero) 0f else .9f
                        translationX = corrected.x - targetSize.width / 2
                        translationY = corrected.y - targetSize.height / 2
                    }
                        .onGloballyPositioned { targetSize = it.size }
                ) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }
}



@Composable
fun <T> DragTarget(
    modifier: Modifier = Modifier,
    dataToDrop: T,
    viewModel: IssueViewModel,
    content: @Composable () -> Unit
) {
    val dragInfo = LocalDragTargetInfo.current
    var startPos by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier
            .onGloballyPositioned { startPos = it.localToWindow(Offset.Zero) }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        viewModel.startDragging()
                        dragInfo.dataToDrop = dataToDrop
                        dragInfo.isDragging = true
                        dragInfo.dragPosition = startPos + offset
                        dragInfo.draggableComposable = content
                    },
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                        dragInfo.dragOffset += dragAmount
                    },
                    onDragEnd = {
                        viewModel.stopDragging()
                        dragInfo.isDragging = false
                        dragInfo.dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        viewModel.stopDragging()
                        dragInfo.isDragging = false
                        dragInfo.dragOffset = Offset.Zero
                    }
                )
            }
    ) {
        if (!(dragInfo.isDragging && dragInfo.dataToDrop == dataToDrop)) {
            content()
        }
    }
}

@Composable
fun <T> DropItem(
    modifier: Modifier = Modifier,
    onDrop: (T) -> Unit,
    content: @Composable BoxScope.(isOver: Boolean, label: String) -> Unit
) {
    val dragInfo = LocalDragTargetInfo.current
    val pos = dragInfo.dragPosition + dragInfo.dragOffset
    var isOver by remember { mutableStateOf(false) }
    Box(
        modifier
            .onGloballyPositioned { coords ->
                isOver = coords.boundsInWindow().contains(pos)
            }
    ) {
        content(isOver, "")
        val data: T? = if (!dragInfo.isDragging && isOver) {
            @Suppress("UNCHECKED_CAST") (dragInfo.dataToDrop as? T)
        } else null
        LaunchedEffect(data) {
            data?.let { dropped ->
                onDrop(dropped)
                dragInfo.isDragging = false
                dragInfo.dragOffset = Offset.Zero
                dragInfo.dataToDrop = null
                dragInfo.draggableComposable = null
            }
        }
    }
}

 class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
}