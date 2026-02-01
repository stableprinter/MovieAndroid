package com.movie.android

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.flutter.embedding.android.FlutterFragment
import com.movie.android.channel.EventChannelRegistry
import com.movie.android.channel.MethodChannelRegistry
import com.movie.android.engine.FlutterEngineManager

class MainActivity : FragmentActivity() {

    private lateinit var engineManager: FlutterEngineManager
    private var browseFragment: FlutterFragment? = null
    private var favoriteFragment: FlutterFragment? = null
    private var currentFragment: FlutterFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupFlutterEngines()
        setupFragments()
        setupNavigation()
    }

    private fun setupFlutterEngines() {
        engineManager = FlutterEngineManager(applicationContext)
        engineManager.initialize(AppConfig.API_TOKEN, AppConfig.USER_ID)

        MethodChannelRegistry.registerAll(
            browseEngine = engineManager.browseEngine,
            favoriteEngine = engineManager.favoriteEngine,
            onNavigateToMovieDetail = ::navigateToMovieDetail
        )
        EventChannelRegistry.registerAll(
            browseEngine = engineManager.browseEngine,
            favoriteEngine = engineManager.favoriteEngine
        )
    }

    private fun navigateToMovieDetail(movieId: Int) {
        runOnUiThread {
            val engineId = "${Constants.Engine.ID_EXTRA}_${System.nanoTime()}"
            val engine = engineManager.createExtraEngine(
                engineId = engineId,
                entrypoint = Constants.Navigation.ENTRY_BROWSE,
                args = listOf(
                    AppConfig.API_TOKEN,
                    AppConfig.USER_ID,
                ),
                initRoute = "/movie:true:$movieId"
            )
            MethodChannelRegistry.register(engine, engineId)
            EventChannelRegistry.register(engine, engineId)
            startActivity(Intent(this, ExtraActivity::class.java).apply {
                putExtra(ExtraActivity.EXTRA_ENGINE_ID, engineId)
            })
        }
    }

    private fun setupFragments() {
        val fragmentManager = supportFragmentManager

        browseFragment = findOrCreateFragment(
            fragmentManager = fragmentManager,
            engineId = Constants.Engine.ID_BROWSE,
            tag = Constants.Fragment.TAG_BROWSE
        )
        favoriteFragment = findOrCreateFragment(
            fragmentManager = fragmentManager,
            engineId = Constants.Engine.ID_FAVORITE,
            tag = Constants.Fragment.TAG_FAVORITE,
            hidden = true
        )

        currentFragment = browseFragment
    }

    private fun findOrCreateFragment(
        fragmentManager: FragmentManager,
        engineId: String,
        tag: String,
        hidden: Boolean = false
    ): FlutterFragment {
        var fragment = fragmentManager.findFragmentByTag(tag) as? FlutterFragment
        if (fragment == null) {
            fragment = FlutterFragment.withCachedEngine(engineId).build()
            val transaction = fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, tag)
            if (hidden) transaction.hide(fragment)
            transaction.commit()
        }
        return fragment
    }

    private fun setupNavigation() {
        findViewById<BottomNavigationView>(R.id.bottom_nav).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> switchToFragment(browseFragment!!)
                R.id.nav_list -> switchToFragment(favoriteFragment!!)
            }
            true
        }
    }

    private fun switchToFragment(fragment: FlutterFragment) {
        if (currentFragment == fragment) return
        currentFragment?.let { hide ->
            supportFragmentManager.beginTransaction()
                .show(fragment)
                .hide(hide)
                .commit()
        }
        currentFragment = fragment
    }

    override fun onPostResume() {
        super.onPostResume()
        currentFragment?.onPostResume()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        currentFragment?.onNewIntent(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: super.onBackPressed()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        currentFragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        currentFragment?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        currentFragment?.onUserLeaveHint()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        currentFragment?.onTrimMemory(level)
    }
}
