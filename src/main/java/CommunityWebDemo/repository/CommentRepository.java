package CommunityWebDemo.repository;

import CommunityWebDemo.entity.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {
}
