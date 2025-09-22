export interface Theme {
  id: number;
  name: string;
  description: string;
  createdAt: string;
  updatedAt: string;
}

export interface Article {
  id: number;
  title: string;
  content: string;
  authorUsername: string;
  theme: Theme;
  commentsCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateArticleRequest {
  title: string;
  content: string;
  themeId: number;
}

export interface Comment {
  id: number;
  content: string;
  username: string;
  articleId: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCommentRequest {
  content: string;
  articleId: number;
}