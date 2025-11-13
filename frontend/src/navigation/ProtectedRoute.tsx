
import {Navigate, Outlet, useLocation} from "react-router-dom";
import {useAuthentication} from "../features/authentication/contexts/AuthenticationContextProvider.tsx";

const ProtectedRoute = () => {
    const location = useLocation();
    const {user} = useAuthentication();

    if (user && !user.emailVerified && location.pathname !== "/authentication/verify-email") {
        return <Navigate to="/authentication/verify-email"/>;
    }

    if (user && user.emailVerified && location.pathname == "/authentication/verify-email") {
        return <Navigate to="/"/>;
    }

    if (
        user &&
        user.emailVerified &&
        !user.profileComplete &&
        !location.pathname.includes("/authentication/profile")
    ) {
        return <Navigate to={`/authentication/profile/${user.id}`}/>;
    }

    if (
        user &&
        user.emailVerified &&
        user.profileComplete &&
        location.pathname.includes("/authentication/profile")
    ) {
        return <Navigate to="/"/>;
    }


    return user ? <Outlet/> : <Navigate to="/authentication/login" state={{from: location.pathname}}/>;
};

export default ProtectedRoute;
