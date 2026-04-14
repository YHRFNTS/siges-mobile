package dev.spiffocode.sigesmobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
fun <T> InfiniteScrollGrid(
    modifier: Modifier = Modifier,
    elements: List<T>,
    columns: Int,
    key: (Int, T) -> Any,
    loadMoreItems: () -> Unit,
    hasNextPage: Boolean,
    spacing: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(0.dp),
    footerContent: (LazyGridScope.() -> Unit)? = null,
    content: @Composable LazyGridScope.(T) -> Unit
) {
    val gridState = rememberLazyGridState()
    LaunchedEffect(gridState, hasNextPage) {
        snapshotFlow {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = gridState.layoutInfo.totalItemsCount
            lastVisible >= total - 2
        }
            .distinctUntilChanged()
            .filter { it && hasNextPage }
            .collect { loadMoreItems() }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier, 
        state = gridState,
        verticalArrangement = spacing
    ) {
        itemsIndexed(items = elements, key = { i, el -> key(i, el) }) { _, element ->
            this@LazyVerticalGrid.content(element)
        }

        footerContent?.invoke(this)

        if (hasNextPage) {
            item(key = "loader", span = { GridItemSpan(this.maxLineSpan) }) {
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
