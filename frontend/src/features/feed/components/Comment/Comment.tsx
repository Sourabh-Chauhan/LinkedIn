import {useEffect, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import {type IUser, useAuthentication} from "../../../authentication/contexts/AuthenticationContextProvider.tsx";
import classes from "./Comment.module.scss";
import Input from "../../../../components/Input/Input.tsx";
import TimeAgo from "../TimeAgo/TimeAgo.tsx";

export interface IComment {
    id: number;
    content: string;
    author: IUser;
    creationDate: string;
    updatedDate?: string;
}

interface ICommentProps {
    comment: IComment;
    deleteComment: (commentId: number) => Promise<void>;
    editComment: (commentId: number, content: string) => Promise<void>;
}


const Comment = ({comment, deleteComment, editComment}: ICommentProps) => {
    const navigate = useNavigate();
    const {user} = useAuthentication();
    const [showActions, setShowActions] = useState(false);
    const [editing, setEditing] = useState(false);
    const [commentContent, setCommentContent] = useState(comment.content);

    const menuRef = useRef<HTMLDivElement>(null);
    const buttonRef = useRef<HTMLButtonElement>(null);
    const formRef = useRef<HTMLFormElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)
                && buttonRef.current && !buttonRef.current.contains(event.target as Node)) {
                setShowActions(false);

            }
        };

        if (showActions) {
            document.addEventListener("mousedown", handleClickOutside);
        }

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [showActions]);


    useEffect(() => {
        const handleESCkey = (event: KeyboardEvent) => {
            // if (menuRef.current && !menuRef.current.contains(event.target as Node)
            //     && buttonRef.current && !buttonRef.current.contains(event.target as Node)) {
            //     setShowActions(false);
            // }
            if (event.key === 'Escape') {
                // console.log(formRef.current && !formRef.current.contains(event.target as Node));
                // if (formRef.current && !formRef.current.contains(event.target as Node))
                setEditing(false);

            }

        };

        if (editing) {
            document.addEventListener("keydown", handleESCkey);
        }

        return () => {
            document.removeEventListener("keydown", handleESCkey);
        };
    }, [editing]);

    return (<div key={comment.id} className={classes.root}>
        {!editing ? <>

            <div className={classes.header}>
                <button onClick={() => {
                    navigate(`/profile/${comment.author.id}`);
                }} className={classes.author}>
                    <img
                        className={classes.avatar}
                        src={comment.author.profilePicture || "/avatar.svg"}
                        alt=""
                    />
                    <div>
                        <div className={classes.name}>
                            {comment.author.firstName + " " + comment.author.lastName}
                        </div>
                        <div className={classes.title}>
                            {comment.author.position + " at " + comment.author.company}
                        </div>
                        <TimeAgo date={comment.creationDate} edited={!!comment.updatedDate}/>
                    </div>
                </button>

                {comment.author.id == user?.id && (<button
                    className={`${classes.action} ${showActions ? classes.active : ""}`}
                    onClick={() => setShowActions(!showActions)}
                    ref={buttonRef}
                >
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 128 512">
                        <path
                            d="M64 360a56 56 0 1 0 0 112 56 56 0 1 0 0-112zm0-160a56 56 0 1 0 0 112 56 56 0 1 0 0-112zM120 96A56 56 0 1 0 8 96a56 56 0 1 0 112 0z"/>
                    </svg>
                </button>)}

                {showActions && (<div ref={menuRef} className={classes.actions}>
                    <button onClick={() => setEditing(true)}>Edit</button>
                    <button onClick={() => deleteComment(comment.id)}>Delete</button>
                </div>)}

            </div>

            <div className={classes.content}>{comment.content}</div>
        </> : <>
            <form ref={formRef}
                  onSubmit={async (e) => {
                      e.preventDefault();
                      await editComment(comment.id, commentContent);
                      setEditing(false);
                      setShowActions(false);
                  }}
            >
                <Input
                    type="text"
                    value={commentContent}
                    onChange={(e) => {
                        setCommentContent(e.target.value);
                    }}
                    placeholder="Edit your comment"
                />
            </form>
        </>}

    </div>)
}
export default Comment
