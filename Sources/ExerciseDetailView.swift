import SwiftUI

struct ExerciseDetailView: View {
    let day: WorkoutDay
    let exercise: Exercise
    @EnvironmentObject private var store: ProgressStore
    @State private var remaining = 0
    private let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                VStack(spacing: 10) {
                    Text("MÚSCULO TRABAJADO").font(.caption.weight(.heavy)).opacity(0.8)
                    Text(exercise.primary.rawValue).font(.title.weight(.black))
                    BodyMapView(primary: [exercise.primary], secondary: Set(exercise.secondary))
                        .frame(height: 300)
                    HStack {
                        ForEach(exercise.secondary) { m in
                            Text(m.rawValue).font(.caption2.weight(.semibold))
                                .padding(.horizontal, 8).padding(.vertical, 3)
                                .background(.white.opacity(0.2), in: Capsule())
                        }
                    }
                }
                .foregroundStyle(.white)
                .padding(20).frame(maxWidth: .infinity)
                .background(exercise.gradient, in: RoundedRectangle(cornerRadius: 28))

                HStack(spacing: 12) {
                    stat("\(exercise.sets)", "SERIES")
                    stat(exercise.reps, "REPS")
                    stat(exercise.rest > 0 ? "\(exercise.rest)s" : "—", "DESCANSO")
                }

                if exercise.rest > 0 {
                    Button { remaining = exercise.rest } label: {
                        Label(remaining > 0 ? "Descansando… \(remaining) s" : "Iniciar descanso (\(exercise.rest) s)",
                              systemImage: "timer")
                            .frame(maxWidth: .infinity).padding()
                            .background(exercise.primary.color, in: RoundedRectangle(cornerRadius: 16))
                            .foregroundStyle(.white).font(.headline)
                    }
                }

                card("Cómo hacerlo") {
                    ForEach(Array(exercise.steps.enumerated()), id: \.offset) { i, s in
                        HStack(alignment: .top, spacing: 10) {
                            Text("\(i + 1)").font(.caption.weight(.bold)).foregroundStyle(.white)
                                .frame(width: 22, height: 22).background(exercise.primary.color, in: Circle())
                            Text(s)
                        }
                    }
                }
                card("Consejo del entrenador") {
                    Label(exercise.tip, systemImage: "lightbulb.fill").foregroundStyle(.yellow)
                }

                Button { store.toggle(day, exercise) } label: {
                    Label(store.isDone(day, exercise) ? "Completado" : "Marcar como completado",
                          systemImage: store.isDone(day, exercise) ? "checkmark.circle.fill" : "circle")
                        .frame(maxWidth: .infinity).padding()
                        .background(store.isDone(day, exercise) ? Color.green : Color.orange, in: RoundedRectangle(cornerRadius: 16))
                        .foregroundStyle(.white).font(.headline)
                }
            }
            .padding()
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle(exercise.name)
        .navigationBarTitleDisplayMode(.inline)
        .onReceive(timer) { _ in if remaining > 0 { remaining -= 1 } }
    }

    private func stat(_ value: String, _ label: String) -> some View {
        VStack(spacing: 4) {
            Text(value).font(.title3.weight(.heavy)).minimumScaleFactor(0.5).lineLimit(1)
            Text(label).font(.caption2.weight(.bold)).foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity).padding(.vertical, 14)
        .background(Color(.secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: 16))
    }

    private func card<Content: View>(_ title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title).font(.headline)
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading).padding(16)
        .background(Color(.secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: 20))
    }
}
