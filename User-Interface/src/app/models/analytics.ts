export interface  graphData {
  "joinDate": string,
  "weeksSinceJoined": number,
  "totalSessions": number,
  "totalAttended": number,
  "workoutHistory": 
  [
    {
      "name": string,
      "total": number,
      "attended": number,
    }
    
  ]

}