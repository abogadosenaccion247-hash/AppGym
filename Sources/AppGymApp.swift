import SwiftUI

@main
struct AppGymApp: App {
    @StateObject private var store = ProgressStore()

    var body: some Scene {
        WindowGroup {
            TabView {
                NavigationStack { DayView(day: WorkoutData.today) }
                    .tabItem { Label("Hoy", systemImage: "flame.fill") }
                NavigationStack { WeekView() }
                    .tabItem { Label("Semana", systemImage: "calendar") }
                NavigationStack { NutritionView() }
                    .tabItem { Label("Nutrición", systemImage: "fork.knife") }
            }
            .environmentObject(store)
            .tint(.orange)
            .preferredColorScheme(.dark)
        }
    }
}
