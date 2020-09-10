package CommunityWebDemo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tag {

    @Id @GeneratedValue
    private Long id;
    private String tagName;
    private String colour;


}
