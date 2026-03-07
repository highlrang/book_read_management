package com.liber.book_read_management.util

import com.liber.book_read_management.enums.CategoryGroup

object CategoryLabel {
    fun toKorean(group: CategoryGroup): String = when (group) {
        CategoryGroup.LITERATURE -> "문학"
        CategoryGroup.SOCIAL_SCIENCE -> "사회과학"
        CategoryGroup.SELF_HELP -> "자기계발"
        CategoryGroup.BUSINESS_ECONOMY -> "경영/경제"
        CategoryGroup.SCIENCE -> "과학"
        CategoryGroup.TECHNOLOGY -> "기술/IT"
        CategoryGroup.HUMANITIES -> "인문"
        CategoryGroup.ART_CULTURE -> "예술/문화"
        CategoryGroup.HISTORY -> "역사"
        CategoryGroup.HEALTH_LIFE -> "건강/생활"
        CategoryGroup.CHILDREN -> "아동"
        CategoryGroup.RELIGION -> "종교"
        CategoryGroup.EDUCATION -> "교육"
        CategoryGroup.FOREIGN_LANGUAGE -> "외국어"
        CategoryGroup.UNKNOWN -> "기타"
    }
}
