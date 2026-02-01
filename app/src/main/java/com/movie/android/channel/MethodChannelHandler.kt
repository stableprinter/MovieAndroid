package com.movie.android.channel

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import com.movie.android.Constants

/**
 * Handles method calls from Flutter (both Browse and Favorite engines).
 * Override [handleMethodCall] or extend to add custom platform methods.
 */
class MethodChannelHandler(
    private val engineId: String,
    private val onNavigateToMovieDetail: ((Int) -> Unit)?
) : MethodCallHandler {

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "getEngineId" -> result.success(engineId)
            "getPlatformVersion" -> result.success(android.os.Build.VERSION.RELEASE)
            "navigateToMovieDetail" -> {
                if (engineId == Constants.Engine.ID_FAVORITE) {
                    val movieId = call.arguments as? Int
                        ?: (call.arguments as? Number)?.toInt()
                        ?: run {
                            result.error("INVALID_ARG", "movieId must be an Int", null)
                            return
                        }
                    onNavigateToMovieDetail?.invoke(movieId)
                    result.success(null)
                } else {
                    result.notImplemented()
                }
            }
            "broadcastFavList" -> {
                if (engineId == Constants.Engine.ID_FAVORITE) {
                    val movieIds = (call.arguments as? List<*>)
                        ?.mapNotNull { (it as? Number)?.toInt() }
                        ?: emptyList<Int>()
                    EventChannelRegistry.send(Constants.Engine.ID_BROWSE, "broadcastFavList", movieIds)
                    result.success(null)
                } else {
                    result.notImplemented()
                }
            }
            "onToggleFavorite" -> {
                // Handle from both Browse tab and movie-detail (extra) engine
                if (engineId == Constants.Engine.ID_BROWSE || engineId.startsWith(Constants.Engine.ID_EXTRA)) {
                    val movieId = call.arguments as? Int
                        ?: (call.arguments as? Number)?.toInt()
                        ?: run {
                            result.error("INVALID_ARG", "movieId must be an Int", null)
                            return
                        }
                    EventChannelRegistry.send(Constants.Engine.ID_FAVORITE, Constants.EventChannel.EVENT_SHOULD_RELOAD_FAVORITE, movieId)
                    result.success(null)
                } else {
                    result.notImplemented()
                }
            }
            else -> result.notImplemented()
        }
    }
}
