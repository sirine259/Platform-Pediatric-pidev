package com.esprit.platformepediatricback.Service;

import com.esprit.platformepediatricback.Repository.CommentRepository;
import com.esprit.platformepediatricback.Repository.PostRepository;
import com.esprit.platformepediatricback.dto.CommentDTO;
import com.esprit.platformepediatricback.dto.CreateCommentRequest;
import com.esprit.platformepediatricback.dto.UpdateCommentRequest;
import com.esprit.platformepediatricback.dto.ReplyRequest;
import com.esprit.platformepediatricback.entity.Comment;
import com.esprit.platformepediatricback.entity.Post;
import com.esprit.platformepediatricback.entity.VoteComment;
import com.esprit.platformepediatricback.entity.LikePost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentDTOService {

    private static final Logger log = LoggerFactory.getLogger(CommentDTOService.class);

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeCommentService likeCommentService;
    private final VoteCommentService voteCommentService;

    public CommentDTOService(CommentRepository commentRepository, PostRepository postRepository,
                             LikeCommentService likeCommentService, VoteCommentService voteCommentService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.likeCommentService = likeCommentService;
        this.voteCommentService = voteCommentService;
    }

    public CommentDTO createComment(CreateCommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setDescription(request.getDescription());
        comment.setDateComment(LocalDateTime.now());
        comment.setPost(post);
        comment.setVoteComment(VoteComment.UpVote);
        comment.setReponse(new java.util.HashSet<>());

        Comment savedComment = commentRepository.save(comment);
        log.info("Created new comment {} for post {}", savedComment.getId(), request.getPostId());

        return new CommentDTO(savedComment);
    }

    public CommentDTO replyToComment(ReplyRequest request) {
        Comment parentComment = commentRepository.findById(request.getParentCommentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        Comment reply = new Comment();
        reply.setDescription(request.getReplyText());
        reply.setDateComment(LocalDateTime.now());
        reply.setPost(parentComment.getPost());
        reply.setVoteComment(VoteComment.UpVote);
        reply.setReponse(new java.util.HashSet<>());

        Comment savedReply = commentRepository.save(reply);

        if (parentComment.getReponse() == null) {
            parentComment.setReponse(new java.util.HashSet<>());
        }
        parentComment.getReponse().add(savedReply);
        commentRepository.save(parentComment);

        log.info("Created reply {} to comment {}", savedReply.getId(), request.getParentCommentId());

        return new CommentDTO(savedReply);
    }

    public CommentDTO updateComment(Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setDescription(request.getDescription());
        comment.setDateComment(LocalDateTime.now());

        if (request.getIsAnonymous() != null) {
            comment.setUser(null);
        }

        Comment updatedComment = commentRepository.save(comment);
        log.info("Updated comment {}", commentId);

        return new CommentDTO(updatedComment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentRepository.delete(comment);
        log.info("Deleted comment {}", commentId);
    }

    public void voteOnComment(Long commentId, VoteComment vote) {
        voteCommentService.voteComment(commentId, vote);
        log.info("Voted on comment {} with {}", commentId, vote);
    }

    public void likeComment(Long commentId, LikePost reaction) {
        likeCommentService.likeComment(commentId, reaction);
        log.info("Liked comment {} with {}", commentId, reaction);
    }

    public void unlikeComment(Long commentId) {
        likeCommentService.unlikeComment(commentId);
        log.info("Unliked comment {}", commentId);
    }

    public List<CommentDTO> getCommentsByPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    public CommentDTO getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return new CommentDTO(comment);
    }

    public List<CommentDTO> getRepliesToComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (comment.getReponse() != null) {
            return comment.getReponse().stream()
                    .map(CommentDTO::new)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public long countCommentsByPost(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public List<CommentDTO> getRecentComments(int limit) {
        List<Comment> comments = commentRepository.findRecentComments();
        return comments.stream()
                .limit(limit)
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    public List<CommentDTO> searchComments(String keyword) {
        List<Comment> comments = commentRepository.searchByContent(keyword);
        return comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    public List<CommentDTO> getCommentsByVote(VoteComment vote) {
        List<Comment> comments = commentRepository.findByVoteType(vote);
        return comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    public List<CommentDTO> getCommentsByReaction(LikePost reaction) {
        List<Comment> comments = commentRepository.findByReactionType(reaction);
        return comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    public List<CommentDTO> getTopVotedComments() {
        List<Comment> comments = commentRepository.findMostVotedComments();
        return comments.stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getCommentStatistics(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        Map<String, Object> stats = new java.util.HashMap<>();

        stats.put("comment", comment);
        stats.put("vote", comment.getVoteComment());
        stats.put("reaction", comment.getReaction());
        stats.put("replyCount", comment.getReponse() != null ? comment.getReponse().size() : 0);
        stats.put("dateComment", comment.getDateComment());
        stats.put("score", comment.getVoteScore());

        return stats;
    }

    public CommentDTO validateComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        log.info("Validated comment {}", commentId);
        return new CommentDTO(comment);
    }

    public CommentDTO reportComment(Long commentId, String reason) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        log.info("Reported comment {} for reason: {}", commentId, reason);
        return new CommentDTO(comment);
    }
}
