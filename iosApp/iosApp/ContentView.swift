import SwiftUI
import shared

struct ContentView: View {
    @MainActor @StateObject private var viewModel: ProgrammingViewModel

    @MainActor
    init() {
        self._viewModel = StateObject(wrappedValue: ProgrammingViewModel())
    }
    
	var body: some View {
        Text("IsScanning: \(viewModel.isScanning ? "YES" : "NO")")
        Button(action: {
            viewModel.scan()
        }) {
            Text("Start Scan")
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
