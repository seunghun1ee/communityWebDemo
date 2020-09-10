package CommunityWebDemo.entity;

import javax.persistence.*;
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

    @ManyToOne
    private User opener;

    private String subscribers = "{}";

    @OneToMany(fetch = FetchType.EAGER)
    private List<Tag> tags;

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

    public Thread(String url, String name, String description, User opener) {
        this.url = url;
        this.name = name;
        this.description = description;
        this.opener = opener;
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

    public String getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(String subscribedUsers) {
        this.subscribers = subscribedUsers;
    }

    public User getOpener() {
        return opener;
    }

    public void setOpener(User opener) {
        this.opener = opener;
    }

    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thread thread = (Thread) o;
        return Objects.equals(url, thread.url) &&
                Objects.equals(name, thread.name) &&
                Objects.equals(description, thread.description) &&
                Objects.equals(opener, thread.opener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, name, description, opener);
    }
}
