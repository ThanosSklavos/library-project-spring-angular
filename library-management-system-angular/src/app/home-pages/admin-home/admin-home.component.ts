import { Component, OnInit } from '@angular/core';
import { Book, User } from 'src/app/app.interfaces';
import { AppService } from 'src/app/services/app.service';
import { AuthService } from 'src/app/services/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-home',
  templateUrl: './admin-home.component.html',
  styleUrls: ['./admin-home.component.css']
})
export class AdminHomeComponent implements OnInit {
  bookList: Book[] = [];
  isLoggedIn: boolean = false;
  loggedInUserId: number | undefined;
  insertMessage: string = '';
  deleteBookMessage: string = '';
  bookForm: FormGroup;

  constructor(
    private appService: AppService,
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.bookForm = this.fb.group({
      title: ['', Validators.required],
      numberOfCopies: ['', Validators.required],
      authorFirstname: ['', Validators.required],
      authorLastname: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.authService.isLoggedIn.subscribe((isLoggedIn: boolean) => {
      this.isLoggedIn = isLoggedIn;

      if(!isLoggedIn) {
        this.router.navigate([''])
        return;
      }

      this.fetchBookList();

    });

    this.loggedInUserId = this.authService.loggedInUserId;
  }

  deleteBook(bookId: number) {
    this.deleteBookMessage = '';
    this.insertMessage = '';
    
    this.appService.deleteBook(bookId).subscribe({
      next: (response) => {
        console.log('Book deleted: ' + response.title + ' ' + response.id + ' by ' + response.authorLastname);
      },
      error: (error) => {
        this.deleteBookMessage = "Error in book delete, you cannot delete a book rented by a user";
      },
      complete: () => {
        this.fetchBookList();
      }
    });
  }

  onSubmit() {
    this.deleteBookMessage = '';
    this.insertMessage = '';
    if (this.bookForm.valid) {
      const aBook: Book = this.bookForm.value as Book;

      this.appService.registerBook(aBook).subscribe(
        (response) => {
          console.log('Book registered: ' + response)
          if (response.id != 0 || response.id != undefined) {
            this.insertMessage = 'Book registered successfully';
            this.fetchBookList();
          } else {
            this.insertMessage = 'Error in book insertion';
          }
          this.bookForm.reset();
        }, (error) => {
          this.insertMessage = 'Error in book insertion';
        }
      );
    }
  }

  fetchBookList() {
    this.appService.getAllBooks().subscribe((books: Book[]) => {
      this.bookList = books;
      console.log(books);

      // Fetch users that has rented each book
      this.bookList.forEach((book: Book) => {
        this.appService.getUsersByBookId(book.id).subscribe({
          next: (response) => {
            response.forEach((user: User) => {
              book.rentByUser.push(user.username)
            })
          }
      });
    });
  })
}
}






