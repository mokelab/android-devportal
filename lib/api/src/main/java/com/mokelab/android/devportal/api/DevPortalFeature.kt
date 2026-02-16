package com.mokelab.android.devportal.api

import androidx.navigation3.runtime.EntryProviderScope

interface DevPortalFeature {
    val name: String
    val installer : EntryProviderScope<Any>.() -> Unit
    val root: Any
}