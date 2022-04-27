package com.ifcompany.githubrepository

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import com.ifcompany.githubrepository.data.database.DataBaseProvider
import com.ifcompany.githubrepository.data.entity.GithubRepoEntity
import com.ifcompany.githubrepository.databinding.ActivityMainBinding
import com.ifcompany.githubrepository.view.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RepositoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()
    }

    private fun initAdapter() {
        adapter = RepositoryRecyclerAdapter()
    }

    private fun initViews() = with(binding) {
        emptyResultTextView.isGone = true
        recyclerView.adapter = adapter
        searchButton.setOnClickListener {
            startActivity(
                    Intent(this@MainActivity, SearchActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        launch(coroutineContext) {
            loadSearchHistory()
        }
    }

    private suspend fun loadSearchHistory() = withContext(Dispatchers.IO) {
        val histories = DataBaseProvider.provideDB(this@MainActivity).searchHistoryDao().getHistory()
        withContext(Dispatchers.Main) {
            setData(histories)
        }
    }

    private fun setData(githubRepoList: List<GithubRepoEntity>) = with(binding) {
        if (githubRepoList.isEmpty()) {
            emptyResultTextView.isGone = false
            recyclerView.isGone = true
        } else {
            emptyResultTextView.isGone = true
            recyclerView.isGone = false
            adapter.setSearchResultList(githubRepoList) {
                startActivity(
                        Intent(this@MainActivity, RepositoryActivity::class.java).apply {
                            putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                            putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                        }
                )
            }
        }
    }

}
