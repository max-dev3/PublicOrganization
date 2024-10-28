import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { PostService, Post } from "../services/post.service";
import {Router} from "@angular/router";
import {AuthService} from "../services/auth.service";
import * as ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import { Editor } from '@ckeditor/ckeditor5-core';

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.component.html',
  styleUrls: ['./create-project.component.css']
})
export class CreateProjectComponent {
  createPostForm: FormGroup;
  submitted = false;
  imageFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;
  public Editor: any = ClassicEditor;

  constructor(private formBuilder: FormBuilder, private userService: AuthService, private router: Router, private postService: PostService) {
    this.createPostForm = this.formBuilder.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
      image: [null],
      fileSelected: [null, Validators.required],
    });
  }

  ngOnInit() { }

  get f() { return this.createPostForm.controls; }

  onFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    const files = target.files as FileList;
    if (files.length > 0) {
      this.imageFile = files[0];
      this.createPostForm.get('fileSelected')?.setValue(true);

      const reader = new FileReader();

      reader.onload = (e: ProgressEvent<FileReader>) => {
        if(e.target?.result) {
          this.imagePreview = e.target.result;
        }
      };

      reader.readAsDataURL(this.imageFile);
    } else {
      this.createPostForm.get('fileSelected')?.setValue(null);
    }
    console.log(this.createPostForm.value)
  }

  onSubmit() {
    this.submitted = true;

    if (this.createPostForm.invalid) {
      return;
    }

    const post: { title: any; content: any,user: { id: number } } = {
      title: this.createPostForm.get('title')?.value,
      content: this.createPostForm.get('content')?.value,
      user: { id: this.userService.getUser().id }
    };

    if (this.imageFile) {
      this.postService.createPost(post, this.imageFile).subscribe(
        data => {
          this.router.navigate(['/profile']);
        },
        error => {
          // handle error
          console.error(error);

        }
      );
    }
  }
}
