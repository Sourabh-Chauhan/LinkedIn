import { useNavigate } from "react-router-dom";
import { useAuthentication } from "../../../authentication/contexts/AuthenticationContextProvider";
import classes from "./Feed.module.scss";
import LeftSidebar from "../../components/LeftSidebar/LeftSidebar";
import Button from "../../../../components/Button/Button";
import { useEffect, useState } from "react";
import { request } from "../../../../utils/api";
import { type IPost } from "../../components/Post/Post";
import Modal from "../../components/Modal/Modal";
import { usePageTitle } from "../../../../hooks/usePageTitle";
import { Loader } from "../../../../components/Loader/Loader";
import RightSidebar from "../../components/RightSidebar/RightSidebar";

const Feed = () => {
  usePageTitle("Feed");
  const { user } = useAuthentication();
  const navigate = useNavigate();

  const [showPostingModal, setShowPostingModal] = useState(false);
  const [posts, setPosts] = useState<IPost[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  // const ws = useWebSocket();

  useEffect(() => {
    const fetchPosts = async () => {
      await request<IPost[]>({
        endpoint: "/api/v1/feed",
        onSuccess: (data) => {
          setPosts(data);
          setLoading(false);
        },
        onFailure: (error) => setError(error),
      });
    };
    fetchPosts()
      .then((r) => console.log("fetch Posts done", r))
      .catch((e) => console.log("fetchPosts Failed", e));
  }, []);

  const handlePost = async (data: FormData) => {
    await request<IPost>({
      endpoint: "/api/v1/feed/posts",
      method: "POST",
      contentType: "multipart/form-data",
      body: data,
      onSuccess: (data) => setPosts([data, ...posts]),
      onFailure: (error) => setError(error),
    });
  };

  // useEffect(() => {
  //     const subscription = ws?.subscribe(`/topic/feed/${user?.id}/post`, (data) => {
  //         const post = JSON.parse(data.body);
  //         setPosts((posts) => [post, ...posts]);
  //     });
  //     return () => subscription?.unsubscribe();
  // }, [user?.id, ws]);

  return (
    <div className={classes.root}>
      <div className={classes.left}>
        <LeftSidebar user={user} />
      </div>
      <div className={classes.center}>
        <div className={classes.posting}>
          <button
            onClick={() => {
              navigate(`/profile/${user?.id}`);
            }}
          >
            <img
              className={`${classes.top} ${classes.avatar}`}
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
          <Button outline onClick={() => setShowPostingModal(true)}>
            Start a post
          </Button>

          <Modal
            title="Creating a post"
            onSubmit={handlePost}
            showModal={showPostingModal}
            setShowModal={setShowPostingModal}
          />
        </div>
        {error && <div className={classes.error}>{error}</div>}

        {loading ? (
          <Loader isInline />
        ) : (
          <div className={classes.feed}>
            {posts.map((post) => (
              // <Post key={post.id} post={post} setPosts={setPosts}/>
              <>{post.id}</>
            ))}
            {posts.length === 0 && (
              <p>
                Start connecting with poople to build a feed that matters to
                you.
              </p>
            )}
          </div>
        )}
      </div>
      <div className={classes.right}>
        <RightSidebar />
      </div>
    </div>
  );
};

export default Feed;
