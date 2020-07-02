package CommunityWebDemo.repository;

import CommunityWebDemo.entity.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {
}
