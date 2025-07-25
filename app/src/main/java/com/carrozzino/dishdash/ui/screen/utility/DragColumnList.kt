package com.carrozzino.dishdash.ui.screen.utility

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@Composable
fun <T> LazyColumnDragAndDrop(
    modifier    : Modifier = Modifier,
    list        : List<T> = listOf(),
    event       : (List<T>) -> Unit = {},
    content     : @Composable ColumnScope.(isDragging: Boolean, index : Int, item : T) -> Unit
) {
    var internalList by remember { mutableStateOf(list) }

    val listState = rememberLazyListState()
    val dragDropState =
        rememberDragDropState(listState) { fromIndex, toIndex ->
            internalList = internalList.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
            event(internalList)
        }

//    LaunchedEffect(key1 = list) {
//        internalList = list
//    }

    LazyColumn(
        modifier            = modifier.dragContainer(dragDropState),
        state               = listState,
        contentPadding      = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(internalList, key = { _, item -> item.hashCode() }) { index, item ->
            DraggableItem(dragDropState, index) { isDragging, index ->
                content(isDragging, index, item)
            }
        }
    }
}

@Composable
fun rememberDragDropState(lazyListState: LazyListState, onMove: (Int, Int) -> Unit): DragDropState {
    val scope = rememberCoroutineScope()
    val state =
        remember(lazyListState) {
            DragDropState(state = lazyListState, onMove = onMove, scope = scope)
        }
    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            lazyListState.scrollBy(diff)
        }
    }
    return state
}

class DragDropState
internal constructor(
    private val state: LazyListState,
    private val scope: CoroutineScope,
    private val onMove: (Int, Int) -> Unit,
) {
    var draggingItemIndex by mutableStateOf<Int?>(null)
        private set

    internal val scrollChannel = Channel<Float>()

    private var draggingItemDraggedDelta by mutableFloatStateOf(0f)
    private var draggingItemInitialOffset by mutableIntStateOf(0)
    internal val draggingItemOffset: Float
        get() =
            draggingItemLayoutInfo?.let { item ->
                draggingItemInitialOffset + draggingItemDraggedDelta - item.offset
            } ?: 0f

    private val draggingItemLayoutInfo: LazyListItemInfo?
        get() = state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == draggingItemIndex }

    internal var previousIndexOfDraggedItem by mutableStateOf<Int?>(null)
        private set

    internal var previousItemOffset = Animatable(0f)
        private set

    internal fun onDragStart(offset: Offset) {
        state.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
            ?.also {
                draggingItemIndex = it.index
                draggingItemInitialOffset = it.offset
            }
    }

    internal fun onDragInterrupted() {
        if (draggingItemIndex != null) {
            previousIndexOfDraggedItem = draggingItemIndex
            val startOffset = draggingItemOffset
            scope.launch {
                previousItemOffset.snapTo(startOffset)
                previousItemOffset.animateTo(
                    0f,
                    spring(stiffness = Spring.StiffnessMediumLow, visibilityThreshold = 1f),
                )
                previousIndexOfDraggedItem = null
            }
        }
        draggingItemDraggedDelta = 0f
        draggingItemIndex = null
        draggingItemInitialOffset = 0
    }

    internal fun onDrag(offsetY: Float) {
        draggingItemDraggedDelta += offsetY

        val draggingItem = draggingItemLayoutInfo ?: return
        val startOffset = draggingItem.offset + draggingItemOffset
        val endOffset = startOffset + draggingItem.size
        val middleOffset = startOffset + (endOffset - startOffset) / 2f

        val targetItem =
            state.layoutInfo.visibleItemsInfo.find { item ->
                middleOffset.toInt() in item.offset..item.offsetEnd &&
                        draggingItem.index != item.index
            }
        if (targetItem != null) {
            if (
                draggingItem.index == state.firstVisibleItemIndex ||
                targetItem.index == state.firstVisibleItemIndex
            ) {
                state.requestScrollToItem(
                    state.firstVisibleItemIndex,
                    state.firstVisibleItemScrollOffset,
                )
            }
            onMove.invoke(draggingItem.index, targetItem.index)
            draggingItemIndex = targetItem.index
        } else {
            val overscroll =
                when {
                    draggingItemDraggedDelta > 0 ->
                        (endOffset - state.layoutInfo.viewportEndOffset).coerceAtLeast(0f)
                    draggingItemDraggedDelta < 0 ->
                        (startOffset - state.layoutInfo.viewportStartOffset).coerceAtMost(0f)
                    else -> 0f
                }
            if (overscroll != 0f) {
                scrollChannel.trySend(overscroll)
            }
        }
    }

    private val LazyListItemInfo.offsetEnd: Int
        get() = this.offset + this.size
}

fun Modifier.dragContainer(dragDropState: DragDropState): Modifier {
    return this.pointerInput(dragDropState) {
        detectDragGesturesAfterLongPress (
            onDragStart = { offset ->
                println("Daniele on Drag start")
                dragDropState.onDragStart(offset)
            },
            onDragEnd = { dragDropState.onDragInterrupted() },
            onDragCancel = { dragDropState.onDragInterrupted() },
            onDrag = { change, offset ->
                println("Daniele on Drag Vertical")
                change.consume()
                dragDropState.onDrag(offsetY = offset.y)
            },
        )
    }
}

@Composable
fun LazyItemScope.DraggableItem(
    dragDropState: DragDropState,
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(isDragging: Boolean, index : Int) -> Unit,
) {
    val dragging = index == dragDropState.draggingItemIndex
    val draggingModifier =
        if (dragging) {
            Modifier.zIndex(1f).graphicsLayer { translationY = dragDropState.draggingItemOffset }
        } else if (index == dragDropState.previousIndexOfDraggedItem) {
            Modifier.zIndex(1f).graphicsLayer {
                translationY = dragDropState.previousItemOffset.value
            }
        } else {
            Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
        }
    Column(modifier = modifier.then(draggingModifier)) { content(dragging, index) }
}