import Box from "../../components/Box/Box";
import Input from "../../../../components/Input/Input";
import Button from "../../../../components/Button/Button";
import Separator from "../../components/Separator/Separator";
import {Link, useNavigate} from "react-router-dom";
import classes from "./Signup.module.scss";
import {type FormEvent, useState} from "react";
import {useAuthentication} from "../../contexts/AuthenticationContextProvider.tsx";
import {usePageTitle} from "../../../../hooks/usePageTitle.tsx";

const Signup = () => {
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const [isLoading, setIsLoading] = useState(false);
    const {signup} = useAuthentication();
    const navigate = useNavigate();
    usePageTitle("Signup");
    // const { isOauthInProgress, oauthError, startOauth } = useOauth("signup");

    const doSignup = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsLoading(true);
        const email = e.currentTarget.email.value;
        const password = e.currentTarget.password.value;

        try {
            await signup(email, password);
            navigate("/verify-email");
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

    // if (isOauthInProgress) {
    //     return <Loader isInline />;
    // }

    return (
        <div className={classes.root}>
            <Box>
                {/* <header> */}
                <h1>Sign up</h1>
                <p>Make the most of your professional life.</p>
                {/* </header> */}
                <form onSubmit={doSignup}>
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
                        By clicking Agree & Join, you agree to the LinkedIn{" "}
                        <a href="#">User Agreement</a>, <a href="#">Privacy Policy</a>, and{" "}
                        <a href="#">Cookie Policy</a>.
                    </p>
                    <Button type="submit">
                        {isLoading ? "...Please Wait" : "Agree & Join"}
                    </Button>
                </form>
                <Separator>Or</Separator>
                {/* <Separator /> */}

                <p>
                    Already on LinkedIn? <Link to="/authentication/login">Sign in</Link>
                </p>
            </Box>
        </div>
    );
};

export default Signup;
