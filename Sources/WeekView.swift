import SwiftUI

struct WeekView: View {
    @EnvironmentObject private var store: ProgressStore

    var body: some View {
        ScrollView {
            VStack(spacing: 14) {
                ForEach(WorkoutData.week) { day in
                    NavigationLink { DayView(day: day) } label: {
                        HStack(spacing: 14) {
                            VStack(alignment: .leading, spacing: 6) {
                                Text(day.weekday.uppercased()).font(.caption.weight(.heavy)).opacity(0.85)
                                Text(day.title).font(.title3.weight(.bold)).multilineTextAlignment(.leading)
                                Text(day.isRest ? "Recuperación" : day.muscles.map(\.rawValue).joined(separator: " • "))
                                    .font(.footnote).opacity(0.9).multilineTextAlignment(.leading)
                            }
                            Spacer()
                            if day.index == WorkoutDay.todayIndex {
                                Text("HOY").font(.caption.weight(.black))
                                    .padding(.horizontal, 10).padding(.vertical, 4)
                                    .background(.white, in: Capsule()).foregroundStyle(.black)
                            } else if !day.isRest {
                                Image(systemName: store.progress(day) >= 1 ? "checkmark.seal.fill" : "chevron.right")
                            }
                        }
                        .foregroundStyle(.white)
                        .padding(18)
                        .background(day.gradient, in: RoundedRectangle(cornerRadius: 22))
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding()
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle("Tu semana")
    }
}
