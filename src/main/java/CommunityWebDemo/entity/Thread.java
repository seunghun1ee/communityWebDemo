package CommunityWebDemo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Thread {

    @Id
    private String initial;
    private String name;

    @OneToMany
    private List<Post> posts = new ArrayList<>();

    public Thread() {
        this.initial = "a";
        this.name = "apple";
    }

    public Thread(String initial, String name) {
        this.initial = initial;
        this.name = name;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thread thread = (Thread) o;
        return Objects.equals(initial, thread.initial) &&
                Objects.equals(name, thread.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initial, name);
    }
}
