import classes from "./AuthenticationLayout.module.scss";
import { Outlet } from "react-router-dom";

const AuthenticationLayout = () => {
  return (
    <div className={classes.root}>
      {/* AuthenticationLayout */}
      <header>
        <div className={classes.container}>
          <a href="/" className={classes.logo}>
            <img src="/logo.svg" alt="Logo" className={classes.logo} />
          </a>
        </div>
      </header>
      <main className={classes.container}>
        <Outlet />
      </main>
      <footer>
        <ul className={classes.container}>
          <li>
            <img src="/logo-dark.svg" alt="" />
            <span>&copy;{new Date().getFullYear()}</span>
          </li>
          <li>
            <a href="">Accessibility</a>
          </li>
          <li>
            <a href="">User Agreement</a>
          </li>
          <li>
            <a href="">Privacy Policy</a>
          </li>
          <li>
            <a href="">Cookie Policy</a>
          </li>
          <li>
            <a href="">Copyright Policy</a>
          </li>
          <li>
            <a href="">Brand Policy</a>
          </li>
          <li>
            <a href="">Guest Controls</a>
          </li>
          <li>
            <a href="">Community Guidelines</a>
          </li>
          <li>
            <a href="">Language</a>
          </li>
        </ul>
      </footer>
    </div>
  );
};

export default AuthenticationLayout;
