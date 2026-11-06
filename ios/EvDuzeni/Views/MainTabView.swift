import SwiftUI

struct MainTabView: View {
    @ObservedObject var viewModel: PlannerViewModel
    @State private var selectedTab = 0
    @State private var showPromoDialog = false
    
    var body: some View {
        TabView(selection: $selectedTab) {
            TodayView(viewModel: viewModel)
                .tabItem {
                    Label(loc("nav_today"), systemImage: "checkmark.circle.fill")
                }
                .tag(0)
            
            PlanView(viewModel: viewModel)
                .tabItem {
                    Label(loc("nav_plan"), systemImage: "calendar")
                }
                .tag(1)
            
            RoomsView(viewModel: viewModel)
                .tabItem {
                    Label(loc("nav_rooms"), systemImage: "door.left.hand.open")
                }
                .tag(2)
            
            SettingsView(viewModel: viewModel)
                .tabItem {
                    Label(loc("nav_settings"), systemImage: "gearshape.fill")
                }
                .tag(3)
        }
        .tint(AppColors.sageGreen)
        .onAppear {
            if viewModel.preferences.onboardingDone && !viewModel.preferences.promoDialogShown {
                showPromoDialog = true
                viewModel.setPromoDialogShown(true)
            }
        }
        .sheet(isPresented: $showPromoDialog) {
            PromotionalNoticeSheet()
        }
    }
}
