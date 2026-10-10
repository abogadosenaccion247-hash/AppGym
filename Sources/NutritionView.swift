import SwiftUI

struct NutritionView: View {
    @AppStorage("weight") private var weight = 75
    @AppStorage("height") private var height = 175
    @AppStorage("age") private var age = 30
    @AppStorage("activity") private var activity = 1
    @AppStorage("goal") private var goal = 1

    private let activities = ["Ligera (3 días)", "Moderada (4-5 días)", "Alta (6 días)"]
    private let factors: [Double] = [1.375, 1.55, 1.725]
    private let goals = ["Definir", "Mantener", "Ganar músculo"]
    private let adjust: [Double] = [-400, 0, 300]

    private var tdee: Double {
        (10 * Double(weight) + 6.25 * Double(height) - 5 * Double(age) + 5) * factors[activity]
    }
    private var kcal: Double { tdee + adjust[goal] }
    private var protein: Double { Double(weight) * 2.0 }
    private var fat: Double { Double(weight) * 0.9 }
    private var carbs: Double { max(0, (kcal - protein * 4 - fat * 9) / 4) }

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                profileCard
                caloriesCard
                plateCard
                mealsCard
                Text("Guía orientativa (fórmula Mifflin-St Jeor). Consulta a un nutricionista si tienes condiciones médicas.")
                    .font(.caption).foregroundStyle(.secondary).multilineTextAlignment(.center)
            }
            .padding()
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle("Alimentación")
    }

    private var profileCard: some View {
        card("Tu perfil") {
            Stepper("Peso: \(weight) kg", value: $weight, in: 40...200)
            Stepper("Estatura: \(height) cm", value: $height, in: 140...220)
            Stepper("Edad: \(age) años", value: $age, in: 15...80)
            Picker("Actividad", selection: $activity) {
                ForEach(0..<3, id: \.self) { Text(activities[$0]).tag($0) }
            }
            Picker("Objetivo", selection: $goal) {
                ForEach(0..<3, id: \.self) { Text(goals[$0]).tag($0) }
            }.pickerStyle(.segmented)
        }
    }

    private var caloriesCard: some View {
        VStack(spacing: 14) {
            Text("META DIARIA").font(.caption.weight(.heavy)).opacity(0.8)
            Text("\(Int(kcal)) kcal").font(.system(size: 44, weight: .black))
            HStack(spacing: 10) {
                macro("Proteína", protein, "🥩")
                macro("Carbohidratos", carbs, "🍚")
                macro("Grasas", fat, "🥑")
            }
            Label("Agua: \(String(format: "%.1f", Double(weight) * 0.035)) L al día", systemImage: "drop.fill")
                .font(.subheadline.weight(.semibold))
        }
        .foregroundStyle(.white)
        .padding(20).frame(maxWidth: .infinity)
        .background(LinearGradient(colors: [.orange, .red, .black.opacity(0.85)], startPoint: .topLeading, endPoint: .bottomTrailing),
                    in: RoundedRectangle(cornerRadius: 28))
    }

    private func macro(_ name: String, _ grams: Double, _ emoji: String) -> some View {
        VStack(spacing: 4) {
            Text(emoji).font(.title)
            Text("\(Int(grams)) g").font(.headline)
            Text(name).font(.caption2).minimumScaleFactor(0.7).lineLimit(1)
        }
        .frame(maxWidth: .infinity).padding(.vertical, 10)
        .background(.white.opacity(0.18), in: RoundedRectangle(cornerRadius: 14))
    }

    private var plateCard: some View {
        card("El plato balanceado") {
            HStack(spacing: 8) {
                plateBlock("½", "Verduras y frutas", "🥗", .green)
                plateBlock("¼", "Proteína magra", "🍗", .red)
                plateBlock("¼", "Cereales integrales", "🍠", .orange)
            }
            Text("Añade grasas saludables (palta, frutos secos, aceite de oliva) en porciones pequeñas.")
                .font(.footnote).foregroundStyle(.secondary)
        }
    }

    private func plateBlock(_ frac: String, _ name: String, _ emoji: String, _ c: Color) -> some View {
        VStack(spacing: 4) {
            Text(emoji).font(.largeTitle)
            Text(frac).font(.title3.weight(.black))
            Text(name).font(.caption2).multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity).padding(.vertical, 12)
        .background(c.opacity(0.25), in: RoundedRectangle(cornerRadius: 14))
    }

    private var mealsCard: some View {
        card("Ejemplo de día") {
            ForEach(meals, id: \.0) { m in
                HStack(alignment: .top, spacing: 12) {
                    Text(m.1).font(.title)
                    VStack(alignment: .leading, spacing: 2) {
                        Text(m.0).font(.headline)
                        Text(m.2).font(.subheadline).foregroundStyle(.secondary)
                    }
                }
            }
        }
    }

    private let meals: [(String, String, String)] = [
        ("Desayuno", "🍳", "3 huevos revueltos, avena con plátano, papaya y té verde."),
        ("Media mañana", "🥜", "Yogur natural con frutos secos y una fruta de temporada."),
        ("Almuerzo", "🍗", "Pollo o pescado (bonito, jurel), quinua o arroz integral, ensalada grande y aceite de oliva."),
        ("Pre-entreno (1 h antes)", "🍌", "Camote o pan integral con mantequilla de maní y un plátano."),
        ("Post-entreno", "🥛", "Batido de proteína con leche o agua y una fruta; o atún con galletas de arroz."),
        ("Cena", "🥗", "Lentejas o menestras con huevo cocido, verduras al vapor y palta."),
        ("Antes de dormir", "🧀", "Queso fresco o yogur griego.")
    ]

    private func card<Content: View>(_ title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title).font(.headline)
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading).padding(16)
        .background(Color(.secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: 20))
    }
}
