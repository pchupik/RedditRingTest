package org.chupik.redditringtest

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
public class ApplicationModule(private val appContext: Context) {

    @Provides
    fun providesContext() : Context = appContext
}