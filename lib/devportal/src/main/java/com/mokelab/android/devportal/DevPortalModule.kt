package com.mokelab.android.devportal

import com.mokelab.android.devportal.api.DevPortalNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

object DevPortal

@Module
@InstallIn(ActivityRetainedComponent::class)
object DevPortalModule {
    @Provides
    fun provideNavigator(): DevPortalNavigator {
        return DevPortalNavigator(startDestination = DevPortal)
    }
}