import SwiftUI

enum Muscle: String, CaseIterable, Identifiable {
    case pecho = "Pecho", espalda = "Espalda", hombros = "Hombros", trapecio = "Trapecio"
    case biceps = "Bíceps", triceps = "Tríceps", antebrazos = "Antebrazos", core = "Abdomen"
    case cuadriceps = "Cuádriceps", isquios = "Isquiotibiales", gluteos = "Glúteos", pantorrillas = "Pantorrillas"

    var id: String { rawValue }

    var color: Color {
        switch self {
        case .pecho: return .red
        case .espalda: return .blue
        case .hombros: return .orange
        case .trapecio: return .cyan
        case .biceps: return .purple
        case .triceps: return .pink
        case .antebrazos: return .brown
        case .core: return .yellow
        case .cuadriceps: return .green
        case .isquios: return .teal
        case .gluteos: return .indigo
        case .pantorrillas: return .mint
        }
    }

    var symbol: String {
        switch self {
        case .core: return "figure.core.training"
        case .cuadriceps, .isquios, .gluteos, .pantorrillas: return "figure.run"
        case .espalda, .trapecio: return "figure.rower"
        default: return "figure.strengthtraining.traditional"
        }
    }
}

struct Exercise: Identifiable {
    var id: String { name }
    let name: String
    let primary: Muscle
    var secondary: [Muscle] = []
    let sets: Int
    let reps: String
    let rest: Int
    let steps: [String]
    let tip: String

    var gradient: LinearGradient {
        LinearGradient(colors: [primary.color, primary.color.opacity(0.55), Color.black.opacity(0.85)],
                       startPoint: .topLeading, endPoint: .bottomTrailing)
    }
}

struct WorkoutDay: Identifiable {
    let index: Int            // 0 = lunes ... 6 = domingo
    let weekday: String
    let title: String
    let exercises: [Exercise]

    var id: Int { index }
    var isRest: Bool { exercises.isEmpty }
    var muscles: [Muscle] {
        var seen: [Muscle] = []
        for e in exercises where !seen.contains(e.primary) { seen.append(e.primary) }
        return seen
    }
    var gradient: LinearGradient {
        let c = muscles.map(\.color)
        let colors = c.isEmpty ? [Color.gray, Color.blue.opacity(0.6)] : c + [Color.black.opacity(0.8)]
        return LinearGradient(colors: colors.count == 1 ? colors + [Color.black.opacity(0.8)] : colors,
                              startPoint: .topLeading, endPoint: .bottomTrailing)
    }

    static var todayIndex: Int {
        (Calendar.current.component(.weekday, from: Date()) + 5) % 7
    }
}

final class ProgressStore: ObservableObject {
    @Published private(set) var done: Set<String>

    init() {
        done = Set(UserDefaults.standard.stringArray(forKey: "done") ?? [])
    }

    private func key(_ day: WorkoutDay, _ ex: Exercise) -> String {
        let f = DateFormatter()
        f.dateFormat = "YYYY-ww"
        return "\(f.string(from: Date()))|\(day.index)|\(ex.name)"
    }

    func isDone(_ day: WorkoutDay, _ ex: Exercise) -> Bool { done.contains(key(day, ex)) }

    func toggle(_ day: WorkoutDay, _ ex: Exercise) {
        let k = key(day, ex)
        if done.contains(k) { done.remove(k) } else { done.insert(k) }
        UserDefaults.standard.set(Array(done), forKey: "done")
    }

    func progress(_ day: WorkoutDay) -> Double {
        guard !day.exercises.isEmpty else { return 0 }
        return Double(day.exercises.filter { isDone(day, $0) }.count) / Double(day.exercises.count)
    }
}
