package io.silv.moviemp.data.network.ratelimit

import kotlin.system.getTimeNanos
import kotlin.time.TimeSource

actual fun nanoTime(): Long = TimeSource.Monotonic.markNow().elapsedNow().inWholeNanoseconds