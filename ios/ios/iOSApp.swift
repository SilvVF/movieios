import SwiftUI
import shared

@main
struct iOSApp: App {
    
    var screenModel: MovieScreenModel

    init() {
        DependencyInjectionKt.doInitKoinIos(userDefaults: UserDefaults())
        screenModel = MovieScreenModel.companion.create()
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
