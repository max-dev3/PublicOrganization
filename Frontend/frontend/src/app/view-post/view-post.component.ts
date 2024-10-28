import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PostService, Post } from '../services/post.service';

@Component({
  selector: 'app-view-post',
  templateUrl: './view-post.component.html',
  styleUrls: ['./view-post.component.css']
})
export class ViewPostComponent implements OnInit {
  post: Post | undefined;
  imageStr: string = '';

  constructor(private route: ActivatedRoute, private postService: PostService) { }

  ngOnInit(): void {
    const postId = this.route.snapshot.paramMap.get('id');
    if (postId) {
      this.postService.getPostById(+postId).subscribe(post => {
        this.post = post;
        this.imageStr = `http://localhost:8080/api/v1/posts/${post.id}/image`;
      });
    }
  }


}
