import { Component, OnInit } from '@angular/core';

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
  initialLoadStatus= false;
  ngOnInit(): void {
    // this.initialLoadStatus = !this.initialLoadStatus;
  }
  onActivate(event : any) {
    
  }
  countChangedHandler(event:any) {
  
    console.log(" event value ", event);
    this.status = event;
  }
}