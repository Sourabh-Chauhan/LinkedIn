// import React from "react";
// import { BrowserRouter } from "react-router-dom";
// import { useShallow } from "zustand/react/shallow";
//
// import { useAuthStore } from "~/store/auth.store";
// import { useOnboardingStore } from "~/store/onboarding.store";
// import { PublicRoutes } from "./public-routes";
// import { PrivateRoutes } from "./protected-routes";
// import { OnboardingRoutes } from "./onboarding-routes";
//
// export const AppRoutes = () => {
//   const isAuthenticated = useAuthStore(
//     useShallow((state) => state.isAuthenticated)
//   );
//
//   // selecting onboarding status
//   const { data: onboardingData } = useOnboardingStore();
//
//   // render routes based on authentication and onboarding status
//   return !isAuthenticated ? (
//     <PublicRoutes />
//   ) : !onboardingData.isComplete ? (
//     <OnboardingRoutes />
//   ) : (
//     <PrivateRoutes />
//   );
// };
