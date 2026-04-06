package dev.spiffocode.sigesmobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun <T> InfiniteScrollList(
    modifier: Modifier = Modifier,
    elements: List<T>,
    key: (Int, T) -> Any,
    loadMoreItems: () -> Unit,
    hasNextPage: Boolean,
    content: @Composable LazyListScope.(T) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(listState, hasNextPage) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 2
        }
            .distinctUntilChanged()
            .filter { it && hasNextPage }
            .collect { loadMoreItems() }
    }

    LazyColumn(modifier = modifier, state = listState) {
        itemsIndexed(items = elements, key = { i, el -> key(i, el) }) { _, element ->
            this@LazyColumn.content(element)
        }

        if (hasNextPage) {
            item(key = "loader") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}