import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { Book, User } from '../app.interfaces'
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root',
})
export class AppService {

  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();

  private loggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.loggedInSubject.asObservable();

  private loggedInUserFullnameSubject = new BehaviorSubject<string>('');
  loggedInUserFullname$ = this.loggedInUserFullnameSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}
  
  // THIS IS THE USER SERVICE.
  register(user: User) {
    return this.http.post<User>('http://localhost:8080/api/users', user)
  }

  getUserById(userId: number) {
    return this.http.get<User>(`http://localhost:8080/api/users/${userId}`);
  }

  returnBook(userId: number, bookId: number) {
    return this.http.delete<void>(`http://localhost:8080/api/users/deleteBook/${userId}/${bookId}`);
  }

  rentBook(userId: number, bookId: number) {
    return this.http.put<void>(`http://localhost:8080/api/users/addBook/${userId}/${bookId}`, null);
  }

   // THIS IS THE BOOK SERVICE
  getAllBooks() {
    return this.http.get<Book[]>('http://localhost:8080/api/books/getAll');
  }

  registerBook(newBook: Book){
    return this.http.post<Book>('http://localhost:8080/api/books', newBook)
  }

  deleteBook(bookId: number) {
    return this.http.delete<Book>(`http://localhost:8080/api/books/${bookId}`)
  }
}
