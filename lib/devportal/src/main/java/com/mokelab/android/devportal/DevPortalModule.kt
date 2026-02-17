package com.mokelab.android.devportal

import com.mokelab.android.devportal.api.DevPortalNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

object DevPortal

@Module
@InstallIn(ActivityRetainedComponent::class)
object DevPortalModule {
    @Provides
    @ActivityRetainedScoped
    fun provideNavigator(): DevPortalNavigator {
        return DevPortalNavigator(startDestination = DevPortal)
    }
}