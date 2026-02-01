package com.movie.android.engine

import android.content.Context
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.embedding.engine.loader.FlutterLoader
import com.movie.android.Constants

/**
 * Manages Flutter engine lifecycle: initialization, creation, and caching.
 */
class FlutterEngineManager(private val context: Context) {

    private var _engineGroup: FlutterEngineGroup? = null
    private var _apiToken: String = ""
    private var _userId: String = ""

    val engineGroup: FlutterEngineGroup
        get() = _engineGroup!!

    val browseEngine: FlutterEngine
        get() = FlutterEngineCache.getInstance().get(Constants.Engine.ID_BROWSE)!!

    val favoriteEngine: FlutterEngine
        get() = FlutterEngineCache.getInstance().get(Constants.Engine.ID_FAVORITE)!!

    /**
     * Initializes Flutter and creates both engines. Must be called before using engines.
     */
    fun initialize(apiToken: String, userId: String) {
        _apiToken = apiToken
        _userId = userId
        val loader = FlutterLoader()
        loader.startInitialization(context)
        loader.ensureInitializationComplete(context, null)

        _engineGroup = FlutterEngineGroup(context)
        val pathToBundle = loader.findAppBundlePath()

        val browseEngine = createEngine(
            pathToBundle = pathToBundle,
            entrypoint = Constants.Navigation.ENTRY_BROWSE,
            args = listOf(apiToken, userId),
            initRoute = "/"
        )

        val favoriteEngine = createEngine(
            pathToBundle = pathToBundle,
            entrypoint = Constants.Navigation.ENTRY_FAVORITE,
            args = listOf(apiToken, userId),
            initRoute = "/"
        )


        FlutterEngineCache.getInstance().put(Constants.Engine.ID_BROWSE, browseEngine)
        FlutterEngineCache.getInstance().put(Constants.Engine.ID_FAVORITE, favoriteEngine)
    }

    /**
     * Creates and caches an engine for ExtraActivity. The engine can display any Flutter view
     * based on [entrypoint] and [args]. Uses [engineId] for cache key - must be unique per
     * ExtraActivity instance to avoid race conditions when navigating quickly (each activity
     * removes only its own engine on destroy).
     */
    fun createExtraEngine(engineId: String, entrypoint: String, initRoute: String, args: List<String>): FlutterEngine {
        val loader = FlutterLoader()
        loader.startInitialization(context)
        loader.ensureInitializationComplete(context, null)
        val engine = createEngine(
            pathToBundle = loader.findAppBundlePath(),
            entrypoint = entrypoint,
            args = args,
            initRoute = initRoute
        )
        FlutterEngineCache.getInstance().put(engineId, engine)
        return engine
    }

    private fun createEngine(
        pathToBundle: String,
        entrypoint: String,
        initRoute: String,
        args: List<String>
    ): FlutterEngine {
        val options = FlutterEngineGroup.Options(context)
            .setInitialRoute(initRoute)
            .setDartEntrypoint(DartExecutor.DartEntrypoint(pathToBundle, entrypoint))
            .setDartEntrypointArgs(args)
        return engineGroup.createAndRunEngine(options)
    }
}
