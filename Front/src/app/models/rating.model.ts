export interface PostRating {
  id?: number;
  postId: number;
  userId?: number;
  rating: number; // 1-5 étoiles
  createdAt?: Date | string;
  updatedAt?: Date | string;
}

export interface PostRatingSummary {
  postId: number;
  averageRating: number;
  totalRatings: number;
  ratingDistribution: {
    1: number;
    2: number;
    3: number;
    4: number;
    5: number;
  };
}

export interface RatingRequest {
  postId: number;
  rating: number;
}
