
package io.silv.moviemp.data.network.ratelimit

expect fun nanoTime(): Long

internal interface  Ticker {
    fun read(): Long

    companion object {
        /**
         * A ticker that reads the current time using [System.nanoTime].
         *
         * @since 10.0
         */
        fun systemTicker(): Ticker {
            return SYSTEM_TICKER
        }

        private val SYSTEM_TICKER: Ticker = object : Ticker {

            override fun read(): Long {
                return nanoTime()
            }
        }
    }
}


