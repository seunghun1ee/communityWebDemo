package CommunityWebDemo.entity;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Post {

    @Id @GeneratedValue
    private Long id;
    private String title;
    private String body;
    private LocalDateTime dateTime = LocalDateTime.now();
    private Integer vote = 0;
    @ManyToOne() @JoinColumn()
    private User user;
    @ManyToOne
    private Thread thread;
    @OneToMany
    private List<Comment> comments = new ArrayList<>();

    private String ip;
    private String password;

    private String voterList = "{\"users\":{}, \"guests\":{}}";

    public Post() {

    }

    public Post(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Post(String title, String body, User user) {
        this.title = title;
        this.body = body;
        this.user = user;
    }

    public Post(Thread thread, String title, String body, User user) {
        this.thread = thread;
        this.title = title;
        this.body = body;
        this.user = user;
    }

    public Post(String title, String body, Thread thread, String password) {
        this.title = title;
        this.body = body;
        this.thread = thread;
        this.password = password;
    }

    public Post(String title, String body, LocalDateTime dateTime, User user, Thread thread) {
        this.title = title;
        this.body = body;
        this.dateTime = dateTime;
        this.user = user;
        this.thread = thread;
    }

    public Post(String title, String body, LocalDateTime dateTime, Thread thread, String password) {
        this.title = title;
        this.body = body;
        this.dateTime = dateTime;
        this.thread = thread;
        this.password = password;
    }

    public Post(Thread thread) {
        this.thread = thread;
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVoterList() {
        return voterList;
    }

    public void setVoterList(String votingList) {
        this.voterList = votingList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) &&
                Objects.equals(title, post.title) &&
                Objects.equals(body, post.body) &&
                Objects.equals(dateTime, post.dateTime) &&
                Objects.equals(vote, post.vote) &&
                Objects.equals(user, post.user) &&
                Objects.equals(thread, post.thread) &&
                Objects.equals(ip, post.ip) &&
                Objects.equals(password, post.password) &&
                Objects.equals(voterList, post.voterList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body, dateTime, vote, user, thread, ip, password, voterList);
    }
}
