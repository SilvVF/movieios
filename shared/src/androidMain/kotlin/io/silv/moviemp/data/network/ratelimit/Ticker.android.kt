package io.silv.moviemp.data.network.ratelimit

actual fun nanoTime(): Long {
    return System.nanoTime()
}