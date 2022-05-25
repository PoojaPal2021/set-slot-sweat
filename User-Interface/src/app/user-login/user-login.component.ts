import { Component, OnInit, Output, EventEmitter, ɵɵsetComponentScope } from '@angular/core';
import { ScheduleSessionService } from '../services/schedule-session.service';
import { schedule } from '../models/schedule';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { graphData } from '../models/analytics';
import { ChartType, ChartOptions } from 'chart.js';
import { SingleDataSet, Label, monkeyPatchChartJsLegend, monkeyPatchChartJsTooltip } from 'ng2-charts';

@Component({
  selector: 'app-user-login',
  templateUrl: './user-login.component.html',
  styleUrls: ['./user-login.component.css']
})
/*

  UserLoginComponent component is reponsible for gym members login creditial varification to authenticate the user.
  It gives provision to view the booked upcoming sessions, all the availble sessions and the reports.
  It has methods to book a new available session, cancel booked session 
  potential users of this components are new members and new trainers

*/
export class UserLoginComponent implements OnInit {
  /* @output is used to emit teh events to other components for communication */
  @Output() loginStatus: EventEmitter<boolean> = new EventEmitter();

  /* Helper objects to map reponse data from the services  */
  sessionInfo: schedule[] | undefined;
  upcomingSessionInfo: schedule[] | undefined;
  userEmail: string = "";

  /* properties that gets assigned with either success or failure messages */
  successMessage: any = "";
  loginErrorMessage: any = "";

  /* flags to manipulate the display of the template elements */
  authenticatedUser = false;
  hideLoginForm: boolean = false;
  showloginTemp: boolean = false;
  isPresentUpSesh: boolean = false;
  
  /* default message when there are no sessions booked */
  infoMessage: string = " There are no Upcoming Session scheduled";
  allSessionInfo: any;
  singSessionInfo: any;

  /*arra holding 7 days from now */
  weekDaysFrmTdy: any = new Array(7);

  /* History reports supported structures fron charts.js */
  public pieChartOptions: ChartOptions = {
    responsive: true,
  };
  public pieChartLabels: Label[] = [];
  public pieChartData: SingleDataSet = [];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartPlugins = [];
  public chartColors: Array<any> = [
    { // all colors in order
      backgroundColor: ['#9dc5a8', '#5d946c', '#89ab80c3', '#8d944fc3', '#536157', '#649472', '#7c9583']
    }
  ]
  historyData: graphData | undefined;
  categorisedHistory = [];

  /*key attributes captures for analysis */
  totalSessionConducted: number | undefined;
  totalSessionAttended: number | undefined;
  favoriteSession: string | undefined;

  enableBookButton: boolean = true;
  now: any = new Date();
  loginForm = new FormGroup(
    {
      email: new FormControl('', Validators.email),
      password: new FormControl('', [Validators.required, Validators.minLength(4)]),
    }
  );

  constructor(private scheduleSessionService: ScheduleSessionService) {
    /* on charts hover adds effects */
    monkeyPatchChartJsTooltip();
    monkeyPatchChartJsLegend();
  }

  ngOnInit(): void {
  }

  /* 
    load profiles gets the member account
    loads upcoming sessions based on the user activity 
    loads all the available upcoming sessions for the next 7 days 
    generates reports based on the part gym memebers sessions 
  */
  loadProfile(loginForm: FormGroup) {
    console.log(" COMPONENT :: USER LOGIN :: loadProfile =>", loginForm);
    this.userEmail = loginForm.value['email'];
    this.loadManageData();
  }

  /*
  Helper function to emit the events that to be captured by the app component
  */
  logOutEventEvent() {
    console.log(" COMPONENT :: USER LOGIN :: logOutEventEvent.");
    this.scheduleSessionService.AClicked("logout");
  }

  /* 
    Loads the booked sessions and available session for the logged in used after authentication
  */
  loadManageData() 
  {
    /* intialization */
    this.historyDataReset();     
    this.loginErrorMessage = "";

    /* invokes authentication service */
    this.scheduleSessionService.authenticateAndloadProfileData(this.loginForm)
      .subscribe(
        (data: any) => {
          /* set the flags for manipulating template elelemnts */
          this.hideLoginForm = true;
          this.showloginTemp = true;

          this.loginStatus.emit(this.hideLoginForm);
          this.sessionInfo = data;
          console.log(" ALL THE AVAILABLE SESSIONS  =>", this.sessionInfo);
          if (this.sessionInfo != undefined) {
            this.getWeekdays();
            this.convertDateTo12hFormat(this.sessionInfo);
            this.genHistoryReports()
          }
          this.loadUpcomingSessionData();
          this.scheduleSessionService.AClicked("login");
        },
        (error: any) => {
          console.log("ERROR")
          console.log(" making false in error")
          this.showloginTemp = false;
          this.loginErrorMessage = error;
          console.log(" Login error message ===>", this.loginErrorMessage)
        }
      )
  }

