package com.mindless.screen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_category_map")
data class AppCategoryMapEntity(
    @PrimaryKey
    val packageName: String,
    val category: String
)
