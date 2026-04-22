package com.mindless.screen.domain.usecase

import com.mindless.screen.data.tracking.CategoryResolver

class ResolveCategoryUseCase(
    private val categoryResolver: CategoryResolver = CategoryResolver()
) {
    operator fun invoke(
        manualOverride: String?,
        builtIn: String?,
        playStore: String?,
        playStoreEnabled: Boolean,
        defaultTools: String = "tools"
    ): ResolvedCategory {
        return categoryResolver.resolve(
            manualOverride = manualOverride,
            builtIn = builtIn,
            playStore = playStore,
            playStoreEnabled = playStoreEnabled,
            defaultTools = defaultTools
        )
    }
}

data class ResolvedCategory(
    val value: String,
    val source: CategorySource
)

enum class CategorySource {
    USER_OVERRIDE,
    BUILT_IN,
    PLAY_STORE
}
