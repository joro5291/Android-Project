package com.ifcompany.githubrepository.data.response

import com.ifcompany.githubrepository.data.entity.GithubRepoEntity

data class GithubRepoSearchResponse(
    val totalCount: Int,
    val items: List<GithubRepoEntity>
)
