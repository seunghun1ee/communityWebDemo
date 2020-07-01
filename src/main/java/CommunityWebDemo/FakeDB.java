package CommunityWebDemo;

import CommunityWebDemo.entity.Post;

import java.util.ArrayList;

public class FakeDB {

    public ArrayList<Post> postTable;

    public FakeDB() {
        this.postTable = new ArrayList<>();
    }
}
