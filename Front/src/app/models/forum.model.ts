// Modèles Forum compatibles avec le backend Spring Boot

export enum PostType {
  FORUM = 'FORUM',
  MEDICAL_UPDATE = 'MEDICAL_UPDATE',
  FOLLOW_UP = 'FOLLOW_UP',
  ANNOUNCEMENT = 'ANNOUNCEMENT'
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
}

export interface Forum {
  id: number;
  theme: string;
  description?: string;
  moderator?: User;
  isActive: boolean;
  isPrivate: boolean;
  posts: Post[];
  createdAt: string;
  updatedAt: string;
}

export interface ForumComment {
  id: number;
  post: Post;
  author: User;
  content: string;
  parentComment?: ForumComment;
  replies: ForumComment[];
  isEdited: boolean;
  isDeleted: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Post {
  id: number;
  title: string;
  content: string;
  postType: PostType;
  author: User;
  forum?: Forum;
  transplant?: Transplant;
  createdAt: string;
  updatedAt: string;
  isDeleted: boolean;
  isPublic: boolean;
  viewCount: number;
  likeCount: number;
  comments: ForumComment[];
  // Rating fields
  averageRating?: number;
  totalRatings?: number;
  userRating?: number;
}

// Importer Transplant depuis le modèle de transplantation
import { Transplant } from './transplant.model';

// DTOs pour les réponses API
export interface ForumDTO {
  id: number;
  theme: string;
  description?: string;
  moderatorId?: number;
  moderatorName?: string;
  isActive: boolean;
  isPrivate: boolean;
  postCount: number;
  forumStatus: string;
  createdAt: string;
  updatedAt: string;
  recentPosts?: PostSummaryDTO[];
  lastActivity?: string;
  isUserModerator?: boolean;
  canUserAccess?: boolean;
}

export interface PostSummaryDTO {
  id: number;
  title: string;
  authorName: string;
  createdAt: string;
  commentCount: number;
  viewCount: number;
}

export interface ForumStats {
  totalForums: number;
  activeForums: number;
  totalPosts: number;
  totalComments: number;
  recentActivity: Post[];
}
