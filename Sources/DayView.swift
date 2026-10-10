import SwiftUI

struct DayView: View {
    let day: WorkoutDay
    @EnvironmentObject private var store: ProgressStore

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                hero
                if day.isRest { restCard }
                ForEach(day.exercises) { ex in
                    ZStack(alignment: .topTrailing) {
                        NavigationLink { ExerciseDetailView(day: day, exercise: ex) } label: { ExerciseCard(exercise: ex) }
                            .buttonStyle(.plain)
                        Button { withAnimation { store.toggle(day, ex) } } label: {
                            Image(systemName: store.isDone(day, ex) ? "checkmark.circle.fill" : "circle")
                                .font(.title2)
                                .foregroundStyle(store.isDone(day, ex) ? Color.green : Color.white.opacity(0.8))
                                .padding(12)
                        }
                    }
                }
            }
            .padding()
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle(day.weekday)
        .navigationBarTitleDisplayMode(.inline)
    }

    private var hero: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(day.index == WorkoutDay.todayIndex ? "RUTINA DE HOY" : day.weekday.uppercased())
                .font(.caption.weight(.heavy)).opacity(0.8)
            Text(day.title).font(.largeTitle.weight(.black))
            if !day.muscles.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack { ForEach(day.muscles) { MuscleChip(muscle: $0) } }
                }
                ProgressView(value: store.progress(day)).tint(.white)
                Text("\(Int(store.progress(day) * 100))% completado • \(day.exercises.count) ejercicios")
                    .font(.footnote).opacity(0.85)
                BodyMapView(primary: Set(day.muscles))
                    .frame(height: 260)
            }
        }
        .foregroundStyle(.white)
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(day.gradient, in: RoundedRectangle(cornerRadius: 28))
    }

    private var restCard: some View {
        VStack(spacing: 10) {
            Image(systemName: "bed.double.fill").font(.system(size: 44)).foregroundStyle(.indigo)
            Text("Día de recuperación").font(.title3.weight(.bold))
            Text("Camina 30-40 min, estira 10 min y duerme 7-9 horas. Los músculos crecen mientras descansas.")
                .multilineTextAlignment(.center).foregroundStyle(.secondary)
        }
        .padding(24).frame(maxWidth: .infinity)
        .background(Color(.secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: 22))
    }
}

struct ExerciseCard: View {
    let exercise: Exercise

    var body: some View {
        HStack(spacing: 14) {
            ZStack {
                exercise.gradient
                Image(systemName: exercise.primary.symbol)
                    .font(.system(size: 34)).foregroundStyle(.white)
            }
            .frame(width: 84, height: 84)
            .clipShape(RoundedRectangle(cornerRadius: 18))

            VStack(alignment: .leading, spacing: 6) {
                Text(exercise.name).font(.headline).multilineTextAlignment(.leading)
                Text("\(exercise.sets) series × \(exercise.reps)").font(.subheadline).foregroundStyle(.secondary)
                MuscleChip(muscle: exercise.primary)
            }
            Spacer(minLength: 28)
        }
        .padding(12)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: 22))
    }
}
