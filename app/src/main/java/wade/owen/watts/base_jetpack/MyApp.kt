package wade.owen.watts.base_jetpack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import wade.owen.watts.base_jetpack.data.local.SharedPrefs

@HiltAndroidApp
class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPrefs.init(this)
    }
}