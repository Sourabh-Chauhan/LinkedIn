import {type Dispatch, type SetStateAction, useState} from "react";
import Box from "../../components/Box/Box";
import Button from "../../../../components/Button/Button";
import Input from "../../../../components/Input/Input";
import classes from "./ResetPassword.module.scss";
import {usePageTitle} from "../../../../hooks/usePageTitle.tsx";
import {type NavigateFunction, useNavigate} from "react-router-dom";
import {request} from "../../../../utils/api.ts";


interface ResetEmailFormProps {
    errorMessage: String,
    isLoading: boolean,
    setIsLoading: Dispatch<SetStateAction<boolean>>,
    setEmail: Dispatch<SetStateAction<string>>
    navigate: NavigateFunction
    sendPasswordResetToken: (token: string) => Promise<void>
}


const ResetEmailForm = ({
                            errorMessage,
                            isLoading,
                            setIsLoading,
                            setEmail,
                            navigate,
                            sendPasswordResetToken,
                        }: ResetEmailFormProps) => {

    return (
        <>
            <form onSubmit={async (e) => {
                e.preventDefault();
                setIsLoading(true);
                const email = e.currentTarget.email.value;
                await sendPasswordResetToken(email);
                setEmail(email);
                setIsLoading(false);
            }}>
                <p>
                    Enter your email and we’ll send a verification code if it matches an
                    existing LinkedIn account.
                </p>

                <Input key="email" name="email" type="email" label="Email"/>
                <p style={{color: "red"}}>{errorMessage}</p>

                <Button type="submit" disabled={isLoading}> Next </Button>
                <Button
                    outline
                    onClick={() => {
                        navigate("/");
                    }}
                    disabled={isLoading}
                >
                    Back
                </Button>
            </form>

        </>
    );
};


interface ResetPasswordFormProps {
    isLoading: boolean,
    setIsLoading: Dispatch<SetStateAction<boolean>>,
    email: string,
    setEmailSent: Dispatch<SetStateAction<boolean>>
    errorMessage: String,
    setErrorMessage: Dispatch<SetStateAction<string>>
    resetPassword: (email: string, code: string, password: string) => Promise<void>,
}


const ResetPasswordForm = ({
                               isLoading,
                               setIsLoading,
                               email,
                               setEmailSent,
                               errorMessage,
                               setErrorMessage,
                               resetPassword,
                           }: ResetPasswordFormProps) => {

    return (
        <>
            <form
                onSubmit={async (e) => {
                    e.preventDefault();
                    setIsLoading(true);
                    const code = e.currentTarget.code.value;
                    const password = e.currentTarget.password.value;
                    const Re_password = e.currentTarget.Re_password.value;
                    if (password !== Re_password) {
                        setErrorMessage("Passwords don't match")
                        // throw new Error("Passwords don't match");
                        setIsLoading(false)
                        return
                    }
                    await resetPassword(email, code, password);
                    setIsLoading(false);
                }}
            >
                <p>Enter the verification code we sent to your email and your new password.</p>
                <Input type="text" label="Verification code" key="code" name="code"/>
                <Input
                    label="New password"
                    name="password"
                    key="password"
                    type="password"
                    id="password"
                />
                <Input
                    label="Re-Enter password"
                    name="Re_password"
                    key="Re_password"
                    type="Re_password"
                    id="Re_password"
                />
                <p style={{color: "red"}}>{errorMessage}</p>
                <Button type="submit" disabled={isLoading}>
                    {isLoading ? "..." : "Reset Password"}
                </Button>
                <Button
                    outline
                    type="button"
                    onClick={() => {
                        setEmailSent(false);
                        setErrorMessage("");
                    }}
                    disabled={isLoading}
                >
                    {isLoading ? "..." : "Back"}
                </Button>
            </form>

        </>
    );
};


const ResetPassword = () => {
    const [emailSent, setEmailSent] = useState(false);
    const [email, setEmail] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    usePageTitle("Reset Password");

    const sendPasswordResetToken = async (email: string) => {
        await request<void>({
            endpoint: `/api/v1/authentication/send-password-reset-token?email=${email}`,
            method: "PUT",
            onSuccess: () => {
                setErrorMessage("");
                setEmailSent(true);
            },
            onFailure: (error) => {
                setErrorMessage(error);
            },
        });
        setIsLoading(false);
    };

    const resetPassword = async (email: string, code: string, password: string) => {
        await request<void>({
            endpoint: `/api/v1/authentication/reset-password?email=${email}&token=${code}&newPassword=${password}`,
            method: "PUT",
            onSuccess: () => {
                setErrorMessage("");
                navigate("/login");
            },
            onFailure: (error) => {
                setErrorMessage(error);
            },
        });
        setIsLoading(false);
    };

    return (
        <div className={classes.root}>
            <Box>
                <h1>Reset your password</h1>

                {!emailSent ?

                    <ResetEmailForm errorMessage={errorMessage}
                                    isLoading={isLoading}
                                    setIsLoading={setIsLoading}
                                    setEmail={setEmail}
                                    navigate={navigate}
                                    sendPasswordResetToken={sendPasswordResetToken}


                    />
                    : <ResetPasswordForm

                        isLoading={isLoading}
                        setIsLoading={setIsLoading}
                        email={email}
                        setEmailSent={setEmailSent}
                        errorMessage={errorMessage}
                        setErrorMessage={setErrorMessage}
                        resetPassword={resetPassword}


                    />}


            </Box>
        </div>
    );
};

export default ResetPassword;
