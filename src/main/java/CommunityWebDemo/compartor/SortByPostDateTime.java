package CommunityWebDemo.compartor;

import CommunityWebDemo.entity.Post;

import java.util.Comparator;

public class SortByPostDateTime implements Comparator<Post> {

    //Latest dateTime goes to the front
    @Override
    public int compare(Post o1, Post o2) {
        if(o1.getDateTime().isAfter(o2.getDateTime())) {
            return -1;
        }
        else if(o1.getDateTime().isBefore(o2.getDateTime())) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
