package CommunityWebDemo.service;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService extends MyService<Comment>{

    @Autowired
    CommentRepository commentRepository;

    @Override
    public void add(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public void addAll(Iterable<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    @Override
    public List<Comment> getAll() {
        return (List<Comment>) commentRepository.findAll();
    }

    @Override
    public Optional<Comment> getById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        if(commentRepository.findById(id).isPresent()) {
            commentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAll() {
        commentRepository.deleteAll();
    }

    @Override
    public void deleteAll(Iterable<Comment> comments) {
        commentRepository.deleteAll(comments);
    }
}
