package CommunityWebDemo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Post {

    @Getter @Setter @Id @GeneratedValue
    private Long id;
    @Getter @Setter
    private String title;
    @Getter @Setter
    private String body;
    @Getter @Setter
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
}
