package CommunityWebDemo.service;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.PostRepository;
import CommunityWebDemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService extends MyService<User> implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public void add(User user) {
        userRepository.save(user);
    }

    @Override
    public void addAll(Iterable<User> users) {
        userRepository.saveAll(users);
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        if(userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public void deleteAll(Iterable<User> users) {
        userRepository.deleteAll(users);
    }

    public Optional<User> getByUsername(String username) {
        Optional<User> optionalUser = Optional.empty();
        List<User> users = (List<User>) userRepository.findAll();
        for(User user : users) {
            if(user.getUsername().equals(username)) {
                optionalUser = Optional.of(user);
                break;
            }
        }
        return optionalUser;

    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> optionalUser = getByUsername(s);
        return optionalUser.orElse(null);
    }
}
