package com.pediatric.platform.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String content;
    private String subject;
    private String picture;
    private Boolean isAnonymous;
    private String archivedReason;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_post", nullable = false, updatable = false)
    @CreationTimestamp
    private Date datePost;
    
    @Enumerated(EnumType.STRING)
    private LikePost likePost;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "post")
    private Set<Comment> comments;
    
    @Enumerated(EnumType.STRING)
    private StatusComplaint status = StatusComplaint.Pending;
    
    @ManyToOne
    private User user;
    
    // Constructeur avec paramètres essentiels
    public Post(String subject, String content, Boolean isAnonymous, User user) {
        this.subject = subject;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.user = user;
        this.comments = new HashSet<>();
        this.status = StatusComplaint.Pending;
    }
}
