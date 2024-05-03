package io.silv.moviemp.data.network.ratelimit

import kotlin.time.TimeSource

val time = TimeSource.Monotonic.markNow()

actual fun nanoTime(): Long = time.elapsedNow().inWholeNanoseconds