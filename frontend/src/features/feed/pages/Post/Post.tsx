import {useEffect, useState} from "react";

import classes from "./Post.module.scss";
import {useParams} from "react-router-dom";
import {useAuthentication} from "../../../authentication/contexts/AuthenticationContextProvider";
import RightSidebar from "../../components/RightSidebar/RightSidebar.tsx";
import {type IPost, Post} from "../../components/Post/Post.tsx";
import {request} from "../../../../utils/api.ts";
import LeftSidebar from "../../components/LeftSidebar/LeftSidebar.tsx";

const PostPage = () => {
    const [posts, setPosts] = useState<IPost[]>([]);
    const {id} = useParams();
    const {user} = useAuthentication();

    useEffect(() => {
        request<IPost>({
            endpoint: `/api/v1/feed/posts/${id}`,
            onSuccess: (post) => setPosts([post]),
            onFailure: (error) => console.log(error),
        });
    }, [id]);

    return <div className={classes.root}>
        <div className={classes.left}>
            <LeftSidebar user={user}/>
        </div>
        <div className={classes.center}>
            {posts.length > 0 && <Post setPosts={setPosts} post={posts[0]}/>}
        </div>
        <div className={classes.right}>
            <RightSidebar/>
        </div>
    </div>;
};

export default PostPage;
