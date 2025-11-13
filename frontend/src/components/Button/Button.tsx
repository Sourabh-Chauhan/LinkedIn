import {type ButtonHTMLAttributes} from "react";
import classes from "./Button.module.scss";

interface IButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    outline?: boolean;
    size?: "small" | "medium" | "large";
}

const Button = ({outline, children, className, size = "large", ...others}: IButtonProps) => {
    return (
        <button
            className={`${classes.button} ${classes[size]} ${
                outline ? classes.outline : ""
            } ${className}`}
            {...others}
        >
            {children}
        </button>
    );
};

export default Button;
