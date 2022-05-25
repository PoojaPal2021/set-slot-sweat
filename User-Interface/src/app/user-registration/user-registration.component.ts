import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ScheduleSessionService } from '../services/schedule-session.service';
import { Observable } from 'rxjs';
@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.css']
})

/*
  UserRegistrationComponent component is reponsible for new user registrations and validations.
  potential users of this components are new members and new trainers
*/
export class UserRegistrationComponent implements OnInit {
  profileForm!: FormGroup; //captures form data filled by the new user

  /*
       by default the application is currently used only by the new memeber registrations.
       trainer functionality is disabled
  */
  memberValue = "member";

  /* properties that displays error or success messages */
  successMessage: any = "";
  errorMessage: any = "";


  constructor(private scheduleSessionService: ScheduleSessionService) { }

  /* On component load user should be provided with reactive forms */
  ngOnInit() {
    this.profileForm = new FormGroup
      ({
        firstName: new FormControl(''),
        lastName: new FormControl(''),
        email: new FormControl('', [Validators.email]),
        password: new FormControl('', [Validators.required, Validators.minLength(6)]),
        type: new FormControl('')
      });
  }

  /* 
      registerNewUser validates the input information and registers new users into set slot sweat system
      @input: form with mandatory informations to register new user 
      @output: returns success message  if the form is validated, no duplicate user exists
              return error message on non validation criteria
  */
  registerNewUser(profileForm: FormGroup) {// TODO: Use EventEmitter with form value
    if (profileForm.invalid) {
      return
    }
    this.errorMessage = "";
    this.successMessage = "";
    console.warn(profileForm.value);

    if (profileForm.controls['type'].value == 'member') {
      this.scheduleSessionService.registerNewMember(profileForm)
        .subscribe((data: any) => {
          console.log(" DATA", data.message);
          this.successMessage = data.message;
          console.log("COMPONENT :: registerNewUser :: SUCCESS =>", data.message)
        }, (error: any) => {
          this.errorMessage = error;
          console.log("COMPONENT :: registerNewUser :: ERROR =>", error)
        });
    }
  }
}
