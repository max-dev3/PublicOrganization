import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { HomeComponent } from './home/home.component';
import { ProjectsComponent } from './projects/projects.component';
import { AboutComponent } from './about/about.component';
import { ContactsComponent } from './contacts/contacts.component';
import { LoginComponent } from './login/login.component';
import { FooterComponent } from './footer/footer.component';
import {HttpClientModule} from "@angular/common/http";
import { RegisterComponent } from './register/register.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { ProfileComponent } from './profile/profile.component';
import { DeleteDialogComponent } from './delete-dialog/delete-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";
import {MatTabsModule} from "@angular/material/tabs";
import { CreateProjectComponent } from './create-project/create-project.component';
import {MatIconModule} from "@angular/material/icon";
import {MatBadgeModule} from "@angular/material/badge";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { UkDatePipe } from './uk-date.pipe';
import {CKEditorModule} from "@ckeditor/ckeditor5-angular";
import { ViewPostComponent } from './view-post/view-post.component';
import { StatusTranslatePipe } from './status-translate.pipe';
import { UserListComponent } from './user-list/user-list.component';
import {MatTableModule} from "@angular/material/table";
import { FaqComponent } from './faq/faq.component';
import {MatExpansionModule} from "@angular/material/expansion";
import {MatInputModule} from "@angular/material/input";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HomeComponent,
    ProjectsComponent,
    AboutComponent,
    ContactsComponent,
    LoginComponent,
    FooterComponent,
    RegisterComponent,
    ProfileComponent,
    DeleteDialogComponent,
    CreateProjectComponent,
    UkDatePipe,
    ViewPostComponent,
    StatusTranslatePipe,
    UserListComponent,
    FaqComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatTabsModule,
    MatIconModule,
    MatBadgeModule,
    BrowserAnimationsModule,
    CKEditorModule,
    MatTableModule,
    MatExpansionModule,
    MatInputModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
