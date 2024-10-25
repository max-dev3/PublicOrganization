import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from "@angular/router";
import { Post, PostService } from "../services/post.service";


@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user: any;
  isDialogVisible = false;
  userPosts: Post[] = [];
  likedPosts: Post[] = [];

  currentUserId: number = this.authService.getUser()?.id;

  currentUserRole: string = this.authService.getUser()?.role;

  constructor(private authService: AuthService, private postService: PostService, private router: Router) { }

  ngOnInit(): void {
    this.user = this.authService.getUser();
    this.getUserPosts();
    this.getLikedPosts();
  }

  getUserPosts() {
    this.postService.getUserPosts(this.user.id).subscribe(
      posts => {
        this.userPosts = posts;
        this.userPosts.forEach(post => {
          post.image = `http://localhost:8080/api/v1/posts/${post.id}/image`; // Формуємо шлях до зображення поста
        });
      },
      error => console.error('Failed to fetch user posts', error)
    );
  }


  getLikedPosts() {
    this.postService.getLikedPosts(this.user.id).subscribe(
      posts => {
        this.likedPosts = posts;
        this.likedPosts.forEach(post => {
          post.image = `http://localhost:8080/api/v1/posts/${post.id}/image`; // Формуємо шлях до зображення вподобаного поста
        });
      },
      error => console.error('Failed to fetch liked posts', error)
    );
  }

  showDialog() {
    this.isDialogVisible = true;
  }
  showingOwnProjects = true;

  showOwnProjects() {
    this.showingOwnProjects = true;
  }

  isLiked(post: Post): boolean {
    // Check if the current user has liked this post
    return post.likes.some(like => like.user.id === this.currentUserId);
  }

  toggleLike(postId: number, userId: number): void {
    this.postService.toggleLike(postId, userId).subscribe(() => {
      const postIndex = this.likedPosts.findIndex(post => post.id === postId);
      if (postIndex > -1) {
        this.likedPosts.splice(postIndex, 1);
      }
    });
  }

  hideDialog() {
    this.isDialogVisible = false;
  }

  showLikedProjects() {
    this.showingOwnProjects = false;
  }

  deletePost(postId: number) {
    this.postService.deletePost(postId).subscribe(() => {
      // After successful delete, remove the post from userPosts array
      this.userPosts = this.userPosts.filter(post => post.id !== postId);
    }, error => {
      console.error('Error deleting post', error);
      // Handle errors here
    });
  }

  deleteAccount() {
    this.authService.deleteAccount(this.user.id).subscribe(
      response => {
        console.log('Account deleted successfully', response);
        this.authService.logout();
        this.router.navigate(['/']);
      },
      error => {
        console.log('Account deletion failed', error);
      }
    );
  }
  canSeeStatus(): boolean {
    return this.currentUserRole === 'ADMIN' || this.currentUserRole === 'MODERATOR'|| this.currentUserRole === 'USER';
  }
}
