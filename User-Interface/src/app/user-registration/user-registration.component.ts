import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ScheduleSessionService } from '../services/schedule-session.service';
import {Observable} from 'rxjs';
@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.css']
})
export class UserRegistrationComponent implements OnInit {
  profileForm!: FormGroup;
  successMessage:any="";
  errorMessage:any="";
  
  constructor(private scheduleSessionService: ScheduleSessionService) { }
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

  registerNewUser(profileForm: FormGroup) {// TODO: Use EventEmitter with form value
    if (profileForm.invalid) {
      return
    }
    this.errorMessage = "";
    this.successMessage="";
    console.warn(profileForm.value);
    console.log(" type ==>", profileForm.controls['type'].value);

    if (profileForm.controls['type'].value == 'member')
    {
      this.scheduleSessionService.registerNewMember(profileForm)
      .subscribe((data: any)=>{
        console.log(" DATA", data.message);
        this.successMessage = data.message;
        // this.products = data;
      }, (error:any)=>{
        this.errorMessage = error;
        console.log("Error ==>", error)
      });
    }
    else 
    {

      this.scheduleSessionService.registerNewTrainer(profileForm).subscribe((data: any)=>{
        console.log(data);
        // this.products = data;
      });
    }

  }
}
