import { Component, OnInit } from '@angular/core';
import { ScheduleSessionService } from '../app/services/schedule-session.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
 
  // ngOnInit(): void {
  //   throw new Error('Method not implemented.');
  // }
  title = 'set-slot-sweat';
  status :boolean =false;
  initialLoadStatus= true;


  routerOutletComponent :any =Object ;
  routerOutletComponentClassName:  string="";

constructor(private scheduleSessionService: ScheduleSessionService)
{

}
  ngOnInit() {

    this.scheduleSessionService.aClickedEvent
    .subscribe((data:string) => {
      console.log('Event message from Login Component: ================>' + data);

      if (data == "login")
      {
        console.log(' INSIDE LOGIN   =======>>>>>>>>' + data);
        this.initialLoadStatus = false;
      }
      else if (data == "logout")
      {
        console.log(' INSIDE LOGOUT =====>>>>>>>>' + data);
        this.initialLoadStatus = true;
      }
    });
  }

  
  componentAdded(event : any) {
    
    
  }
  countChangedHandler(event:any) {
  
  }
}