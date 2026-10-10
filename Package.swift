// swift-tools-version: 5.9
import PackageDescription
import AppleProductTypes

let package = Package(
    name: "AppGym",
    platforms: [.iOS("17.0")],
    products: [
        .iOSApplication(
            name: "AppGym",
            targets: ["AppModule"],
            bundleIdentifier: "com.anayaasociados.appgym",
            teamIdentifier: "",
            displayVersion: "1.0",
            bundleVersion: "1",
            appIcon: .placeholder(icon: .dumbbell),
            accentColor: .presetColor(.orange),
            supportedDeviceFamilies: [.phone],
            supportedInterfaceOrientations: [.portrait]
        )
    ],
    targets: [
        .executableTarget(name: "AppModule", path: "Sources")
    ]
)
