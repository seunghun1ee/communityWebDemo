package CommunityWebDemo.service;

import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService extends MyService<User>{

    @Autowired
    UserRepository userRepository;

    @Override
    public void add(User user) {

    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }

    @Override
    public void deleteAll() {

    }
}
