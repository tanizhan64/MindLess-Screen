package com.mindless.screen.data.tracking

import com.mindless.screen.domain.usecase.CategorySource
import com.mindless.screen.domain.usecase.ResolvedCategory

class CategoryResolver {
    fun resolve(
        manualOverride: String?,
        builtIn: String?,
        playStore: String?,
        playStoreEnabled: Boolean,
        defaultTools: String
    ): ResolvedCategory {
        val overrideValue = manualOverride?.takeIf { it.isNotBlank() }
        if (overrideValue != null) {
            return ResolvedCategory(overrideValue, CategorySource.USER_OVERRIDE)
        }

        val builtInValue = builtIn?.takeIf { it.isNotBlank() }
        if (builtInValue != null) {
            return ResolvedCategory(builtInValue, CategorySource.BUILT_IN)
        }

        val playStoreValue = playStore?.takeIf { it.isNotBlank() }
        if (playStoreEnabled && playStoreValue != null) {
            return ResolvedCategory(playStoreValue, CategorySource.PLAY_STORE)
        }

        return ResolvedCategory(defaultTools, CategorySource.BUILT_IN)
    }
}
