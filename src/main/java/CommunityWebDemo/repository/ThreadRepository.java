package CommunityWebDemo.repository;

import CommunityWebDemo.entity.Thread;
import org.springframework.data.repository.CrudRepository;

public interface ThreadRepository extends CrudRepository<Thread, String> {
}
