package CommunityWebDemo.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public abstract class MyService<T> {

    abstract public void add(T t);

    abstract public List<T> getAll();

    abstract public Optional<T> getById(Long id);

    abstract public boolean deleteById(Long id);

    abstract public void deleteAll();
}
