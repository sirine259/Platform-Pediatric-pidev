export enum VoteComment {
  UpVote = 'UpVote',
  DownVote = 'DownVote'
}

export enum LikePost {
  Like = 'Like',
  Celebrate = 'Celebrate',
  Support = 'Support',
  Love = 'Love',
  Insightful = 'Insightful',
  Funny = 'Funny',
  Dislike = 'Dislike'
}

export enum StatusComplaint {
  Pending = 'Pending',
  Approved = 'Approved',
  Rejected = 'Rejected',
  Archived = 'Archived'
}

export interface Comment {
  id?: number;
  description: string;
  dateComment: Date | string;
  voteComment?: VoteComment;
  reaction?: LikePost;
  reponse?: Comment[];
  postId?: number;
}

export interface Post {
  id?: number;
  content: string;
  subject: string;
  picture?: string;
  isAnonymous: boolean;
  archivedReason?: string;
  datePost: Date;
  likePost?: LikePost;
  comments?: Comment[];
  status?: StatusComplaint;
  user?: {
    id?: number;
    userName?: string;
    firstName?: string;
    lastName?: string;
  };
}
