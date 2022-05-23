import { Component, OnInit,Output,EventEmitter, ɵɵsetComponentScope } from '@angular/core';
import { ScheduleSessionService } from '../services/schedule-session.service';
import { schedule } from '../models/schedule';
import {upcomingSchedule} from '../models/upcomingSchedule'
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { getLocaleCurrencyName, getLocaleDateFormat, getLocaleDayNames, getLocaleWeekEndRange } from '@angular/common';
@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
export class UserLoginComponent implements OnInit {
  @Output() loginStatus: EventEmitter<boolean> =   new EventEmitter();
  sessionInfo: schedule[] | undefined;
  upcomingSessionInfo: schedule[] | undefined;
  
  authenticatedUser = false;
  hideLoginForm: boolean = false;
  showloginTemp: boolean = false;
  userEmail: string = "";


  allSessionInfo: any;
  singSessionInfo: any;

  /*arra holding 7 days from now */
  weekDaysFrmTdy: any = new Array(7);


  enableBookButton: boolean = true;
  now: any = new Date();
  loginForm = new FormGroup(
    {
      email: new FormControl('', Validators.email),
      password: new FormControl('', [Validators.required, Validators.minLength(4)]),
    }
  );

  constructor(private scheduleSessionService: ScheduleSessionService) { }

  ngOnInit(): void {
  }

  loadProfile(loginForm: FormGroup) {
    

    console.log("Login Form .. cannot stringify  -->", loginForm);
    this.userEmail = loginForm.value['email'];
    

    // if (!this.authenticatedUser) {
    // console.log(" Flag  and inside IF  ==>", this.authenticatedUser)
    // console.log("Noq ==>", this.now)
    // console.log("Day   ==>", this.now.toLocaleDateString())
    // this.scheduleSessionService.scheduleSessionService(LoginForm)
    // .subscribe
    // (
    //   (data: schedule) => {
    //     this.hideLoginForm = true;
    //     this.sessionInfo = { ...data };
    //     console.log(" mapped to model ===>", this.sessionInfo)
    //     this.userEmail = this.sessionInfo.email;
    //     this.allSessionInfo = this.sessionInfo.profile;
    //     this.authenticatedUser = true;
    //   }
    // );
    
    this.scheduleSessionService.authenticateAndloadProfileData(loginForm).subscribe((data: any) => {

      this.hideLoginForm = true;
      this.showloginTemp = true;
      
      this.loginStatus.emit(this.hideLoginForm);
      this.sessionInfo = data;
      console.log("LOADED DATA =>", this.sessionInfo);
      if (this.sessionInfo != undefined) {
        this.getWeekdays();
        this.convertDateTo12hFormat(this.sessionInfo);

      }
    })
    this.scheduleSessionService.authenticateAndloadUpcomingSessions(this.userEmail).subscribe((data: any) => {

      this.hideLoginForm = true;
      this.showloginTemp = true;

      this.upcomingSessionInfo = data;
      console.log("LOADED UPCOMING SESSION DATA =>", this.upcomingSessionInfo);
    });

    this.loginForm.reset()

  }

  convertDateTo12hFormat(allsessionInfo: schedule[]) {

    // allsessionInfo.forEach(singleSessionInfo => {
    //   const timeString = singleSessionInfo.session.startTime;
    //   const timeString12hr = new Date('2022-05-20' + timeString + 'Z')
    //     .toLocaleTimeString('en-US',
    //       // { timeZone: 'PST', hour12: true, hour: 'numeric', minute: 'numeric' }
    //       { timeZone: 'PST', hour12: true, hour: 'numeric' }
    //     );
    //     singleSessionInfo.session.startTime = timeString12hr.toString(); 
    // });
    //  allsessionInfo.forEach(singleSessionInfo => {

    //   if singleSessionInfo.session.dayOfWeek == 'MONDAY'
    // });

  }





  bookSession(singSessionInfo: any) {
    console.log(" User logged in ===>", this.userEmail)
    singSessionInfo.booked = true;
    this.scheduleSessionService.bookSession(singSessionInfo, this.userEmail).subscribe((data: any) => {
      console.log("RESPONSE FROM BACKEND ON booking", data);
      this.sessionInfo = data;
    })

  }

  cancelSession(singSessionInfo: any) {
    console.log(" User logged in ===>", this.userEmail)
    console.log(" reservation Id =>", this.sessionInfo)
    singSessionInfo.booked = false;
    this.scheduleSessionService.cancelSession(singSessionInfo, this.userEmail);
  }

  getWeekdays() {
    var dayNames = ['SUN', 'MON', 'TUE', 'WED', 'THUR', 'FRI', 'SAT' ]; 
    var today = new Date();
    console.log("today", today.getDay());
    var todayNum = today.getDay(); // 0 - SUNDAY , 6-Saturday
  
    console.log("TODAY NUM ===>", todayNum)
    var day: number;
    for (day = 0; day <= 6; day++) // today+0 = today , today +1 = tomorrow
    {
        this.weekDaysFrmTdy[day] = (dayNames[(todayNum+day)%7]);  
    }
  }
}

