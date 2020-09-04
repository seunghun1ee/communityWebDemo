package CommunityWebDemo.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private Integer accountLevel = 2; //admin: 0, ???:1 ,user:2

    @OneToMany
    private List<Post> posts = new ArrayList<>();
    @OneToMany
    private List<Comment> comments = new ArrayList<>();

    private String subscribedThreads = "{}";

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, Integer accountLevel) {
        this.username = username;
        this.accountLevel = accountLevel;
    }

    public User(String username, String password, Integer accountLevel) {
        this.username = username;
        this.password = password;
        this.accountLevel = accountLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAccountLevel() {
        return accountLevel;
    }

    public void setAccountLevel(Integer accountLevel) {
        this.accountLevel = accountLevel;
    }

    public String getSubscribedThreads() {
        return subscribedThreads;
    }

    public void setSubscribedThreads(String subscribedThreads) {
        this.subscribedThreads = subscribedThreads;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        switch (this.accountLevel) {
            case 0:
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
            case 2:
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
            default:
                break;
        }
        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(accountLevel, user.accountLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, accountLevel);
    }
}
