import { type InputHTMLAttributes } from "react";
import classes from "./Input.module.scss";
interface InputProps
  extends Omit<InputHTMLAttributes<HTMLInputElement>, "size"> {
  label?: string;
  size?: "small" | "medium" | "large";
}

const Input = ({ label, size, width, ...others }: InputProps) => {
  return (
    <div className={`${classes.root} ${classes[size || "large"]}`}>
      {label && (
        <label className={classes.label} htmlFor={others.id}>
          {label}
        </label>
      )}

      <input {...others} style={{ width: width ? `${width}px` : "auto" }} />
    </div>
  );
};

export default Input;
