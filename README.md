# Unnamed community/forum website project

## About
This project is about making reddit-like website with Spring Boot framework.

## Key features
The project aims the website to have two major features
1. Content moderation by everyone not by few moderators
2. Guest account is enough to enjoy the website (Signing up is optional)

    ### 1. Content moderation
    To prevent excessive or biased censorship by small number of people, there are no moderators.
    
    Instead, all contents have up/down vote system.
    
    When some contents get too many downvotes, they go to the bottom of the list and faded so users can find that those contents are not good.

    ### 2. Guest account
    Users can post, comment and vote without having an actual account.
    
    They can sign up for the website, but it is completely up to them.
    
    This is for giving users as much freedom as possible.

## Roadmap

#### Backend
* [x] Core features for posting and commenting
* [x] Core features for threads
* [x] Multiple threads
* [x] Guests can post and comment
* [x] Guests can vote
* [x] Sign in/Sign up
* [x] Username and password change
* [x] Users with account can post and comment
* [x] Users with account can vote
* [x] Save votes instantly
* [x] Be able to cancel vote
* [x] Reply to comments
* [ ] Voting system for comments and replies
* [x] Users can subscribe to threads
* [ ] Be able to sort posts in various order (New, hot, best etc)
* [x] Users can open threads
* [ ] Thread settings
* [ ] Be able to archive threads
* [x] Users can bookmark posts
* [x] Be able to attach an image in posts
* [ ] post tags
* [x] Use markdown for posts
* [ ] Use markdown for comments
* [x] Disable entities instead of deleting

#### Frontend
* [x] Basic frontend design {
    * [x] Homepage
    * [x] Thread (with banner)
    * [x] Post page
    * [x] User profile
    * [x] Navbar
    
    }
* [x] Show subscribed threads first at the homepage (Users)
* [ ] Separate subscribed threads and all threads
* [ ] Custom themes for each threads
* [x] Verify passwords from frontend
* [ ] Show character count for posts and comments
* [x] Markdown editor
* [ ] Navigation bar with search function
* [x] Keep replies of the comment after the comment is deleted
* [x] Deleted bookmark post is shown as \[Deleted post\]

Not fixed. They are subject to change.


## License
Not confirmed yet (probably MIT license soon)