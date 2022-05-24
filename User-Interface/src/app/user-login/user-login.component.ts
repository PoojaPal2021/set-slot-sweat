import { Component, OnInit, Output, EventEmitter, ɵɵsetComponentScope } from '@angular/core';
import { ScheduleSessionService } from '../services/schedule-session.service';
import { schedule } from '../models/schedule';
import { upcomingSchedule } from '../models/upcomingSchedule'
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { getLocaleCurrencyName, getLocaleDateFormat, getLocaleDayNames, getLocaleWeekEndRange } from '@angular/common';
import { graphData } from '../models/analytics';
import { ChartType, ChartOptions } from 'chart.js';
import { SingleDataSet, Label, monkeyPatchChartJsLegend, monkeyPatchChartJsTooltip } from 'ng2-charts';

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
export class UserLoginComponent implements OnInit {
  @Output() loginStatus: EventEmitter<boolean> = new EventEmitter();
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

  /*Graph mapping*/
  public pieChartOptions: ChartOptions = {
    responsive: true,

  };
  public pieChartLabels: Label[] = [];
  public pieChartData: SingleDataSet = [];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartPlugins = [];

  historyData: graphData | undefined;
  categorisedHistory = [];
  totalSessionConducted: number | undefined;
  totalSessionAttended: number | undefined;
  favoriteSession: string | undefined;

  public chartColors: Array<any> = [
    { // all colors in order
      backgroundColor: ['#9dc5a8', '#5d946c', '#89ab80c3', '#8d944fc3', '9cb664']
    }
  ]


  enableBookButton: boolean = true;
  now: any = new Date();
  loginForm = new FormGroup(
    {
      email: new FormControl('', Validators.email),
      password: new FormControl('', [Validators.required, Validators.minLength(4)]),
    }
  );

  constructor(private scheduleSessionService: ScheduleSessionService) {
    monkeyPatchChartJsTooltip();
    monkeyPatchChartJsLegend();
  }

  ngOnInit(): void {
  }

  loadProfile(loginForm: FormGroup) {

    console.log("Login Form .. cannot stringify  -->", loginForm);
    this.userEmail = loginForm.value['email'];

    this.loadManageData();
    this.loadUpcomingSessionData();

  
  }

  loadManageData(){

    console.log(" Cred ==>", this.loginForm.value['email'],   "", this.loginForm.value['password'])
    this.scheduleSessionService.authenticateAndloadProfileData(this.loginForm).subscribe((data: any) => {
      this.hideLoginForm = true;
      this.showloginTemp = true;

      this.loginStatus.emit(this.hideLoginForm);
      this.sessionInfo = data;
      console.log("LOADED DATA =>", this.sessionInfo);
      if (this.sessionInfo != undefined) {
        this.getWeekdays();
        this.convertDateTo12hFormat(this.sessionInfo);
        this.genHistoryReports()
      }
    })
  }

  loadUpcomingSessionData()
  {
    this.scheduleSessionService.authenticateAndloadUpcomingSessions(this.userEmail).subscribe((data: any) => {

      this.hideLoginForm = true;
      this.showloginTemp = true;

      this.upcomingSessionInfo = data;
      if (this.upcomingSessionInfo != undefined) {
        this.convertDateTo12hFormat(this.upcomingSessionInfo);
      }
      console.log("LOADED UPCOMING SESSION DATA =>", this.upcomingSessionInfo);
    });
  }

  convertDateTo12hFormat(allsessionInfo: schedule[]) {

    allsessionInfo.forEach(singleSessionInfo => {
      const timeString = singleSessionInfo.session.startTime;
      const timeString12hr = new Date('1984-06-08T' + timeString + 'Z')
        .toLocaleTimeString('en-US',
          { timeZone: 'UTC', hour12: true, hour: 'numeric', minute:'numeric' }
        );
        singleSessionInfo.session.startTime = timeString12hr;
    });
  }





  bookSession(singSessionInfo: any, tab: string) {
    console.log(" User logged in ===>", this.userEmail)

    this.scheduleSessionService.bookSession(singSessionInfo, this.userEmail).subscribe((data: any) => {
      console.log("RESPONSE FROM BACKEND ON booking", data);
      singSessionInfo.booked = true;
      this.sessionInfo = data;
    });

    if (tab == 'manage') {
      this.loadUpcomingSessionData();
    }

  }

  cancelSession(singSessionInfo: any, tab: string) {

    this.scheduleSessionService.cancelSession(singSessionInfo, this.userEmail).subscribe((data: any) => {
      console.log("RESPONSE FROM BACKEND ON booking", data);
      singSessionInfo.booked = false;
    });

    if (tab == 'upcoming') {
      this.loadManageData();
    }else if (tab == 'manage'){
      this.loadUpcomingSessionData();
    }
  }

  updateUserDetails() {
    console.log("Addded", this.userEmail)
  }



  genHistoryReports() {

    let sessionNumbers: number[] = Array();
    this.scheduleSessionService.genHistoryReports(this.userEmail).subscribe((data: any) => {
      this.hideLoginForm = true;
      this.showloginTemp = true;
      this.historyData = data;
      this.totalSessionConducted = this.historyData?.totalSessions;
      this.totalSessionAttended = this.historyData?.totalAttended;

      console.log("HISTORY DATA  =>", this.historyData);
      this.historyData?.workoutHistory.forEach(element => {
        this.pieChartLabels.push(element.name);
        this.pieChartData.push(element.attended);
        sessionNumbers.push(element.attended);
      });


      console.log(" FAvourite Session ==>", this.pieChartLabels[sessionNumbers.indexOf(Math.max(...sessionNumbers))]);
      this.favoriteSession = this.pieChartLabels[sessionNumbers.indexOf(Math.max(...sessionNumbers))].toString();
      console.log(" Pie chart data ==>", this.pieChartData);
    });
  }




  getWeekdays() {
    var dayNames = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
    var today = new Date();
    console.log("today", today.getDay());
    var todayNum = today.getDay(); // 0 - SUNDAY , 6-Saturday

    console.log("TODAY NUM ===>", todayNum)
    var day: number;
    for (day = 0; day <= 6; day++) // today+0 = today , today +1 = tomorrow
    {
      this.weekDaysFrmTdy[day] = (dayNames[(todayNum + day) % 7]);
    }
  }
}

