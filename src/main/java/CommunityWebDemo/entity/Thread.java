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
    private String description;

    @OneToMany
    private List<Post> posts = new ArrayList<>();

    public Thread() {
        this.url = "null";
        this.name = "Null";
    }

    public Thread(String initial, String name) {
        this.url = initial;
        this.name = name;
    }

    public Thread(String url, String name, String description) {
        this.url = url;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thread thread = (Thread) o;
        return url.equals(thread.url) &&
                Objects.equals(name, thread.name) &&
                Objects.equals(description, thread.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, name, description);
    }
}
