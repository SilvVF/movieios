import SwiftUI
import NukeUI
import shared
import Combine
import Nuke
import KMPNativeCoroutinesCore
import KMPNativeCoroutinesCombine

struct ContentView: View {
	
    let assembler: Assembler
    
    let greet = Greeting().greet()

    
	var body: some View {
        VStack{
            Movies()
        }
	}
}

@MainActor final class MovieSwiftViewModel: ObservableObject {
    
    let pagingHelper = SwiftUiPagingHelper<Movie>()
    
    @LazyKoin var presenter: MovieBrowsePresenter
    
    @Published var search: String = ""
    
    @Published var items: [Movie] = []
    
    @Published var hasNextPage: Bool = false
    
    @Published var errorMessage: String? = nil

    @Published var showLoadingPlaceholder: Bool = false
    
    @Published var type: ContentPagedType = ContentPagedTypeDefault.Popular()
    
    var cancellables = Set<AnyCancellable>()

    deinit {
        cancellables.forEach { $0.cancel() }
    }
    
    func activatepaging() async {
        $type
            .eraseToAnyPublisher()
            .combineLatest(
                $search
                    .debounce(for: 1, scheduler: RunLoop.main)
                    .eraseToAnyPublisher()
            )
            .removeDuplicates { prev, current in
                let pPaged = prev.0
                let cPaged = current.0
                
                let first =   current.1.lowercased() == prev.1.lowercased()
                
                if pPaged is ContentPagedTypeDefault.Popular {
                    return cPaged is ContentPagedTypeDefault.Popular && first
                }
                if pPaged is ContentPagedTypeDefault.TopRated {
                    return cPaged is ContentPagedTypeDefault.TopRated && first
                }
                if pPaged is ContentPagedTypeDefault.Upcoming {
                    return cPaged is ContentPagedTypeDefault.Upcoming && first
                }
                return false
           }
            .map { t, s in
                if s.isEmpty {
                    createPublisher(for: self.presenter.invoke(type: t.self))
                } else {
                    createPublisher(for: self.presenter.invoke(type: ContentPagedTypeSearch(query: s).self))
                }
            }
            .switchToLatest()
            .sink(receiveCompletion: {_ in }, receiveValue: { pagingData in
                Task.init {
                    try? await self.pagingHelper.submitData(pagingData: pagingData)
                }
            })
            .store(in: &cancellables)
    }
    
    func activateUpdates() async {
        for await _ in pagingHelper.onPagesUpdatedFlow {
            self.items = pagingHelper.getItems()
        }
    }

    func activateLoadState() async {
        for await loadState in pagingHelper.loadStateFlow {
            switch onEnum(of: loadState.append) {
            case .error(let errorState):
                print("loading error \(errorState.description())")
                DispatchQueue.main.asyncAfter(deadline: .now() + 1){
                    self.errorMessage = errorState.error.message
                }
                break
            case .loading(_):
                print("loading l")
                break
            case .notLoading(let notLoadingState):
                print("loading not")
                hasNextPage = !notLoadingState.endOfPaginationReached
                break
            }
            switch onEnum(of: loadState.refresh) {
            case .error(let errorState):
                print("r error \(errorState.description())")
                DispatchQueue.main.asyncAfter(deadline: .now() + 1){ [self] in
                    self.errorMessage = errorState.error.message
                    showLoadingPlaceholder = false
                }
                break
            case .loading(_):
                print("r error")
                showLoadingPlaceholder = true
                break
            case .notLoading(_):
                print("not r error")
                showLoadingPlaceholder = false
                break
            }
        }
    }
}


struct FilterChips: View {
        
    var selected: ContentPagedType
    
    let select: (ContentPagedTypeDefault) -> Void
    
    var body: some View {
        ScrollView(.horizontal) {
            HStack(alignment: .center, spacing: 12) {
                Button(action: { select(ContentPagedTypeDefault.Popular()) }) {
                    Text("Popular")
                }
                .if(selected is ContentPagedTypeDefault.Popular) { view in
                    view.buttonStyle(.borderedProminent)
                }
                Button(action: { select(ContentPagedTypeDefault.TopRated()) }) {
                    Text("TopRated")
                }

                .if(selected is ContentPagedTypeDefault.TopRated) { view in
                    view.buttonStyle(.borderedProminent)
                }
                Button(action: { select(ContentPagedTypeDefault.Upcoming()) }) {
                    Text("Upcoming")
                }
                .if(selected is ContentPagedTypeDefault.Upcoming) { view in
                    view.buttonStyle(.borderedProminent)
                }
            }
            .frame(maxWidth: .infinity)
            .background(.clear)

        }
    }
    
}

