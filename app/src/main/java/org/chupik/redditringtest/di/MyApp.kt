package org.chupik.redditringtest.di

import android.app.Application
import dagger.Component

import org.chupik.redditringtest.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(activity: MainActivity)
}

class MyApp : Application() {
    val appComponent : ApplicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
}