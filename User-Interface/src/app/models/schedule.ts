export interface schedule {

  "id": number,
  "bookTime": string,
  "scheduledTime": null,
  "cancelTime": null,
  "session": 
  {
        "id": number,
        "workout":
        {
          "id": number,
          "name": string,
          "description": string,
        },
        "trainer":
        {
          "id": number,
          "username": string,
          "email": string,
          "password":string,
          "firstName": string,
          "lastName": string,
          "gender": string,
          "joinDate": string,
          "bio": string
        }
       "startTime": string,
      "dayOfWeek": "MONDAY",
      "capacity": 25
      }
    "memberEmail": null,
    "booked": boolean
}
