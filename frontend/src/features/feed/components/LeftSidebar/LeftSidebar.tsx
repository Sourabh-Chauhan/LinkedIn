import classes from "./LeftSidebar.module.scss";
import type {IUser} from "../../../authentication/contexts/AuthenticationContextProvider";
import {useNavigate} from "react-router-dom";

interface ILeftSidebarProps {
    user: IUser | null;
}

function LeftSidebar({user}: ILeftSidebarProps) {
    const navigate = useNavigate();

    return (
        <div className={classes.root}>
            <div className={classes.cover}>
                <img
                    src={
                        user?.coverPicture
                            ? `${import.meta.env.VITE_API_URL}/api/v1/storage/${
                                user?.coverPicture
                            }`
                            : "/cover.jpeg"
                    }
                    alt="Cover"
                />
            </div>
            <button
                className={classes.avatar}
                onClick={() => navigate("/profile/" + user?.id)}
            >
                <img
                    src={
                        user?.profilePicture
                            ? `${import.meta.env.VITE_API_URL}/api/v1/storage/${
                                user?.profilePicture
                            }`
                            : "/avatar.svg"
                    }
                    alt=""
                />
            </button>

            <div className={classes.name}>
                {user?.firstName + " " + user?.lastName}
            </div>
            <div className={classes.title}>
                {user?.position + " at " + user?.company}
            </div>
            <div className={classes.info}>
                <div className={classes.item}>
                    <div className={classes.label}>Profile viewers</div>
                    <div className={classes.value}>0</div>
                </div>
            </div>
        </div>
    );
}

export default LeftSidebar;
