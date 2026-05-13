export interface Post {
  id: number;
  title: string;
  content: string;
  postType: PostType;
  author: User;
  forum?: Forum;
  transplant?: Transplant;
  comments: ForumComment[];
  createdAt: Date;
  updatedAt: Date;
}

export enum PostType {
  FORUM = 'FORUM',
  MEDICAL_UPDATE = 'MEDICAL_UPDATE', 
  FOLLOW_UP = 'FOLLOW_UP',
  ANNOUNCEMENT = 'ANNOUNCEMENT'
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: string;
}

export interface Forum {
  id: number;
  theme: string;
  description: string;
  moderator: User;
  isActive: boolean;
  isPrivate: boolean;
  posts: Post[];
  createdAt: Date;
  updatedAt: Date;
}

export interface ForumComment {
  id: number;
  post: Post;
  author: User;
  content: string;
  parentComment?: ForumComment;
  replies: ForumComment[];
  isEdited: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export interface Transplant {
  id: number;
  donor: User;
  recipient: User;
  surgeon: User;
  hospital: string;
  scheduledDate: Date;
  actualDate?: Date;
  status: string;
  createdAt: Date;
  updatedAt: Date;
}
