import { Component, OnInit } from '@angular/core';
import { ScheduleSessionService } from '../services/schedule-session.service';
import { schedule } from '../models/schedule';
import { FormGroup, FormControl, Validators } from '@angular/forms';
@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
export class UserLoginComponent implements OnInit {

  sessionInfo: schedule | undefined;
  // authenticatedUser = true;
  authenticatedUser = false;
  allSessionInfo: any;
  singSessionInfo: any;
  userEmail: string = "";
  hideLoginForm: boolean = false;
  enableBookButton: boolean = true;
  now: any = new Date();

  loginForm = new FormGroup(
    {
      email: new FormControl('', Validators.email),
      pwd: new FormControl('', Validators.required),
    }
  );
  constructor(private scheduleSessionService: ScheduleSessionService) { }

  ngOnInit(): void {
  }

  loadProfile() {
    console.log("Login json-->", this.loginForm)

    if (this.authenticatedUser) {
      console.log(" Flag  and inside IF  ==>", this.authenticatedUser)
      console.log("Noq ==>", this.now)
      console.log("Day   ==>", this.now.toLocaleDateString())
      this.scheduleSessionService.loadProfileData()
        .subscribe
        (
          (data: schedule) => {
            this.hideLoginForm = true;
            this.sessionInfo = { ...data };
            console.log(" mapped to model ===>", this.sessionInfo)
            this.userEmail = this.sessionInfo.email;
            this.allSessionInfo = this.sessionInfo.profile;
            this.authenticatedUser = true;
          }

        );
    }
    else {
      //check if authenticated user 
      this.scheduleSessionService.authenticateUser(this.loginForm);

    }

  }


  bookSession(singSessionInfo: any) {

    singSessionInfo.sStatus = true;
    this.scheduleSessionService.bookSession(singSessionInfo)

  }

  cancelSession(singSessionInfo: any) {
    console.log("Inside Book session", singSessionInfo.sTrainer)
    console.log("Inside Book session", singSessionInfo.sSlot)
    console.log("Inside Book session", singSessionInfo.sTitle)
    console.log("Inside Book session", singSessionInfo.sStatus)
    singSessionInfo.sStatus = false;
    console.log("Inside Book session", singSessionInfo.sStatus)
    this.scheduleSessionService.cancelSession(singSessionInfo)

  }





}
