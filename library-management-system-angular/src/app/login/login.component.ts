import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { timer } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  form: FormGroup;
  errorMessage: string = '';

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    const { username, password } = this.form.value;
    
    this.authService.login(username, password);

    // Delay before displaying the error message
    timer(1500).subscribe(() => {
      this.authService.isLoggedIn.subscribe(isLoggedIn => {
        if (!isLoggedIn) {
          this.errorMessage = 'Bad credentials';
        }
      });
    });
  }
}
