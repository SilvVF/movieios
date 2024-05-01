package io.silv.moviemp

import org.koin.core.KoinApplication
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

fun initKoinIos(
    userDefaults: NSUserDefaults,
): KoinApplication = initKoin(
    listOf(
        module {
            single { BundleProvider(bundle = NSBundle.mainBundle) }
        }
    )
)

// Workaround class for injecting an `NSObject` class.
// When not used, an error "KClass of Objective-C classes is not supported." is thrown.
data class BundleProvider(val bundle: NSBundle)
