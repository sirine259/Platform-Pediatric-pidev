package com.esprit.platformepediatricback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumPostDTO {
    
    private Long id;
    private String title;
    private String content;
    private String postType;
    private Long authorId;
    private Integer viewCount;
    private Integer likeCount;
    private String createdAt;
}
