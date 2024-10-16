import {Component, ElementRef, HostListener} from '@angular/core';
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  menuOpen = false;
  isLoggedIn = false;
  currentUserRole: string = this.authService.getUser()?.role;

  constructor(private elRef: ElementRef,private authService: AuthService) {}
  ngOnInit(): void {
    this.authService.isLoggedIn().subscribe((loggedIn) => {
      this.isLoggedIn = loggedIn;
      this.isAdmin();
    });
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elRef.nativeElement.contains(event.target)) {
      this.menuOpen = false;
    }
  }
  logout(): void {
    this.authService.logout();
  }

  isAdmin(): boolean {
    this.currentUserRole = this.authService.getUser()?.role;
    return this.currentUserRole === 'ADMIN';
  }



}
