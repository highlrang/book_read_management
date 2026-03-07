package com.liber.book_read_management.util

import com.liber.book_read_management.enums.CategoryGroup

object CategoryClassifier {

    private data class Rule(
        val group: CategoryGroup,
        val keywords: List<String>
    )

    private val rules = listOf(
        Rule(CategoryGroup.LITERATURE, listOf("문학", "소설", "시", "희곡")),
        Rule(CategoryGroup.SOCIAL_SCIENCE, listOf("사회과학")),
        Rule(CategoryGroup.SELF_HELP, listOf("자기계발")),
        Rule(CategoryGroup.BUSINESS_ECONOMY, listOf("경영", "경제")),
        Rule(CategoryGroup.SCIENCE, listOf("과학")),
        Rule(CategoryGroup.TECHNOLOGY, listOf("컴퓨터/모바일", "컴퓨터", "IT", "기술", "공학")),
        Rule(CategoryGroup.HUMANITIES, listOf("인문", "철학")),
        Rule(CategoryGroup.ART_CULTURE, listOf("예술", "대중문화", "디자인", "음악", "영화")),
        Rule(CategoryGroup.HISTORY, listOf("역사")),
        Rule(CategoryGroup.HEALTH_LIFE, listOf("건강", "취미", "가정", "요리", "여행")),
        Rule(CategoryGroup.CHILDREN, listOf("어린이", "유아", "청소년")),
        Rule(CategoryGroup.RELIGION, listOf("종교")),
        Rule(CategoryGroup.EDUCATION, listOf("교육", "학습")),
        Rule(CategoryGroup.FOREIGN_LANGUAGE, listOf("외국어", "언어"))
    )

    fun classify(categoryPath: String?): CategoryGroup {
        val tokens = categoryPath
            ?.split('>')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: return CategoryGroup.UNKNOWN

        for (rule in rules) {
            if (tokens.any { token -> rule.keywords.any { keyword -> token.contains(keyword) } }) {
                return rule.group
            }
        }

        return CategoryGroup.UNKNOWN
    }
}