struct Movies: View {
    
    private let pipeline = ImagePipeline {
         $0.dataLoader = {
             let config = URLSessionConfiguration.default
             config.urlCache = nil
             return DataLoader(configuration: config)
         }()
     }
    
    @StateObject var viewModel: MovieSwiftViewModel = MovieSwiftViewModel()
    
    var body: some View {
        NavigationStack {
            VStack {
                FilterChips(selected: viewModel.type) { type in
                    viewModel.type = type
                }
                .frame(maxWidth: .infinity)
                .scaledToFit()
                ScrollView(.vertical) {
                    LazyVGrid(columns: [.init(.adaptive(minimum: 180, maximum: .infinity), spacing: 2)], spacing: 2) {
                        ForEach(viewModel.items, id: \.id) { item in
                            MoviePosterView(
                                movie: item,
                                updater: { createPublisher(for: viewModel.presenter.observeMoviePoster(id: item.id)) },
                                pipeline: pipeline
                            )
                        }
                        if(viewModel.hasNextPage && viewModel.errorMessage == nil && !viewModel.items.isEmpty) {
                            ProgressView()
                                .gridColumnAlignment(.center)
                                .onAppear() {
                                    viewModel.pagingHelper.loadNextPage()
                                }
                        }
                        
                        if(viewModel.errorMessage != nil) {
                            Text(viewModel.errorMessage ?? "error")
                        }
                    }
                    .listStyle(.sidebar)
                }
            }
        }
        .searchable(
            text: $viewModel.search
        )
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .task {
            await viewModel.activatepaging()
        }
        .task {
            await viewModel.activateLoadState()
        }
        .task {
            await viewModel.activateUpdates()
        }
    }
}


struct MoviePosterView: View {
    
    @State var movie: Movie
    @State private var opacity: Double = 0.0

    
    let updater: () -> AnyPublisher<Movie, any Error>
    
    let pipeline: ImagePipeline
    
    var body: some View {
        ZStack(alignment: .bottom){
            LazyImage(url: URL(string: movie.posterUrl!)) { phase in
                if let image = phase.image {
                    image
                        .renderingMode(.original)
                        .resizable()
                        .opacity(opacity)
                        .onDisappear {
                            opacity = 0.0
                        }
                        .onAppear {
                            withAnimation(.easeInOut(duration: 1)) {
                                opacity = 1.0
                            }
                        }
                } else if phase.error != nil {
                    Color.red.opacity(30)
                } else {
                    ProgressView()
                }
            }
            .pipeline(pipeline)
            .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
            .aspectRatio(2 / 3, contentMode: .fill)
            .background(.black.opacity(5))

            ZStack(alignment: .center) {
                Text(movie.title)
                    .fontWeight(.semibold)
                    .lineLimit(2)
                    .colorInvert()
                    .frame(maxWidth: .infinity)
            }
                .padding(.vertical, 12)
                .padding(.horizontal, 4)
                .frame(maxWidth: .infinity)
        }
        .clipShape(RoundedRectangle(cornerSize: CGSize(width: 12, height: 12)))
        .padding([.vertical], 4)
        .padding([.horizontal], 8)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .task {
            var cancellable: AnyCancellable? = nil
            cancellable = updater()
                .sink(
                    receiveCompletion: {_ in cancellable?.cancel()},
                    receiveValue: { m in
                        movie = m
                    }
                )
        }
    }
}



extension View {
    /// Applies the given transform if the given condition evaluates to `true`.
    /// - Parameters:
    ///   - condition: The condition to evaluate.
    ///   - transform: The transform to apply to the source `View`.
    /// - Returns: Either the original `View` or the modified `View` if the condition is `true`.
    @ViewBuilder func `if`<Content: View>(_ condition: Bool, transform: (Self) -> Content) -> some View {
        if condition {
            transform(self)
        } else {
            self
        }
    }
}
