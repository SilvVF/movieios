import SwiftUI
import shared

@main
struct iOSApp: App {

    private let assembler = AppAssembler()

    
    init() {
        KoinApplication.start()
    }
    
	var body: some Scene {
		WindowGroup {
            VStack {
                ContentView(assembler: assembler)
            }
		}
	}
}


