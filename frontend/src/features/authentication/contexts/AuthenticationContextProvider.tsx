import {createContext, type Dispatch, type SetStateAction, useContext, useEffect, useState} from "react";
import {Outlet, useLocation} from "react-router-dom";
import {request} from "../../../utils/api";
import {Loader} from "../../../components/Loader/Loader.tsx";

interface IAuthenticationResponse {
    token: string;
    message: string;
}

export interface IUser {
    id: string;
    email: string;
    emailVerified: boolean;
    firstName?: string;
    lastName?: string;
    company?: string;
    position?: string;
    location?: string;
    profileComplete: boolean;
    profilePicture?: string;
    coverPicture?: string;
    about?: string;
}

interface IAuthenticationContextType {
    user: IUser | null;
    setUser: Dispatch<SetStateAction<IUser | null>>;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
    signup: (email: string, password: string) => Promise<void>;
    isLoading: boolean;
    // oauthLogin: (code: string, page: "login" | "signup") => Promise<void>;
}

const AuthenticationContext = createContext<IAuthenticationContextType | null>(null);

export function useAuthentication() {
    return useContext(AuthenticationContext)!;
}

const AuthenticationContextProvider = () => {
    const location = useLocation();
    const [user, setUser] = useState<IUser | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    const login = async (email: string, password: string) => {
        await request<IAuthenticationResponse>({
            endpoint: '/api/v1/authentication/login',
            method: 'POST',
            contentType: 'application/json',
            body: JSON.stringify({email, password}),
            onSuccess: (res) => {
                localStorage.setItem('token', res.token);
            },
            onFailure: (error) => {
                throw new Error(error)
            }
        })
    }


    // const oauthLogin = async (code: string, page: "login" | "signup") => {
    //     await request<IAuthenticationResponse>({
    //         endpoint: "/api/v1/authentication/oauth/google/login",
    //         method: "POST",
    //         body: JSON.stringify({ code, page }),
    //         onSuccess: ({ token }) => {
    //             localStorage.setItem("token", token);
    //         },
    //         onFailure: (error) => {
    //             throw new Error(error);
    //         },
    //     });
    // };


    const signup = async (email: string, password: string) => {
        await request<IAuthenticationResponse>({
            endpoint: '/api/v1/authentication/register',
            method: 'POST',
            contentType: 'application/json',
            body: JSON.stringify({email, password}),
            onSuccess: (res) => {
                localStorage.setItem('token', res.token);
            },
            onFailure: (error) => {
                throw new Error(error)
            }
        })
    }

    const logout = async () => {
        localStorage.removeItem("token");
        setUser(null);
    };


    useEffect(() => {
        if (user) return;

        setIsLoading(true);
        const fetchUser = async () => {
            await request<IUser>({
                endpoint: "/api/v1/authentication/users/me",
                onSuccess: (data) => setUser(data),
                onFailure: (error) => {
                    console.log(error);
                },
            });
            setIsLoading(false);
        };

        fetchUser()
    }, [location.pathname, user]);

    if (isLoading) {
        return <Loader/>;
    }

    return (
        <AuthenticationContext.Provider value={{
            user,
            login,
            logout,
            signup,
            setUser,
            isLoading
            // oauthLogin
        }}>
            <Outlet/>
        </AuthenticationContext.Provider>
    );
};

export default AuthenticationContextProvider;
