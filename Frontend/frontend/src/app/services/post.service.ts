import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Post {
  id: number;
  title: string;
  content: string;
  image: any;  // Тут зміни тип на any, щоб приймати байти
  user: User;
  createdAt: Date;
  updatedAt: Date;
  likes: Like[];
  status?: string
}

export interface User {
  id: number;
  lastName?: string;
  firstName?: string;
}

export interface Like {
  id: number;
  user: User;
}
export interface FAQ {
  id: number;
  question: string;
  answer: string;
  user: {
    id: number;
    username: string;
  };
  createdAt: Date;
  updatedAt: Date;
  isOpen: boolean;
}


@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = 'http://localhost:8080/api/v1/posts';

  constructor(private http: HttpClient) {}

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiUrl);
  }
  getUserPosts(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/user/${userId}`);
  }
  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  createPost(post: Partial<Post>, imageFile: File): Observable<Post> {
    const formData: FormData = new FormData();

    formData.append('post', JSON.stringify(post));
    formData.append('imageFile', imageFile, imageFile.name);

    return this.http.post<Post>(this.apiUrl, formData);
  }


  updatePost(id: number, updatedPost: Post): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${id}`, updatedPost);
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  getLikedPosts(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/user/${userId}/likes`);
  }
  toggleLike(postId: number, userId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${postId}/toggle-like/${userId}`, {});
  }
  approvePost(postId: number): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${postId}/approve`,{});
  }
  getPostsByStatus(status: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/status/${status}`);
  }

  rejectPost(postId: number): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${postId}/reject`, {});
  }
}
