import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  isLoggedIn: boolean = false;
  loggedInUsername$: string | undefined

  constructor(private authService: AuthService) {}

  ngOnInit() {
      this.authService.isLoggedIn.subscribe((isLoggedIn: boolean) => {
        this.isLoggedIn = isLoggedIn;
      });
  }

  getBrowseLink() {
    this.loggedInUsername$ = this.authService.loggedInUsername;

    if (this.loggedInUsername$ === 'admin') {
      return ['/admin-home'];
    } else {
      return ['/books'];
    }
  }

  logout() {
    this.authService.logout();
  }
}
