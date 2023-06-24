package com.rie.alkisah

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.rie.alkisah.Screens.viewmodel.MainVModel
import com.rie.alkisah.database.adapter.MrPagingAdapter
import com.rie.alkisah.database.data.MrStoryRepository
import com.rie.alkisah.database.db.MrKisahResRoom
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)

class MainVModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repositoryStory: MrStoryRepository
    private lateinit var mainViewModel: MainVModel
    private val dummyStoryList = DataDummy.generateDummyStoryResponse()

    @Before
    fun setUp() {
        mainViewModel = MainVModel(repositoryStory)
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest{
        val data: PagingData<MrKisahResRoom> = QuotePagingSource.snapshot(dummyStoryList)
        val expectedResult = MutableLiveData<PagingData<MrKisahResRoom>>()
        expectedResult.value = data
        Mockito.`when`(repositoryStory.getStory()).thenReturn(expectedResult)

        val actualStory: PagingData<MrKisahResRoom> = mainViewModel.getStory().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MrPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Mockito.verify(repositoryStory).getStory()
        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStoryList, differ.snapshot())
        assertEquals(dummyStoryList.size, differ.snapshot().size)
        assertEquals(dummyStoryList[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story No Data Should Return Empty List`() = runTest {
        val emptyData: PagingData<MrKisahResRoom> = PagingData.empty()
        val expectedResult = MutableLiveData<PagingData<MrKisahResRoom>>()
        expectedResult.value = emptyData
        Mockito.`when`(repositoryStory.getStory()).thenReturn(expectedResult)

        val actualStory: PagingData<MrKisahResRoom> = mainViewModel.getStory().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MrPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Mockito.verify(repositoryStory).getStory()
        Assert.assertNotNull(differ.snapshot())
        Assert.assertTrue(differ.snapshot().isEmpty())
    }


}

class QuotePagingSource : PagingSource<Int, LiveData<List<MrKisahResRoom>>>(){
    companion object {
        fun snapshot(items: List<MrKisahResRoom>): PagingData<MrKisahResRoom> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<MrKisahResRoom>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<MrKisahResRoom>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}