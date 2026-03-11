package com.liber.book_read_management.service.recommendation

import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.service.recommendation.model.RankedRecommendation
import com.liber.book_read_management.service.recommendation.model.RecommendationCandidate
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import kotlin.math.ln

@Service
class RankerService {

    fun rank(bookInfoRequest: BookInfoRequest, candidates: List<RecommendationCandidate>): List<RankedRecommendation> {
        val profileTokens = tokenize(
            listOf(bookInfoRequest.title, bookInfoRequest.introduction) + bookInfoRequest.reviews
        )
        val queryTokens = tokenize(candidates.map { it.query })

        return candidates
            // 여러 검색어에서 후보가 많이 모여도, Ranking 비용과 점수 분포가 과도하게 흔들리지 않도록 상한을 둔다.
            .take(MAX_CANDIDATES_BEFORE_RANKING)
            .map { candidate ->
                val keywordScore = keywordScore(profileTokens + queryTokens, candidate)
                val publishDateScore = publishDateScore(candidate)
                val popularityScore = popularityScore(candidate)
                val totalScore = keywordScore * 0.5 + publishDateScore * 0.25 + popularityScore * 0.25

                RankedRecommendation(candidate = candidate, score = totalScore)
            }
            .sortedByDescending { it.score }
            .take(TOP_RANKED_RESULTS)
    }

    private fun keywordScore(profileTokens: Set<String>, candidate: RecommendationCandidate): Double {
        if (profileTokens.isEmpty()) {
            return 0.0
        }

        val candidateTokens = tokenize(
            listOfNotNull(
                candidate.item.title,
                candidate.item.author,
                candidate.item.description,
                candidate.item.categoryName,
                candidate.query
            )
        )

        return profileTokens.intersect(candidateTokens).size.toDouble() / profileTokens.size
    }

    private fun publishDateScore(candidate: RecommendationCandidate): Double {
        val pubDate = parseDate(candidate.item.pubDate) ?: return 0.0
        val ageInDays = ChronoUnit.DAYS.between(pubDate, LocalDate.now()).coerceAtLeast(0)
        return 1.0 / (1.0 + ageInDays / 365.0)
    }

    private fun popularityScore(candidate: RecommendationCandidate): Double {
        val salesPoint = candidate.item.salesPoint?.coerceAtLeast(0) ?: 0
        val reviewRank = candidate.item.customerReviewRank?.coerceAtLeast(0) ?: 0

        val salesScore = ln((salesPoint + 1).toDouble()) / ln(10001.0)
        val reviewScore = reviewRank / 10.0

        return (salesScore * 0.7 + reviewScore * 0.3).coerceIn(0.0, 1.0)
    }

    private fun tokenize(values: List<String>): Set<String> {
        return values
            .flatMap { value ->
                value.lowercase()
                    .split(Regex("[^0-9a-zA-Z가-힣]+"))
                    .filter { it.length >= 2 }
            }
            .toSet()
    }

    private fun parseDate(value: String?): LocalDate? {
        if (value.isNullOrBlank()) {
            return null
        }

        return try {
            LocalDate.parse(value, DateTimeFormatter.ISO_DATE)
        } catch (_: DateTimeParseException) {
            null
        }
    }

    companion object {
        const val MAX_CANDIDATES_BEFORE_RANKING = 20
        const val TOP_RANKED_RESULTS = 5
    }
}