  loadUpcomingSessionData() {
    console.log(" COMPONENT :: loadUpcomingSessionData ");
    this.loginErrorMessage = "";
    this.scheduleSessionService.authenticateAndloadUpcomingSessions(this.userEmail).subscribe((data: any) => {

      this.hideLoginForm = true;
      this.showloginTemp = true;

      this.upcomingSessionInfo = data;

      if (this.upcomingSessionInfo != undefined) {
        this.convertDateTo12hFormat(this.upcomingSessionInfo);

        if (this.upcomingSessionInfo?.length > 0) 
        {
          this.isPresentUpSesh = true;
        }
      }
      console.log("COMPONENT :: loadUpcomingSessionData :: DATA =>", this.upcomingSessionInfo);
    });
  }

  /*
  Function that converts 24h format data time into 12h, AM-PM style format for UTC timezone
  */
  convertDateTo12hFormat(allsessionInfo: schedule[]) {

    allsessionInfo.forEach(singleSessionInfo => {
      const timeString = singleSessionInfo.session.startTime;
      const timeString12hr = new Date('1984-06-08T' + timeString + 'Z')
        .toLocaleTimeString('en-US',
          { timeZone: 'UTC', hour12: true, hour: 'numeric', minute: 'numeric' }
        );
      singleSessionInfo.session.startTime = timeString12hr;
    });
  }

  /* 
    bookSession reserves the slot for the memeber
    it validates if the capacity of the class is reaches. displays the appropriate messages
  */
  bookSession(singSessionInfo: any, tab: string) {
    console.log(" COMPONENT :: BOOK SESSION :: EMAIL =>", this.userEmail)

    this.scheduleSessionService.bookSession(singSessionInfo, this.userEmail)
      .subscribe((data: any) => {
        console.log("COMPONENT :: BOOK SESSION :: ALL BOOKED SESSIONS", data);
        singSessionInfo.booked = true;
        this.sessionInfo = data;

        if (this.sessionInfo != undefined) {
          this.convertDateTo12hFormat(this.sessionInfo);
        }

        if (tab == 'manage') 
        {
          this.loadUpcomingSessionData();
        }
      },
        (error: any) => {
        }
      );
  }
 
  /* 
    cancelSession reserves the slot for the memeber
    Member can cancel the session from upcoming session tab or manage session tab
  */
  cancelSession(singSessionInfo: any, tab: string) {
    console.log("COMPONENT :: CANCEK SESSION ");
    this.scheduleSessionService.cancelSession(singSessionInfo, this.userEmail).subscribe((data: any) => {
      console.log("COMPONENT :: CANCEK SESSION :: UPDATED SCHEDULE =>", data);
      singSessionInfo.booked = false;
      if (tab == 'upcoming') {
        this.loadManageData();
      } else if (tab == 'manage') {
        this.loadUpcomingSessionData();
      }

    });
  }

  /* 
    genHistoryReports does the necessary computation to analyze memeber usage of the gym session
    It gives the insights about total sessions attended and favourite session
  */
  genHistoryReports() {
    console.log("COMPONENT :; HISTORY REPORT");
    let sessionNumbers: number[] = Array();
    this.scheduleSessionService.genHistoryReports(this.userEmail).subscribe((data: any) => {
      this.hideLoginForm = true;
      this.showloginTemp = true;
      this.historyData = data;
      this.totalSessionConducted = this.historyData?.totalSessions;
      this.totalSessionAttended = this.historyData?.totalAttended;

      console.log("COMPONENT :; HISTORY REPORT :: DATA =>", this.historyData);
      this.historyData?.workoutHistory.forEach(element => {
        this.pieChartLabels.push(element.name);
        this.pieChartData.push(element.attended);
        sessionNumbers.push(element.attended);
      });

      console.log(" COMPONENT :; HISTORY REPORT :: FAV SESSION ==>", this.pieChartLabels[sessionNumbers.indexOf(Math.max(...sessionNumbers))]);
      this.favoriteSession = this.pieChartLabels[sessionNumbers.indexOf(Math.max(...sessionNumbers))].toString();
      
    });
  }

  /* Supporter function for UI display */
  getWeekdays() {
    var dayNames = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
    var today = new Date();
    var todayNum = today.getDay(); // 0 - SUNDAY , 6-Saturday
    var day: number;
    for (day = 0; day <= 6; day++) // today+0 = today , today +1 = tomorrow
    {
      this.weekDaysFrmTdy[day] = (dayNames[(todayNum + day) % 7]);
    }
  }

  /*
  resets the history data 
  */
  historyDataReset()
  {
    this.pieChartLabels =[];
    this.pieChartData =[];

  }
}




