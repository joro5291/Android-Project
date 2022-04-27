package com.ifcompany.bookreview

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.ifcompany.bookreview.adapter.BookAdapter
import com.ifcompany.bookreview.adapter.HistoryAdapter
import com.ifcompany.bookreview.api.BookService
import com.ifcompany.bookreview.databinding.ActivityMainBinding
import com.ifcompany.bookreview.model.BestSellerDto
import com.ifcompany.bookreview.model.History
import com.ifcompany.bookreview.model.SearchBooksDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var bookService: BookService

    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        initBookRecyclerView()
        initHistoryRecyclerView()

        /*
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "BookSearchDB"
        ).build()

         */
        db = getAppDatabase(this)

        var gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interParkAPIKey))
                .enqueue(object : Callback<BestSellerDto> {
                    override fun onResponse(call: Call<BestSellerDto>, response: Response<BestSellerDto>) {
                        // todo 성공처리

                        if(response.isSuccessful.not()){
                            Log.e(TAG,"NOT! SUCCESS")
                            return
                        }

                        response.body()?.let {
                            Log.d(TAG,it.toString())

                            it.books.forEach{ book ->
                                Log.d(TAG,book.toString())
                            }

                            adapter.submitList(it.books)
                        }
                    }

                    override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                        // todo 실패처리
                        Log.e(TAG,t.toString())
                    }
                })



    }

    private fun initSearchEditText()
    {
        binding.searchEditText.setOnKeyListener{ v,keycode,event ->
            if (keycode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN)
            {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }

            return@setOnKeyListener false
        }

        binding.searchEditText.setOnTouchListener{v,event ->
            if(event.action == MotionEvent.ACTION_DOWN)
            {
                showHistoryView()
            }

            return@setOnTouchListener false
        }
    }

    private fun search(keyword: String) {
        bookService.getBooksByName(getString(R.string.interParkAPIKey),keyword)
            .enqueue(object : Callback<SearchBooksDto> {
                override fun onResponse(call: Call<SearchBooksDto>, response: Response<SearchBooksDto>) {
                    // todo 성공처리

                    hideHistoryView()
                    saveSearchKeyword(keyword)

                    if(response.isSuccessful.not()){
                        Log.e(TAG,"NOT! SUCCESS")
                        return
                    }

                    adapter.submitList(response.body()?.books.orEmpty())
                }

                override fun onFailure(call: Call<SearchBooksDto>, t: Throwable) {
                    // todo 실패처리

                    hideHistoryView()
                    Log.e(TAG,t.toString())
                }
            })
    }

    private fun showHistoryView(){
        Thread{
            var keywords = db.historyDao().getAll().reversed()

            runOnUiThread{
                binding.historyRecyclerView.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())
            }
        }.start()

        binding.historyRecyclerView.isVisible = true
    }

    private fun hideHistoryView(){
        binding.historyRecyclerView.isVisible = false
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread{
            db.historyDao().insertHistory(History(null,keyword))
        }.start()

    }

    fun initBookRecyclerView(){
        adapter = BookAdapter(itemClickListener = {
            var intent = Intent(this,DetailActivity::class.java)
            intent.putExtra("bookModel",it)
            startActivity(intent)
        })

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter

        setContentView(binding.root)
    }

    private fun initHistoryRecyclerView() {
        historyAdapter = HistoryAdapter(historyDeleteClickListener = {
            deleteSearchKeyword(it)
        })

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter

        initSearchEditText()
    }

    private fun deleteSearchKeyword(keyword:String) {
        Thread{
            db.historyDao().delete(keyword)
            showHistoryView()
        }.start()
    }

    companion object{
        private const val TAG = "MainActivity"
    }

}