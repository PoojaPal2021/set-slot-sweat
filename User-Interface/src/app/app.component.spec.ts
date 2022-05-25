import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { ScheduleSessionService } from './services/schedule-session.service'
fdescribe('AppComponent', () => {
  // beforeEach(async () => {
  //   await TestBed.configureTestingModule({
  //     imports: [
  //       RouterTestingModule
  //     ],
  //     declarations: [
  //       AppComponent
  //     ],
  //   }).compileComponents();
  let authServiceSpy = jasmine.createSpyObj('ScheduleSessionService', ['loadProfileData','authenticateUser','bookSession','cancelSession','AClicked']);
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [AppComponent],
        providers: [
          {
            provide: ScheduleSessionService, useValue: authServiceSpy
          }
        ]
      })
        .compileComponents();
    });
    
  

  it('To test if the Application Launches', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`To test  if the title is 'set-slot-sweat'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('set-slot-sweat');
  });

  it('To test initial state of the app component', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.initialLoadStatus).toBe(true);

    // const fixture = TestBed.createComponent(AppComponent);
    // fixture.detectChanges();
    // const compiled = fixture.nativeElement as HTMLElement;
    // const span = fixture.nativeElement.querySelector('span');
    // expect(span.innerHTML).toContain('New visitor?')
    // expect(span.innerHTML).toContain('Already a Gym member?')
  });

});


