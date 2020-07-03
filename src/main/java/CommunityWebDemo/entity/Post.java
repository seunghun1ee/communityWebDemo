package CommunityWebDemo.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Post {

    @Id @GeneratedValue
    private Long id;
    private String title;
    private String body;
    private Long authorId;

    public Post() {

    }

    public Post(String title, String body, Long authorId) {
        this.title = title;
        this.body = body;
        this.authorId = authorId;
    }

    public Post(Long id, String title, String body, Long authorId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.authorId = authorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id.equals(post.id) &&
                title.equals(post.title) &&
                body.equals(post.body) &&
                authorId.equals(post.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body, authorId);
    }
}
