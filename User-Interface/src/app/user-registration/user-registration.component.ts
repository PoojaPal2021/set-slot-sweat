import { Component, OnInit } from '@angular/core';
import { FormGroup,FormControl, Validators } from '@angular/forms';
import { ScheduleSessionService } from '../services/schedule-session.service';

@Component({
  selector: 'app-user-registration',
  templateUrl: './user-registration.component.html',
  styleUrls: ['./user-registration.component.css']
})
export class UserRegistrationComponent implements OnInit {
  profileForm!: FormGroup;
  constructor(private scheduleSessionService:ScheduleSessionService) { }
  ngOnInit() {
  
  this.profileForm = new FormGroup
  ({

  firstName: new FormControl(''),
  lastName: new FormControl(''),
  email: new FormControl('', [Validators.email]),
  pwd: new FormControl('',[Validators.required, Validators.minLength(6)]),
  type: new FormControl('')
    });
  }


  registerNewMember() {// TODO: Use EventEmitter with form value
    if(this.profileForm.invalid)
    {
      return
    }
    console.warn(this.profileForm.value);
    this.scheduleSessionService.registerUser(this.profileForm.value);
  }
}
