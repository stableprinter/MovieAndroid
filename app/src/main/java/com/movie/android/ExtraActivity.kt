package com.movie.android

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import io.flutter.embedding.android.FlutterFragment
import com.movie.android.channel.EventChannelRegistry
import io.flutter.embedding.engine.FlutterEngineCache

/**
 * Placeholder activity that displays a Flutter view. The engine must be cached before
 * starting this activity. Use [EXTRA_ENGINE_ID] to specify which cached engine to show.
 */
class ExtraActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extra)

        val engineId = intent.getStringExtra(EXTRA_ENGINE_ID)
        if (engineId.isNullOrEmpty()) {
            finish()
            return
        }

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    FlutterFragment.withCachedEngine(engineId).build(),
                    null
                )
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        intent.getStringExtra(EXTRA_ENGINE_ID)?.let { engineId ->
            EventChannelRegistry.unregister(engineId)
            FlutterEngineCache.getInstance().remove(engineId)
        }
    }

    companion object {
        const val EXTRA_ENGINE_ID = "engine_id"
    }
}
