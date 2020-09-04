package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public interface OptionalEntityExceptionHandler {

    default Comment getActiveCommentOrException(Optional<Comment> optionalComment) {
        if(optionalComment.isPresent() && optionalComment.get().isActive()) {
            return optionalComment.get();
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no_comment");
    }

    default Post getPostOrException(Optional<Post> optionalPost) {
        if(!optionalPost.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no_post");
        }
        else return optionalPost.get();
    }

    default Thread getThreadOrException(Optional<Thread> optionalThread) {
        if(!optionalThread.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no_thread");
        }
        else return optionalThread.get();
    }

    default User getUserOrException(Optional<User> optionalUser) {
        if(!optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no_user");
        }
        else return optionalUser.get();
    }
}
