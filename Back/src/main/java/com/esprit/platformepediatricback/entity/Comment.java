package com.esprit.platformepediatricback.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(name = "date_comment")
    private LocalDateTime dateComment;

    @Enumerated(EnumType.STRING)
    private VoteComment voteComment;

    @Enumerated(EnumType.STRING)
    private LikePost reaction;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> reponse;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;

    public Comment() {
        this.reponse = new HashSet<>();
    }

    public Comment(String description, Post post, User user) {
        this.description = description;
        this.dateComment = LocalDateTime.now();
        this.voteComment = VoteComment.UpVote;
        this.post = post;
        this.user = user;
        this.reponse = new HashSet<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDateComment() { return dateComment; }
    public void setDateComment(LocalDateTime dateComment) { this.dateComment = dateComment; }
    public VoteComment getVoteComment() { return voteComment; }
    public void setVoteComment(VoteComment voteComment) { this.voteComment = voteComment; }
    public LikePost getReaction() { return reaction; }
    public void setReaction(LikePost reaction) { this.reaction = reaction; }
    public Set<Comment> getReponse() { return reponse; }
    public void setReponse(Set<Comment> reponse) { this.reponse = reponse; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getVoteScore() {
        if (voteComment == VoteComment.UpVote) return 1;
        if (voteComment == VoteComment.DownVote) return -1;
        return 0;
    }

    public boolean hasReaction() { return reaction != null; }
    public boolean hasVote() { return voteComment != null; }
}
