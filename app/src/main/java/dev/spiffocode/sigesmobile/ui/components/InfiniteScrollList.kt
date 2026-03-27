package dev.spiffocode.sigesmobile.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> InfiniteScrollList(
    modifier: Modifier = Modifier,
    elements: List<T>,
    key: (Int, T) -> Any,
    loadMoreItems: () -> Unit,
    hasNextPage: Boolean,
    content: @Composable (LazyListScope.(T) -> Unit),
) {
    val listState = rememberLazyListState()

    LazyColumn(modifier = modifier, state = listState) {
        itemsIndexed(items = elements, key = {i, el -> key(i,el)}) { index, element ->
            this@LazyColumn.content(element)
            if (index >= (elements.lastIndex - 2) && hasNextPage) {
                loadMoreItems()
            }
        }
    }
}