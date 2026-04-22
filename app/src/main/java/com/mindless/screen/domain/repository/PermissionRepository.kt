package com.mindless.screen.domain.repository

import com.mindless.screen.domain.model.PermissionCapability

interface PermissionRepository {
    fun isGranted(capability: PermissionCapability): Boolean
}
