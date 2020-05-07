package org.chupik.redditringtest.di

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
public class ApplicationModule(private val appContext: Context) {

    @Provides
    fun providesContext() : Context = appContext

    @Provides
    fun providesOkHttp() : OkHttpClient = OkHttpClient.Builder().build()
}