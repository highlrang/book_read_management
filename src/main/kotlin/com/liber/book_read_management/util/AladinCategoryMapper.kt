package com.liber.book_read_management.util

import com.liber.book_read_management.enums.CategoryGroup

object AladinCategoryMapper {

    private val representativeCid = mapOf(
        CategoryGroup.LITERATURE to 1,
        CategoryGroup.SOCIAL_SCIENCE to 798,
        CategoryGroup.SELF_HELP to 336,
        CategoryGroup.BUSINESS_ECONOMY to 170,
        CategoryGroup.SCIENCE to 987,
        CategoryGroup.TECHNOLOGY to 351,
        CategoryGroup.HUMANITIES to 656,
        CategoryGroup.ART_CULTURE to 517,
        CategoryGroup.HISTORY to 74,
        CategoryGroup.HEALTH_LIFE to 558,
        CategoryGroup.CHILDREN to 1108,
        CategoryGroup.RELIGION to 1230,
        CategoryGroup.EDUCATION to 1100,
        CategoryGroup.FOREIGN_LANGUAGE to 1322
    )

    fun getCategoryIdOrNull(group: CategoryGroup?): Int? {
        if (group == null || group == CategoryGroup.UNKNOWN) return null
        return representativeCid[group]
    }
}
