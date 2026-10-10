# AppGym — Rutinas de gimnasio para varón (iPhone)

App SwiftUI (iOS 17+):

- **Hoy**: rutina del día con muñeco (frente/espalda) que ilumina los músculos trabajados, checklist y progreso.
- **Ejercicio**: músculo principal/secundario, series, repeticiones, descanso con temporizador, pasos y consejo técnico.
- **Semana**: 6 días de entrenamiento + descanso activo.
- **Alimentación**: calorías y macros (Mifflin-St Jeor) según objetivo, plato balanceado y menú ejemplo.

## Cómo abrirla
1. Mac con Xcode 15+ (o Swift Playgrounds 4.4+).
2. Abre la carpeta (`Package.swift`) en Xcode.
3. Elige un iPhone (simulador o físico) y pulsa **Run**. En iPhone físico: Signing → tu Apple ID.

Los gráficos corporales se dibujan en código. Para fotos/GIFs reales de cada ejercicio, agrégalos a un Asset Catalog y muéstralos en `ExerciseDetailView`.
