package CommunityWebDemo.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Tag {

    @Id @GeneratedValue
    private Long id;
    private String tagName;
    private String colour = "#ffffff";

    @ManyToOne
    private Thread thread;

    @OneToMany
    private List<Post> posts;

    public Tag() {
    }

    public Tag(String tagName, Thread thread) {
        this.tagName = tagName;
        this.thread = thread;
    }

    public Tag(String tagName, String colour, Thread thread) {
        this.tagName = tagName;
        this.colour = colour;
        this.thread = thread;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Thread getThread() {
        return this.thread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) &&
                Objects.equals(tagName, tag.tagName) &&
                Objects.equals(colour, tag.colour) &&
                Objects.equals(thread, tag.thread);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tagName, colour, thread);
    }
}
