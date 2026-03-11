package com.liber.book_read_management.service.recommendation

import com.liber.book_read_management.client.AladinClient
import com.liber.book_read_management.service.recommendation.model.RecommendationCandidate
import org.springframework.stereotype.Service

@Service
class RetrieverService(
    private val aladinClient: AladinClient
) {

    fun retrieve(queryCandidates: List<String>, maxResultsPerQuery: Int = 10): List<RecommendationCandidate> {
        return queryCandidates.flatMap { query ->
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
    }
}
