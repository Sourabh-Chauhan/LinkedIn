import classes from "./Separator.module.scss";
import React from "react";
interface SeparatorProps {
  children: React.ReactNode;
}

const Separator = ({ children }: SeparatorProps) => {
  return (
    <div className={classes.separator}>
      {/* Separator */}
      {children}
    </div>
  );
};

export default Separator;
