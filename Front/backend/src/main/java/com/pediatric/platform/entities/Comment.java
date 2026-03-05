package com.pediatric.platform.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String description;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateComment;
    
    @Enumerated(EnumType.STRING)
    private VoteComment voteComment;
    
    @Enumerated(EnumType.STRING)
    private LikePost reaction;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> reponse;
    
    // Relation avec le post parent
    @ManyToOne
    private Post post;
    
    // Auteur du commentaire
    @ManyToOne
    private User user;
    
    // Constructeur avec paramètres essentiels
    public Comment(String description, Post post, User user) {
        this.description = description;
        this.dateComment = new Date();
        this.voteComment = VoteComment.UpVote;
        this.post = post;
        this.user = user;
        this.reponse = new HashSet<>();
    }
}
