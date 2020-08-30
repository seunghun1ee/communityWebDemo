package CommunityWebDemo.compartor;

import CommunityWebDemo.entity.Post;

import java.util.Comparator;

public class SortByPostVote implements Comparator<Post> {

    //Vote number descending order
    @Override
    public int compare(Post o1, Post o2) {
        return o2.getVote() - o1.getVote();
    }
}
