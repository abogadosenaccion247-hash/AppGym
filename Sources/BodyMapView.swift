import SwiftUI

enum BodySide: String { case front = "FRENTE", back = "ESPALDA" }

private struct Region: Identifiable {
    let id = UUID()
    let side: BodySide
    let muscle: Muscle?
    let rect: CGRect          // coordenadas unitarias (ancho 1, alto 1; el lienzo es 1:2)
    var ellipse = false
}

private struct RegionShape: Shape {
    let rect: CGRect
    let ellipse: Bool
    func path(in r: CGRect) -> Path {
        let box = CGRect(x: r.minX + rect.minX * r.width, y: r.minY + rect.minY * r.height,
                         width: rect.width * r.width, height: rect.height * r.height)
        return ellipse ? Path(ellipseIn: box)
                       : Path(roundedRect: box, cornerRadius: min(box.width, box.height) * 0.35)
    }
}

private func R(_ side: BodySide, _ m: Muscle?, _ x: CGFloat, _ y: CGFloat, _ w: CGFloat, _ h: CGFloat, ellipse: Bool = false) -> Region {
    Region(side: side, muscle: m, rect: CGRect(x: x, y: y, width: w, height: h), ellipse: ellipse)
}

private let regions: [Region] = {
    var r: [Region] = []
    for s in [BodySide.front, .back] {
        r += [R(s, nil, 0.42, 0.02, 0.16, 0.10, ellipse: true),      // cabeza
              R(s, nil, 0.46, 0.11, 0.08, 0.06),                     // cuello
              R(s, nil, 0.29, 0.17, 0.42, 0.33),                     // torso
              R(s, nil, 0.32, 0.47, 0.36, 0.07),                     // pelvis
              R(s, nil, 0.12, 0.57, 0.08, 0.06, ellipse: true),      // manos
              R(s, nil, 0.80, 0.57, 0.08, 0.06, ellipse: true),
              R(s, nil, 0.30, 0.95, 0.15, 0.04),                     // pies
              R(s, nil, 0.55, 0.95, 0.15, 0.04)]
        r += [R(s, .hombros, 0.19, 0.18, 0.11, 0.09, ellipse: true),
              R(s, .hombros, 0.70, 0.18, 0.11, 0.09, ellipse: true),
              R(s, .antebrazos, 0.13, 0.42, 0.08, 0.14),
              R(s, .antebrazos, 0.79, 0.42, 0.08, 0.14),
              R(s, .pantorrillas, 0.31, 0.77, 0.13, 0.18),
              R(s, .pantorrillas, 0.56, 0.77, 0.13, 0.18)]
    }
    // Frente
    r += [R(.front, .pecho, 0.30, 0.20, 0.19, 0.10), R(.front, .pecho, 0.51, 0.20, 0.19, 0.10),
          R(.front, .biceps, 0.17, 0.28, 0.08, 0.13), R(.front, .biceps, 0.75, 0.28, 0.08, 0.13),
          R(.front, .core, 0.40, 0.31, 0.20, 0.17),
          R(.front, .cuadriceps, 0.30, 0.54, 0.17, 0.22), R(.front, .cuadriceps, 0.53, 0.54, 0.17, 0.22)]
    // Espalda
    r += [R(.back, .trapecio, 0.38, 0.14, 0.24, 0.07),
          R(.back, .espalda, 0.30, 0.22, 0.40, 0.16), R(.back, .espalda, 0.36, 0.38, 0.28, 0.08),
          R(.back, .triceps, 0.17, 0.28, 0.08, 0.13), R(.back, .triceps, 0.75, 0.28, 0.08, 0.13),
          R(.back, .gluteos, 0.31, 0.47, 0.38, 0.09),
          R(.back, .isquios, 0.30, 0.57, 0.17, 0.19), R(.back, .isquios, 0.53, 0.57, 0.17, 0.19)]
    return r
}()

/// Muñeco masculino (frente y espalda) con los músculos trabajados iluminados.
struct BodyMapView: View {
    var primary: Set<Muscle>
    var secondary: Set<Muscle> = []

    var body: some View {
        HStack(spacing: 16) {
            side(.front)
            side(.back)
        }
    }

    private func side(_ s: BodySide) -> some View {
        VStack(spacing: 6) {
            Text(s.rawValue).font(.caption2.weight(.bold)).foregroundStyle(.secondary)
            ZStack {
                ForEach(regions.filter { $0.side == s }) { reg in
                    RegionShape(rect: reg.rect, ellipse: reg.ellipse)
                        .fill(fill(reg.muscle))
                        .overlay(RegionShape(rect: reg.rect, ellipse: reg.ellipse).stroke(Color.white.opacity(0.35), lineWidth: 0.6))
                        .shadow(color: glow(reg.muscle), radius: 6)
                }
            }
            .aspectRatio(0.5, contentMode: .fit)
        }
    }

    private func fill(_ m: Muscle?) -> Color {
        guard let m else { return Color.gray.opacity(0.28) }
        if primary.contains(m) { return m.color }
        if secondary.contains(m) { return m.color.opacity(0.45) }
        return Color.gray.opacity(0.28)
    }

    private func glow(_ m: Muscle?) -> Color {
        guard let m, primary.contains(m) else { return .clear }
        return m.color.opacity(0.8)
    }
}

struct MuscleChip: View {
    let muscle: Muscle
    var body: some View {
        Text(muscle.rawValue)
            .font(.caption.weight(.semibold))
            .padding(.horizontal, 10).padding(.vertical, 4)
            .background(muscle.color.opacity(0.9), in: Capsule())
            .foregroundStyle(.white)
    }
}
