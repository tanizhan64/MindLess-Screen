package com.mindless.screen.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class ResolveCategoryUseCaseTest {

    @Test
    fun manualOverride_hasHighestPriority() {
        val useCase = ResolveCategoryUseCase()

        val resolved = useCase(
            manualOverride = "productivity",
            builtIn = "entertainment",
            playStore = "education",
            playStoreEnabled = true,
            defaultTools = "tools"
        )

        assertEquals("productivity", resolved.value)
        assertEquals(CategorySource.USER_OVERRIDE, resolved.source)
    }

    @Test
    fun fallsBackToDefaultToolsWithBuiltInSource_whenInputsAreAbsent() {
        val useCase = ResolveCategoryUseCase()

        val resolved = useCase(
            manualOverride = null,
            builtIn = null,
            playStore = null,
            playStoreEnabled = true,
            defaultTools = "tools"
        )

        assertEquals("tools", resolved.value)
        assertEquals(CategorySource.BUILT_IN, resolved.source)
    }
}
