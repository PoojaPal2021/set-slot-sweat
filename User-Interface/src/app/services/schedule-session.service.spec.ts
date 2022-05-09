import { TestBed } from '@angular/core/testing';


import { ScheduleSessionService } from './schedule-session.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
describe('ScheduleSessionService', () => {
  let httpTestingController: HttpTestingController;
  let service: ScheduleSessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    httpTestingController = TestBed.get(HttpClientTestingModule);
    service = TestBed.inject(ScheduleSessionService);
  });


  it('checks if the user defined service is UP', () => {
    expect(service).toBeTruthy();
  });

  //need to integrate with the backend services
  // it('loadProfileData should use GET to retrieve data', () => {

  //   service.loadProfileData().subscribe();

  //   const testRequest = httpTestingController.expectOne('http://localhost:9999/data');

  //   expect(testRequest.request.method).toEqual('GET');
  // });

});
