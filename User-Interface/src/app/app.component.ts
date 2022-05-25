import { Component, OnInit } from '@angular/core';
import { ScheduleSessionService } from '../app/services/schedule-session.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

/* 
  Starter component of set-slot-sweat application. 
*/
export class AppComponent implements OnInit {

  title = 'set-slot-sweat'; //application title
  initialLoadStatus = true; //flag decided whether or not appComponent template is loadede

  constructor(private scheduleSessionService: ScheduleSessionService) 
  {

  }
  ngOnInit() 
  {
    this.scheduleSessionService.aClickedEvent
      .subscribe((data: string) => {
        console.log('AppComponent :: ngOnInit',  data);

        if (data == "login") 
        {
          this.initialLoadStatus = false;
        }
        else if (data == "logout") 
        {
          this.initialLoadStatus = true;
        }
      });
  }

}