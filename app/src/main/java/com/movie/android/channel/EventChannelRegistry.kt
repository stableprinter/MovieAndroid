package com.movie.android.channel

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import com.movie.android.Constants

/**
 * Registers EventChannel with every Flutter engine. Use [send] to push events from native to Flutter.
 * Call [register] for each engine after creation.
 */
object EventChannelRegistry {

    private val sinks = mutableMapOf<String, EventChannel.EventSink>()
    private val lock = Any()

    /**
     * Registers the event channel with the given engine.
     */
    fun register(engine: FlutterEngine, engineId: String) {
        EventChannel(engine.dartExecutor.binaryMessenger, Constants.EventChannel.NAME)
            .setStreamHandler(object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                    synchronized(lock) { sinks[engineId] = events }
                }

                override fun onCancel(arguments: Any?) {
                    synchronized(lock) { sinks.remove(engineId) }
                }
            })
    }

    /**
     * Registers event channels with both browse and favorite engines.
     */
    fun registerAll(browseEngine: FlutterEngine, favoriteEngine: FlutterEngine) {
        register(browseEngine, Constants.Engine.ID_BROWSE)
        register(favoriteEngine, Constants.Engine.ID_FAVORITE)
    }

    /**
     * Sends an event to the given engine's listener as a map with "method" and "param".
     * No-op if no listener is active.
     */
    fun send(engineId: String, method: String, param: Any? = null) {
        synchronized(lock) {
            sinks[engineId]?.success(mapOf("method" to method, "param" to param))
        }
    }

    /**
     * Cleans up when an engine is destroyed. Call from Activity.onDestroy for extra engines.
     */
    fun unregister(engineId: String) {
        synchronized(lock) { sinks.remove(engineId) }
    }
}
