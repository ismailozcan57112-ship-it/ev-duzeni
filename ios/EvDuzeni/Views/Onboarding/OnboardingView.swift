import SwiftUI

struct OnboardingView: View {
    @ObservedObject var viewModel: PlannerViewModel
    @State private var currentStep = 0
    
    private let slides = [
        (icon: "house.fill", titleKey: "onboarding_1_title", descKey: "onboarding_1_desc"),
        (icon: "calendar", titleKey: "onboarding_2_title", descKey: "onboarding_2_desc"),
        (icon: "bell.badge.fill", titleKey: "onboarding_3_title", descKey: "onboarding_3_desc")
    ]
    
    var body: some View {
        VStack {
            HStack {
                Spacer()
                Button(loc("onboarding_btn_skip")) {
                    viewModel.setOnboardingDone(true)
                }
                .font(.system(size: 15, weight: .medium))
                .foregroundColor(AppColors.slateMuted)
                .padding(20)
            }
            
            Spacer()
            
            TabView(selection: $currentStep) {
                ForEach(0..<slides.count, id: \.self) { i in
                    VStack(spacing: 20) {
                        Image(systemName: slides[i].icon)
                            .font(.system(size: 64))
                            .foregroundColor(AppColors.sageGreen)
                            .frame(height: 100)
                        
                        Text(loc(slides[i].titleKey))
                            .font(.system(size: 26, weight: .bold))
                            .foregroundColor(AppColors.forestInk)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 24)
                        
                        Text(loc(slides[i].descKey))
                            .font(.system(size: 15))
                            .foregroundColor(AppColors.slateMuted)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 32)
                    }
                    .tag(i)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .always))
            .frame(height: 380)
            
            Spacer()
            
            VStack(spacing: 12) {
                if currentStep < slides.count - 1 {
                    Button(action: {
                        withAnimation { currentStep += 1 }
                    }) {
                        Text(loc("onboarding_btn_next"))
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(AppColors.sageGreen)
                            .cornerRadius(14)
                    }
                } else {
                    Button(action: {
                        viewModel.setupSamplePlan()
                    }) {
                        Text(loc("onboarding_btn_sample_plan"))
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(AppColors.sageGreen)
                            .cornerRadius(14)
                    }
                    
                    Button(action: {
                        viewModel.setOnboardingDone(true)
                    }) {
                        Text(loc("onboarding_btn_start_empty"))
                            .font(.system(size: 15, weight: .medium))
                            .foregroundColor(AppColors.forestInk)
                            .frame(maxWidth: .infinity)
                            .frame(height: 44)
                    }
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .background(AppColors.background.ignoresSafeArea())
    }
}
