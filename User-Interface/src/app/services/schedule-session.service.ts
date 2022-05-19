import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { schedule } from '../models/schedule';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
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


    const headers = { 'Content-Type': 'application/json', };
    const body = {  "username": 'testusername', // username is required
                    "firstName": profile.value['firstName'] ,
                    "lastName" : profile.value['lastName'],
                    "email" : profile.value['email'],
                    "password" : profile.value['password']
                  };
    console.log("Body ==>", body)
    return  this.http.post(apiendpoints.REGISTER_MEMBER_URL, body, {headers}).pipe(catchError(this.handleError));
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
    return  this.http.post(apiendpoints.REGISTER_TRAINER_URL, body, {headers}).pipe(catchError(this.handleError));

  }

  authenticateAndloadProfileData(loginForm: FormGroup) 
  {
      console.log("INSIDE SERVICE ::authenticateAndloadProfileData ")
  
      const headers = { 'Content-Type': 'application/json'  };
      const body = { username: loginForm.value['email'] ,
                     password : loginForm.value['password']
                   };
      console.log("Body ==>", body)
      return  this.http.post(apiendpoints.AUTHENTICATE_AND_LOAD_URL, body, {headers}).pipe(catchError(this.handleError));
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
          errorMessage = "API Connection Failed :: 404";
        }
      }
      window.alert(errorMessage);
      return throwError(errorMessage);
  }
  
}






//   authenticateUser(loginForm: FormGroup) {
//     return this.http.get<boolean>('');
//   }
  
//   bookSession(singSessionInfo: any) {
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
