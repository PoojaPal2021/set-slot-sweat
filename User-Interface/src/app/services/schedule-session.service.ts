import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { schedule } from '../models/schedule';
import { HttpClient } from '@angular/common/http';
import { throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { apiendpoints } from '../../app/api-endpoints'
@Injectable({
  providedIn: 'root'
})
export class ScheduleSessionService {
  configUrl = 'assets/config.json';
  constructor(private http: HttpClient) { }

  loadProfileData() {
    return this.http.get<schedule>(this.configUrl);
  }
  authenticateUser(loginForm: FormGroup) {
    return this.http.get<boolean>('');
  }

  registerUser(profile: FormGroup) {
    console.log("Inside register user with form")

    fetch(apiendpoints.REGISTER_USER_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(profile),
    })
      .then((response) => response.json())
      //Then with the data from the response in JSON...
      .then((response) => {
        console.log('Success:', response);
      })
      .catch((response) => {
        console.error('Error:', response);
      });


  }
  bookSession(singSessionInfo: any) {
    console.log("Inside service ::  Book Session")
    console.log("JSON TO BE SENT", JSON.stringify(singSessionInfo))
    fetch(apiendpoints.BOOK_SESSION, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(singSessionInfo),
    })
      .then((response) => response.json())
      //Then with the data from the response in JSON...
      .then((response) => {
        console.log('Success:', response);
      })
      .catch((response) => {
        console.error('Error:', response);
      });
  }

  cancelSession(singSessionInfo: any) {
    console.log("Inside service ::  Book Session")
    console.log("JSON TO BE SENT", JSON.stringify(singSessionInfo))
    fetch(apiendpoints.BOOK_SESSION, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(singSessionInfo),
    })
      .then((response) => response.json())
      //Then with the data from the response in JSON...
      .then((response) => {
        console.log('Success:', response);
      })
      .catch((response) => {
        console.error('Error:', response);
      });
  }

}


