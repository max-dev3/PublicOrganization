import { Component, OnInit } from '@angular/core';
import {AuthService} from "../services/auth.service";


@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  users: any[] = [];

  constructor(private userService: AuthService) {}

  ngOnInit(): void {
    this.userService.getUsers().subscribe(users => {
      this.users = users;
    });
  }

  changeUserRole(userId: number, role: string): void {
    this.userService.changeUserRole(userId, role).subscribe(user => {
      // Find the index of the user in the users array
      const index = this.users.findIndex(u => u.id === user.id);

      // Update the user role in the users array
      this.users[index] = user;
    });
  }
}
