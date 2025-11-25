import {useEffect, useState} from "react";
import classes from "./RightSidebar.module.scss";
import type {IUser} from "../../../authentication/contexts/AuthenticationContextProvider";
import {useNavigate, useParams} from "react-router-dom";
import {request} from "../../../../utils/api";
import {Loader} from "../../../../components/Loader/Loader";

function RightSidebar() {
    const [suggestions, setSuggestions] = useState<IUser[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();
    const {id} = useParams();

    useEffect(() => {
        request<IUser[]>({
            endpoint: "/api/v1/networking/suggestions?limit=2",
            onSuccess: (data) => {
                if (id) {
                    setSuggestions(data.filter((s) => s.id !== id));
                } else {
                    setSuggestions(data);
                }
            },
            onFailure: (error) => console.log(error),
        }).then(() => setLoading(false));
    }, [id]);

    return (
        <div className={classes.root}>
            <h3>Add to your connexions</h3>
            <div className={classes.items}>
                {suggestions.map((suggestion: IUser) => {
                    return <div className={classes.item} key={suggestion.id}>
                        <button className={classes.avatar} onClick={() => navigate("/profile/" + suggestion.id)}>
                            <img src={suggestion.profilePicture || "/avatar.svg"} alt=""/>
                        </button>
                    </div>;
                })}
            </div>
            RightSidebar

            {loading && <Loader isInline/>}
        </div>
    );
}

export default RightSidebar;
