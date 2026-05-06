package com.esprit.platformepediatricback.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data

@AllArgsConstructor
public class Post {
    
    public Post() {
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "picture")
    private String picture;
    
    // URL d'une vidéo associée au post (hébergée en externe ou via un service dédié)
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;
    
    @Column(name = "archived_reason")
    private String archivedReason;
    
    @Column(name = "date_post", nullable = false, updatable = false)
    private LocalDateTime datePost;
    
    @Enumerated(EnumType.STRING)
    private LikePost likePost;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "post")
    private Set<Comment> comments;
    
    @Enumerated(EnumType.STRING)
    private StatusComplaint status = StatusComplaint.Pending;
    
    @ManyToOne
    private User user;
    
    // Additional fields for extended functionality
    private String title;
    
    @Enumerated(EnumType.STRING)
    private PostType postType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = true)
    private User author;
    
    // Relations avec Forum (posts de type FORUM)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id")
    private Forum forum;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<ForumComment> forumComments;
    
    // Relations avec Kidney Transplant (posts de type MEDICAL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transplant_id")
    private Transplant transplant;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private Boolean isDeleted = false;
    
    @Column
    private Boolean isPublic = true;
    
    @Column
    private Integer viewCount = 0;
    
    @Column
    private Integer likeCount = 0;
    
    // Constructeur avec paramètres essentiels
    public Post(String subject, String content, Boolean isAnonymous, User user) {
        this.subject = subject;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.user = user;
        this.comments = new HashSet<>();
        this.status = StatusComplaint.Pending;
        this.datePost = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (datePost == null) {
            datePost = createdAt;
        }
        if (postType == null) {
            postType = PostType.FORUM;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PostType {
        FORUM,
        MEDICAL_UPDATE,
        FOLLOW_UP,
        ANNOUNCEMENT
    }
    
    // Méthodes utilitaires
    public boolean isForumPost() {
        return postType == PostType.FORUM;
    }
    
    public boolean isMedicalPost() {
        return postType == PostType.MEDICAL_UPDATE || 
               postType == PostType.FOLLOW_UP || 
               postType == PostType.ANNOUNCEMENT;
    }
    
    public Long getCommentCount() {
        return forumComments != null ? (long) forumComments.size() : 0L;
    }
    
    public String getPostCategory() {
        switch (postType) {
            case FORUM: return "Forum";
            case MEDICAL_UPDATE: return "Mise à jour médicale";
            case FOLLOW_UP: return "Suivi post-greffe";
            case ANNOUNCEMENT: return "Annonce";
            default: return "Autre";
        }
    }
    
    // Compatibility methods
    public void setForumPost(Object forumPost) {
        // This method is for compatibility - forumPost doesn't exist as a field
        // The relationship is through the forum field
    }
    
    public Object getForumPost() {
        // This method is for compatibility - returns the forum relationship
        return this.forum;
    }
    
    public Object getFollowUp() {
        // This method is for compatibility - returns the transplant relationship
        return this.transplant;
    }
    
    public void setFollowUp(Object followUp) {
        // This method is for compatibility - followUp doesn't exist as a field
        // The relationship is through the transplant field
    }
    
    // Getters/Setters manuels pour compatibilité
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getPicture() {
        return picture;
    }
    
    public void setPicture(String picture) {
        this.picture = picture;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public Boolean getIsAnonymous() {
        return isAnonymous;
    }
    
    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    public String getArchivedReason() {
        return archivedReason;
    }
    
    public void setArchivedReason(String archivedReason) {
        this.archivedReason = archivedReason;
    }
    
    public LocalDateTime getDatePost() {
        return datePost;
    }
    
    public void setDatePost(LocalDateTime datePost) {
        this.datePost = datePost;
    }
    
    public LikePost getLikePost() {
        return likePost;
    }
    
    public void setLikePost(LikePost likePost) {
        this.likePost = likePost;
    }
    
    public Set<Comment> getComments() {
        return comments;
    }
    
    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
    
    public StatusComplaint getStatus() {
        return status;
    }
    
    public void setStatus(StatusComplaint status) {
        this.status = status;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public PostType getPostType() {
        return postType;
    }
    
    public void setPostType(PostType postType) {
        this.postType = postType;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public void setAuthor(User author) {
        this.author = author;
    }
    
    public Forum getForum() {
        return forum;
    }
    
    public void setForum(Forum forum) {
        this.forum = forum;
    }
    
    public java.util.List<ForumComment> getForumComments() {
        return forumComments;
    }
    
    public void setForumComments(java.util.List<ForumComment> forumComments) {
        this.forumComments = forumComments;
    }
    
    public Transplant getTransplant() {
        return transplant;
    }
    
    public void setTransplant(Transplant transplant) {
        this.transplant = transplant;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    // Méthodes additionnelles pour compatibilité
    public Long getUpVotes() {
        return likePost != null && likePost.toString().contains("Like") ? 1L : 0L;
    }
    
    public Long getDownVotes() {
        return likePost != null && likePost.toString().contains("Dislike") ? 1L : 0L;
    }
    
    public Long getTotalReactions() {
        return likePost != null ? 1L : 0L;
    }
    
    public Double getPopularityScore() {
        double score = 0.0;
        if (likePost != null) {
            switch (likePost) {
                case Love: score = 10.0; break;
                case Support: score = 8.0; break;
                case Celebrate: score = 7.0; break;
                case Insightful: score = 6.0; break;
                case Funny: score = 5.0; break;
                case Like: score = 3.0; break;
                case Dislike: score = -2.0; break;
            }
        }
        return score + (viewCount != null ? viewCount * 0.1 : 0);
    }
    
    public boolean isTrending() {
        return getPopularityScore() > 5.0;
    }
}
