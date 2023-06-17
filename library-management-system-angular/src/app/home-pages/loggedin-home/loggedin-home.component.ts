import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { AppService } from '../../services/app.service';
import { User, Book } from '../../app.interfaces';

@Component({
  selector: 'app-loggedin-home',
  templateUrl: './loggedin-home.component.html',
  styleUrls: ['./loggedin-home.component.css']
})
export class LoggedinHomeComponent implements OnInit {
  constructor(private authService: AuthService, private appService: AppService) { }

  user: User | undefined;
  loggedInUserId$: number | undefined;
  loggedInUsername: string | undefined;

  ngOnInit(): void {
    this.loggedInUserId$ = this.authService.loggedInUserId;
    if (this.loggedInUserId$ != undefined) {
      this.appService.getUserById(this.loggedInUserId$).subscribe((user: User) => {
        this.user = user;
        this.loggedInUsername = user.username;
      });
    }
  }

  returnBook(userId: number, bookId: number): void {
    this.appService.returnBook(userId, bookId).subscribe(() => {
      this.appService.getUserById(userId).subscribe((user: User) => {
        this.user = user;
      });
    });
  }
}
