package com.ationet.androidterminal.core.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ationet.androidterminal.core.data.local.room.ReceiptDao
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptViewEntity

class ReceiptViewPager(
    private val receiptDao: ReceiptDao,
    private val batchId: Int,
    private val controllerOwner: String,
    private val pumpId: Int?
) : PagingSource<Int, ReceiptViewEntity>() {
    override fun getRefreshKey(
        state: PagingState<Int, ReceiptViewEntity>
    ): Int? {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
            val prevKey = anchorPage.prevKey ?: return anchorPage.nextKey?.minus(1)

            return prevKey + 1
        }
    }

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, ReceiptViewEntity> {
        // Start refresh at page 1 if undefined.
        val nextPageNumber = params.key ?: 0

        val offset = params.loadSize * nextPageNumber
        val response = receiptDao.getReceiptHeaders(
            offset = offset,
            limit = params.loadSize,
            batchId = batchId,
            controllerOwner = controllerOwner,
            pumpId = pumpId
        )

        return LoadResult.Page(
            data = response,
            prevKey = if (nextPageNumber == 0) null else nextPageNumber - 1,
            nextKey = nextPageNumber + 1
        )
    }
}