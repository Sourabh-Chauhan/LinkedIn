import React from "react";
import classes from "./Box.module.scss";
interface BoxProps {
  children: React.ReactNode;
}

const Box = ({ children }: BoxProps) => {
  return <div className={classes.root}>{children}</div>;
};

export default Box;
// This component is used to wrap content in a styled box.
// It can be used for various purposes such as forms, cards, or any other content that needs a box-like appearance.
// The `children` prop allows you to pass any React nodes to be rendered inside the box.
// The styles are defined in the Box.module.scss file, which should contain the necessary CSS for the box appearance.
// Example usage:   <Box>Content goes here</Box>
