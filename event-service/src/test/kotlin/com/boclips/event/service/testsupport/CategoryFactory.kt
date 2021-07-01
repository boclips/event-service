package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.category.CategoryWithAncestors

object CategoryFactory {
    fun createCategoryWithAncestors(
        code: String = "AB",
        description: String = "Animals",
        ancestors: Set<String> = setOf("A")
    ): CategoryWithAncestors =
        CategoryWithAncestors
            .builder()
            .code(code)
            .description(description)
            .ancestors(ancestors)
            .build()
}