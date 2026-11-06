import SwiftUI

extension Color {
    init(hex: UInt, alpha: Double = 1.0) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255,
            green: Double((hex >> 08) & 0xff) / 255,
            blue: Double((hex >> 00) & 0xff) / 255,
            opacity: alpha
        )
    }
}

enum AppColors {
    static let forestInk = Color(hex: 0x213C35)
    static let sageGreen = Color(hex: 0x215C4E)
    static let lightMint = Color(hex: 0xEAF0E6)
    static let warmCream = Color(hex: 0xF6F5F0)
    static let borderLine = Color(hex: 0xE1E7DD)
    static let terracottaOrange = Color(hex: 0xA95830)
    static let slateMuted = Color(hex: 0x697B71)
    static let pureWhite = Color(hex: 0xFFFFFF)
    static let successGreen = Color(hex: 0x2E7D32)
    static let softGold = Color(hex: 0xD4AF37)
    
    static let surface = Color(hex: 0xFFFFFF)
    static let background = Color(hex: 0xF6F5F0)
}
