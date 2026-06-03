package com.liber.book_read_management.service.recommendation

import com.liber.book_read_management.client.AladinClient
import com.liber.book_read_management.service.recommendation.model.RecommendationCandidate
import org.springframework.stereotype.Service

@Service
class RetrieverService(
    private val aladinClient: AladinClient
) {

    fun retrieve(queryCandidates: List<String>, maxResultsPerQuery: Int = 10): List<RecommendationCandidate> {
        val candidatesByQuery = queryCandidates.map { query ->
            val response = aladinClient.searchItem(
                query = query,
                start = 1,
                maxResult = maxResultsPerQuery
            )

            response.item.orEmpty().map { item ->
                RecommendationCandidate(
                    query = query,
                    item = item
                )
            }
        }

        return interleave(candidatesByQuery)
    }

    private fun interleave(candidatesByQuery: List<List<RecommendationCandidate>>): List<RecommendationCandidate> {
        val maxSize = candidatesByQuery.maxOfOrNull { it.size } ?: return emptyList()
        return (0 until maxSize).flatMap { index ->
            candidatesByQuery.mapNotNull { candidates -> candidates.getOrNull(index) }
        }
    }
}
