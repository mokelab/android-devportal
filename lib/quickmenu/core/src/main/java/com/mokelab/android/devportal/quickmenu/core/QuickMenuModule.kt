package com.mokelab.android.devportal.quickmenu.core

import com.mokelab.android.devportal.quickmenu.api.QuickMenuItem
import dagger.Module
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
abstract class QuickMenuModule {
    /** Allows the set to be empty when no item module is on the classpath. */
    @Multibinds
    abstract fun quickMenuItems(): Set<QuickMenuItem>
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface QuickMenuEntryPoint {
    fun quickMenuItems(): Set<@JvmSuppressWildcards QuickMenuItem>
}
