package com.movie.android

/**
 * Application-wide constants.
 */
object Constants {

    object Engine {
        const val ID_BROWSE = "browse_engine"
        const val ID_FAVORITE = "favorite_engine"
        const val ID_EXTRA = "extra_engine"
    }

    object Fragment {
        const val TAG_BROWSE = "flutter_fragment_browse"
        const val TAG_FAVORITE = "flutter_fragment_favorite"
    }

    object MethodChannel {
        const val NAME = "com.movie.android/channel"
    }

    object EventChannel {
        const val NAME = "com.movie.android/events"
        const val EVENT_SHOULD_RELOAD_FAVORITE = "shouldReloadFavorite"
    }

    object Navigation {
        const val ENTRY_BROWSE = "mainBrowse"
        const val ENTRY_FAVORITE = "mainFavorite"
    }
}
