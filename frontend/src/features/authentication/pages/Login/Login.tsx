import {type FormEvent, useState} from "react";
import {Link, useLocation, useNavigate} from "react-router-dom";

import Box from "../../components/Box/Box";
import Input from "../../../../components/Input/Input";
import Button from "../../../../components/Button/Button";
import Separator from "../../components/Separator/Separator";

import {useAuthentication} from "../../contexts/AuthenticationContextProvider";
import {usePageTitle} from "../../../../hooks/usePageTitle.tsx";

import classes from "./Login.module.scss";

const Login = () => {
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const {login} = useAuthentication();
    const location = useLocation();
    const navigate = useNavigate();
    // const {isOauthInProgress, oauthError, startOauth} = useOauth("login");
    usePageTitle("Login");

    const doLogin = async (e: FormEvent<HTMLFormElement>) => {
        console.log(e)
        e.preventDefault();
        setIsLoading(true);
        const email = e.currentTarget.email.value;
        const password = e.currentTarget.password.value;
        try {
            await login(email, password);
            const destination = location.state?.from || "/";
            console.log(destination)
            navigate(destination);
        } catch (error) {
            if (error instanceof Error) {
                setErrorMessage(error.message);
            } else {
                setErrorMessage("An unknown error occurred.");
            }
        } finally {
            setIsLoading(false);
        }
    }


    return (
        <div className={classes.root}>
            <Box>
                <h1>Sign in</h1>
                <p>Access your professional network.</p>

                <form onSubmit={doLogin}>
                    <Input
                        type="email"
                        id="email"
                        label="Email"
                        onFocus={() => setErrorMessage(null)}
                    />
                    <Input
                        type="password"
                        id="password"
                        label="Password"
                        onFocus={() => setErrorMessage(null)}
                    />
                    {errorMessage && <p className={classes.error}>{errorMessage}</p>}
                    <p className={classes.disclaimer}>
                        By clicking Sign In, you agree to the LinkedIn{" "}
                        <a href="#">User Agreement</a>, <a href="#">Privacy Policy</a>, and{" "}
                        <a href="#">Cookie Policy</a>.
                    </p>
                    <Button type="submit">
                        {isLoading ? "...Please Wait" : "Sign In"}
                    </Button>
                    <Link to="/authentication/request-password-reset">
                        Forgot password?
                    </Link>
                </form>
                <Separator>Or</Separator>
                {/* <Separator /> */}

                <p>
                    Already on LinkedIn? <Link to="/authentication/signup">Sign up</Link>
                </p>
            </Box>
        </div>
    );
};
export default Login;
