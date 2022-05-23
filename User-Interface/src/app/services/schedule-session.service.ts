import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { schedule } from '../models/schedule';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
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

  registerNewMember(profile: FormGroup) 
  {
    console.log("INSIDE SERVICE ::registerNewMember ")


    const headers = { 'Content-Type': 'application/json' };
    const body = {  "firstName": profile.value['firstName'] ,
                    "lastName" : profile.value['lastName'],
                    "email" : profile.value['email'],
                    "password" : profile.value['password']
                  };
    console.log("Body ==>", body)
    return  this.http.post(apiendpoints.REGISTER_MEMBER_URL, body, {headers}).pipe(catchError(this.handleErrorSignUp));
  }

  registerNewTrainer(profile: FormGroup)
  {
    console.log("INSIDE SERVICE ::registerNewTrainer ")
    const headers = { 'Content-Type': 'application/json'  };
    const body = { firstName: profile.value['firstName'] ,
                    lastName : profile.value['lastName'],
                    email : profile.value['email'],
                    password : profile.value['password']
                  };
    console.log("Body ==>", body)
    return  this.http.post(apiendpoints.REGISTER_TRAINER_URL, body, {headers}).pipe(catchError(this.handleErrorSignUp));

  }
  
  authenticateAndloadUpcomingSessions(userEmail:string)
  {
      console.log("INSIDE SERVICE ::authenticateAndloadUpcomingSessions ")
      const params = new HttpParams().set('email', userEmail);
      const headers = { 'Content-Type': 'application/json' };
      return  this.http.get(apiendpoints.LOAD_UPCOMING_SESSIONS, {headers, params}).pipe(catchError(this.handleError));
  }


  authenticateAndloadProfileData(loginForm: FormGroup) 
  {
      console.log("INSIDE SERVICE ::authenticateAndloadProfileData ")
  
      const headers = { 'Content-Type': 'application/json'  };
      const body = { username: loginForm.value['email'] ,
                     password : loginForm.value['password']
                   };
      console.log("Body ==>", body)
      return  this.http.post(apiendpoints.AUTHENTICATE_AND_LOAD_URL, body,{headers}).pipe(catchError(this.handleError));
  }

  
  bookSession(singSessionInfo: any, userEmail:string) 
  {
    console.log("INSIDE SERVICE ::  Reserve Session")
    const body = JSON.stringify(singSessionInfo);
    console.log("Body ==>", body)
    const params = new HttpParams().set('email', userEmail);
    console.log(" Username param =>",userEmail )
    const headers = { 'Content-Type': 'application/json'};
    console.log("APU ==>",apiendpoints.RESERVE_SESSION + singSessionInfo.session.id )
    return  this.http.post(apiendpoints.RESERVE_SESSION+singSessionInfo.session.id, body, {headers,params}).pipe(catchError(this.handleError));

  }
  cancelSession(singSessionInfo: any, userEmail:string) 
  {
    console.log("INSIDE SERVICE ::  Cancel Reservation")

    const parameters = new HttpParams().set('email', userEmail);
    console.log(" Username param =>",userEmail )
    const headers = { 'Content-Type': 'application/json'};
    const options = {
      params : parameters
    }
    console.log(" OBJECT ********", singSessionInfo)
    console.log("API ==>",apiendpoints.CANCEL_SESSION + singSessionInfo.id );
    return  this.http.delete(apiendpoints.CANCEL_SESSION + singSessionInfo.id, options ).pipe(catchError(this.handleError));

  }

  handleError(error: HttpErrorResponse) 
  {
      console.log("INSIDE SERVICE ::handleError ")
      let errorMessage = 'Unknown error!';
      if (error.error instanceof ErrorEvent) {
        // Client-side errors
        errorMessage = `Error: ${error.error.message}`;
      } else {
        // Server-side errors
        errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
        if(error.status == 404)
        {
          errorMessage = "SERVICE DOWN :: API CONNECTIN FAILED:: 404";
        }
      }
      window.alert(errorMessage);
      return throwError(errorMessage);
  }
  handleErrorSignUp(error: HttpErrorResponse) 
  {
      console.log("INSIDE SERVICE ::handleErrorSignUp ")
      console.log ( ' code ==>', error.status);
      console.log ( ' response message ==>', error.message);
      console.log ( ' message ==>', error.error.message);
      console.log ( ' status text ==>', error.statusText);
      let errorMessage = 'Unknown error!';
      if (error.error instanceof ErrorEvent) {
        // Client-side errors
        errorMessage = `Error: ${error.error.message}`;
      } else {
        // Server-side errors
        errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
        if(error.status == 404)
        {
          errorMessage = "SERVICE DOWN :: API CONNECTIN FAILED:: 404";
        }else if (error.status == 400)
        {
          errorMessage = error.error.message;
        }
      }
      window.alert(errorMessage);
      return throwError(errorMessage);
  }
  
}






//   authenticateUser(loginForm: FormGroup) {
//     return this.http.get<boolean>('');
//   }
  


//   cancelSession(singSessionInfo: any) {
//     console.log("Inside service ::  Book Session")
//     console.log("JSON TO BE SENT", JSON.stringify(singSessionInfo))
//     fetch(apiendpoints.BOOK_SESSION, {
//       method: 'POST',
//       headers: { 'Content-Type': 'application/json' },
//       body: JSON.stringify(singSessionInfo),
//     })
//       .then((response) => response.json())
//       //Then with the data from the response in JSON...
//       .then((response) => {
//         console.log('Success:', response);
//       })
//       .catch((response) => {
//         console.error('Error:', response);
//       });
//   }

// }

// fetch(apiendpoints.REGISTER_MEMBER_URL, {
//   method: 'POST',
//   headers: { 'Content-Type': 'application/json'  },
//   body: JSON.stringify(profile),
// })
//   .then((response) => response.json())
  
//   .then((response) => {
//     console.log('Success:', response);
//   })
//   .catch((response) => {
//     console.error('Error:', response);
//   });
