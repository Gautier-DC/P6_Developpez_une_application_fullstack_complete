import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormsModule } from '@angular/forms';
import { ArticleService } from '../../services/article.service';
import {
  Article,
  Comment,
  CreateCommentRequest,
} from '../../models/article.models';
import { BackButtonComponent } from '../../components/back-button/back-button.component';

@Component({
  selector: 'app-article-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatDividerModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    FormsModule,
    BackButtonComponent,
  ],
  templateUrl: './article-detail.component.html',
  styleUrls: ['./article-detail.component.scss'],
})
export class ArticleDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private articleService = inject(ArticleService);

  article: Article | null = null;
  comments: Comment[] = [];
  isLoading = true;
  isCommentsLoading = false;
  newComment = '';
  isSubmittingComment = false;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadArticle(+id);
      this.loadComments(+id);
    } else {
      this.router.navigate(['/articles']);
    }
  }

  loadArticle(id: number): void {
    this.isLoading = true;
    this.articleService.getArticleById(id).subscribe({
      next: (article) => {
        this.article = article;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading article:', error);
        this.isLoading = false;
        // Navigate to 404 page for non-existent articles
        this.router.navigate(['/404']);
      },
    });
  }

  loadComments(articleId: number): void {
    this.isCommentsLoading = true;
    this.articleService.getCommentsByArticle(articleId).subscribe({
      next: (comments) => {
        this.comments = comments;
        this.isCommentsLoading = false;
      },
      error: (error) => {
        console.error('Error loading comments:', error);
        this.isCommentsLoading = false;
      },
    });
  }

  onSubmitComment(): void {
    if (!this.newComment.trim() || !this.article || this.isSubmittingComment) {
      return;
    }

    this.isSubmittingComment = true;
    const commentRequest: CreateCommentRequest = {
      content: this.newComment.trim(),
      articleId: this.article.id,
    };

    this.articleService.createComment(commentRequest).subscribe({
      next: (comment) => {
        this.comments.unshift(comment); // Add to beginning of array
        this.newComment = '';
        this.isSubmittingComment = false;
      },
      error: (error) => {
        console.error('Error creating comment:', error);
        this.isSubmittingComment = false;
      },
    });
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  onBackClick(): void {
    this.router.navigate(['/articles']);
  }
}
