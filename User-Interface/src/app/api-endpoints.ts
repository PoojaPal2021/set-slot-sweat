export class apiendpoints 
{
    static REGISTER_MEMBER_URL = "http://localhost:18001/user-management/api/v1/member/signup";
    static REGISTER_TRAINER_URL = "";
    static AUTHENTICATE_AND_LOAD_URL = "http://localhost:18000/reservation-service/api/v1/member/login";
    static RESERVE_SESSION = "http://localhost:18000/reservation-service/api/v1/session/book/";
    static CANCEL_SESSION = "http://localhost:18000/reservation-service/api/v1/session/cancel/";
    static LOAD_UPCOMING_SESSIONS = "http://localhost:18000/reservation-service/api/v1/reservations/booked"; 
    static GET_SESSION_HISTORY = "http://localhost:18000/reservation-service/api/v1/reservations/history";
}
//http://localhost:18000/reservation-service/api/v1/session/book/50?email=jardiamj@gymapp.com