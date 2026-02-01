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
    /** Full app config passed to every engine before render. Set in [initialize]. */
    private var _appConfigArgs: List<String> = emptyList()

    val engineGroup: FlutterEngineGroup
        get() = _engineGroup!!

    val browseEngine: FlutterEngine
        get() = FlutterEngineCache.getInstance().get(Constants.Engine.ID_BROWSE)!!

    val favoriteEngine: FlutterEngine
        get() = FlutterEngineCache.getInstance().get(Constants.Engine.ID_FAVORITE)!!

    /**
     * Initializes Flutter and creates both engines with full app config.
     * Must be called before using engines. All engines receive the same config before render.
     */
    fun initialize(
        apiToken: String,
        userId: String,
        baseUrl: String,
        appName: String,
        imageBaseUrl: String
    ) {
        _appConfigArgs = listOf(apiToken, userId, baseUrl, appName, imageBaseUrl)
        val loader = FlutterLoader()
        loader.startInitialization(context)
        loader.ensureInitializationComplete(context, null)

        _engineGroup = FlutterEngineGroup(context)
        val pathToBundle = loader.findAppBundlePath()

        val browseEngine = createEngine(
            pathToBundle = pathToBundle,
            entrypoint = Constants.Navigation.ENTRY_BROWSE,
            args = _appConfigArgs,
            initRoute = "/"
        )

        val favoriteEngine = createEngine(
            pathToBundle = pathToBundle,
            entrypoint = Constants.Navigation.ENTRY_FAVORITE,
            args = _appConfigArgs,
            initRoute = "/"
        )

        FlutterEngineCache.getInstance().put(Constants.Engine.ID_BROWSE, browseEngine)
        FlutterEngineCache.getInstance().put(Constants.Engine.ID_FAVORITE, favoriteEngine)
    }

    /**
     * Creates and caches an engine for ExtraActivity with the same app config as browse/favorite.
     * Uses [engineId] for cache key - must be unique per ExtraActivity instance.
     * [initialize] must have been called first so config is set before this engine runs.
     */
    fun createExtraEngine(engineId: String, entrypoint: String, initRoute: String): FlutterEngine {
        check(_appConfigArgs.isNotEmpty()) {
            "FlutterEngineManager.initialize() must be called first so all engines have app config before render."
        }
        val loader = FlutterLoader()
        loader.startInitialization(context)
        loader.ensureInitializationComplete(context, null)
        val engine = createEngine(
            pathToBundle = loader.findAppBundlePath(),
            entrypoint = entrypoint,
            args = _appConfigArgs,
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
