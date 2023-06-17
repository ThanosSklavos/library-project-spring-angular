import { Injectable} from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { LoginDTO } from '../app.interfaces';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isLoggedInSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  isLoggedIn = this.isLoggedInSubject.asObservable();

  loggedInUserId: number | undefined;
  loggedInUsername: string | undefined;

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string) {
    const loggedInUserId = 0;
    const loginDTO: LoginDTO = { username, password, loggedInUserId };
    return this.http.post<LoginDTO>(`http://localhost:8080/api/users/login`, loginDTO).subscribe(
      (response => {
        if (response.loggedInUserId === null || response.loggedInUserId === 0) {
          this.isLoggedInSubject.next(false);
          return;
        }
        
        //store the id and username to be used globally.
        this.loggedInUserId = response.loggedInUserId;
        this.loggedInUsername = response.username;

        if (response.username === 'admin') {
          console.log(response);
          this.isLoggedInSubject.next(true);
          this.router.navigate(['/admin-home']);
        } else {
          console.log(response);
          this.isLoggedInSubject.next(true);
          this.router.navigate(['/loggedin-home']);
        }
      })
    );
  }

  logout() {
    this.http.delete<any>('http://localhost:8080/api/users/logout').subscribe(
      () => {
        this.isLoggedInSubject.next(false);
        this.loggedInUserId = undefined;
        this.router.navigate(['']);
      }
    );
  }
}