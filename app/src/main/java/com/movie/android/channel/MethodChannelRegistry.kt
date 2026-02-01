package com.movie.android.channel

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import com.movie.android.Constants

/**
 * Registers MethodChannel with both Flutter engines so each can communicate with native Android.
 */
object MethodChannelRegistry {

    /**
     * Registers the method channel with the given engine.
     * Call this for each engine (browse, favorite) after engine creation.
     */
    fun register(
        engine: FlutterEngine,
        engineId: String,
        onNavigateToMovieDetail: ((Int) -> Unit)? = null
    ) {
        MethodChannel(
            engine.dartExecutor.binaryMessenger,
            Constants.MethodChannel.NAME
        ).setMethodCallHandler(MethodChannelHandler(engineId, onNavigateToMovieDetail))
    }

    /**
     * Registers method channels with both engines.
     * When favorite engine calls "navigateToMovieDetail", [onNavigateToMovieDetail] is invoked.
     */
    fun registerAll(
        browseEngine: FlutterEngine,
        favoriteEngine: FlutterEngine,
        onNavigateToMovieDetail: (Int) -> Unit
    ) {
        register(browseEngine, Constants.Engine.ID_BROWSE)
        register(favoriteEngine, Constants.Engine.ID_FAVORITE, onNavigateToMovieDetail)
    }
}
