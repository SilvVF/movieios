import SwiftUI
import shared

@main
struct iOSApp: App {
    
    init() {
        DependencyInjectionKt.doInitKoinIos(userDefaults: UserDefaults())
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
