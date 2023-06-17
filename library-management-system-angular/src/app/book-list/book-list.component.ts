import { Component, OnInit } from '@angular/core';
import { AppService } from '../services/app.service';
import { Book } from '../app.interfaces';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css']
})
export class BookListComponent implements OnInit {
  bookList: Book[] = [];
  isLoggedIn: boolean = false;
  loggedInUserId: number | undefined;
  successMessage: string = '';
  errorMessage: string = '';
  successBookId: number | undefined;
  errorBookId: number | undefined;

  constructor(private appService: AppService, private authService: AuthService, private router: Router,) {}

  ngOnInit(): void {
    this.fetchBookList();

    // Check if the user is logged in
    this.authService.isLoggedIn.subscribe((isLoggedIn: boolean) => {
      this.isLoggedIn = isLoggedIn;

      if(!isLoggedIn) {
        this.router.navigate([''])
      }
    });

    this.loggedInUserId = this.authService.loggedInUserId;
  }

  rentBook(bookId: number) {
    // Perform book rental
    if (this.isLoggedIn && this.loggedInUserId) {
      this.appService.rentBook(this.loggedInUserId, bookId).subscribe(
        () => {
          this.successMessage = 'Book rented successfully';
          this.successBookId = bookId;
          this.errorBookId = undefined;
          this.fetchBookList(); // Fetch the updated book list
        },
        (error) => {
          this.errorMessage = 'Failed to rent the book';
          this.errorBookId = bookId;
          this.successBookId = undefined;
        }
      );
    }
  }

  fetchBookList() {
    this.appService.getAllBooks().subscribe((books: Book[]) => {
      this.bookList = books;
    });
  }
}