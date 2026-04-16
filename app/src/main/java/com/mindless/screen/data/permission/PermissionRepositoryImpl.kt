package com.mindless.screen.data.permission

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.mindless.screen.domain.model.PermissionCapability
import com.mindless.screen.domain.repository.PermissionRepository

class PermissionRepositoryImpl(
    private val context: Context? = null,
    initialGrants: Map<PermissionCapability, Boolean> = emptyMap()
) : PermissionRepository {

    private val grants = initialGrants.toMutableMap()

    override fun isGranted(capability: PermissionCapability): Boolean {
        val override = grants[capability]
        if (override != null) {
            return override
        }

        val appContext = context ?: return false

        return when (capability) {
            PermissionCapability.USAGE_ACCESS -> hasUsageAccess(appContext)
            PermissionCapability.ACCESSIBILITY_SERVICE -> hasAccessibilityService(appContext)
            PermissionCapability.POST_NOTIFICATIONS -> hasPostNotifications(appContext)
            PermissionCapability.RECEIVE_BOOT_COMPLETED -> true
        }
    }

    private fun hasUsageAccess(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }

        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun hasAccessibilityService(context: Context): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        return enabledServices.contains(context.packageName)
    }

    private fun hasPostNotifications(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
