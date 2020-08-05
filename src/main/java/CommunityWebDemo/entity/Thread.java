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
    private String url;
    private String name;

    @OneToMany
    private List<Post> posts = new ArrayList<>();

    public Thread() {
        this.url = "a";
        this.name = "apple";
    }

    public Thread(String initial, String name) {
        this.url = initial;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String initial) {
        this.url = initial;
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
        return Objects.equals(url, thread.url) &&
                Objects.equals(name, thread.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, name);
    }
}
