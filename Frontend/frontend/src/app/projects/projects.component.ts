import { Component, OnInit } from '@angular/core';
import {Post, PostService} from "../services/post.service";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-project',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {
  posts: Post[] = [];
  imageStr: string  = ''
  currentUserId: number = this.authService.getUser()?.id;
  currentUserRole: string = this.authService.getUser()?.role;
  currentStatus: 'APPROVED' | 'REJECTED' | 'PENDING_APPROVAL' | null = null;
  searchQuery: string = '';
  sortAscending: boolean = true;
  constructor(private postService: PostService, private authService: AuthService) { }
  ngOnInit(): void {
    this.postService.getAllPosts().subscribe(posts => {
      this.posts = posts;
    });
  }

  isAuthorized(): boolean {
    return !!this.currentUserRole;
  }
  isLiked(post: Post): boolean {
    // Check if the current user has liked this post
    return post.likes.some(like => like.user.id === this.currentUserId);
  }

  toggleLike(postId: number, userId: number): void {
    this.postService.toggleLike(postId, userId).subscribe(() => {
      if (!this.isAuthorized() || this.currentUserRole === 'USER') {
        this.loadApprovedPosts();
      } else {
        this.loadPosts();
      }
    });
  }

  canSeeStatus(): boolean {
    return this.currentUserRole === 'ADMIN' || this.currentUserRole === 'MODERATOR';
  }

  approvePost(postId: number): void {
    this.postService.approvePost(postId).subscribe(() => {
      this.filterAndSortPosts(); // <-- Додайте цей рядок
    });
  }

  rejectPost(postId: number): void {
    this.postService.rejectPost(postId).subscribe(() => {
      this.loadPosts();
    });
  }

  // Update this method to get posts based on the current status
  loadPosts(): void {
    if (this.currentStatus) {
      this.postService.getPostsByStatus(this.currentStatus).subscribe(posts => {
        this.posts = posts;

      });
    } else {
      this.postService.getAllPosts().subscribe(posts => {
        this.posts = posts;

      });
    }
  }
  filterAndSortPosts(): void {
    this.filterPosts();
    this.sortPosts();
  }
  filterByStatus(status: 'APPROVED' | 'REJECTED' | 'PENDING_APPROVAL' | null): void {
    this.currentStatus = status;
    this.loadPosts();
  }
  onSearch(): void {
    this.filterAndSortPosts();
  }

  filterPosts(): void {
    if (this.searchQuery.trim() !== '') {
      this.posts = this.posts.filter(post =>
        post.title.toLowerCase().includes(this.searchQuery.toLowerCase()) && post.status === 'APPROVED'
      );
    } else {
      if (!this.isAuthorized() || this.currentUserRole === 'USER') {
        this.loadApprovedPosts();
      } else {
        this.loadPosts();
      }
    }
  }

  loadApprovedPosts(): void {
    this.postService.getAllPosts().subscribe(posts => {
      if (!this.isAuthorized() || this.currentUserRole === 'USER') {
        this.posts = posts.filter(post => post.status === 'APPROVED');
      } else {
        this.posts = posts;
      }
    });
  }
  sortPosts(): void {
    this.posts.sort((a, b) => {
      const valueA = a.title.toLowerCase();
      const valueB = b.title.toLowerCase();
      if (valueA < valueB) {
        return this.sortAscending ? -1 : 1;
      }
      if (valueA > valueB) {
        return this.sortAscending ? 1 : -1;
      }
      return 0;
    });
  }

  toggleSortOrder(): void {
    this.sortAscending = !this.sortAscending;
    this.sortPosts();
  }
}
