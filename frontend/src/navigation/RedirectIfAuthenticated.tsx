
import {Navigate, Outlet, useLocation} from "react-router-dom";
import {useAuthentication} from "../features/authentication/contexts/AuthenticationContextProvider.tsx";

const RedirectIfAuthenticated = () => {
    const location = useLocation();
    const {user} = useAuthentication();

    if (user) {
        return <Navigate to={location.state?.from || "/"}/>;
    }

    return <Outlet/>
};

export default RedirectIfAuthenticated;
