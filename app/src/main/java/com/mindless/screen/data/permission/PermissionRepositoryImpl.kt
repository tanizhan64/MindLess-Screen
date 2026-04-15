package com.mindless.screen.data.permission

import com.mindless.screen.domain.model.PermissionCapability
import com.mindless.screen.domain.repository.PermissionRepository

class PermissionRepositoryImpl(
    initialGrants: Map<PermissionCapability, Boolean> = emptyMap()
) : PermissionRepository {

    private val grants = initialGrants.toMutableMap()

    override fun isGranted(capability: PermissionCapability): Boolean {
        return grants[capability] ?: false
    }
}
