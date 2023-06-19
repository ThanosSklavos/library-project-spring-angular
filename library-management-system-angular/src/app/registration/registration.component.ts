import { Component } from '@angular/core';
import { User } from '../app.interfaces';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppService } from '../services/app.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})

export class RegistrationComponent {
  form: FormGroup;
  message: string = '';

  constructor(private fb: FormBuilder, private appService: AppService, private router: Router) {
    this.form = this.fb.group({
      username: ['', [ Validators.required, Validators.minLength(3) ]],
      password: ['', [ Validators.required, Validators.minLength(3) ]],
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      const user = this.form.value as User;
      this.appService.register(user).subscribe((response) => {
        console.log(response);
        this.router.navigate(['login']);
      }, (error) => {
        this.message = "Username already in use"
      })
    }
  }
}