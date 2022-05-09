import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserLoginComponent } from './user-login.component';
import { ScheduleSessionService } from '../services/schedule-session.service'
import { NotFoundError } from 'rxjs';

describe('UserLoginComponent', () => {
  let component: UserLoginComponent;
  let fixture: ComponentFixture<UserLoginComponent>;

  let loginServiceSpy = jasmine.createSpyObj('ScheduleSessionService', ['loadProfileData','authenticateUser','bookSession','cancelSession']);
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserLoginComponent],
      providers: [
        {
          provide: ScheduleSessionService, useValue: loginServiceSpy
        }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('Should create Login Componnet', () => {
    expect(component).toBeTruthy();
  });

  it('Checks if login infomation has entered', () => {
    let emailValue = component.loginForm.controls['email'];
    emailValue.setValue("abc@gmail.com")
    expect(emailValue.errors).toBeNull();
    let password = component.loginForm.controls['pwd'];
    password.setValue("okman4")
    expect(password.errors).toBeNull();
  });

  it('Checks if login infomation is valid', () => {
    let emailVal = component.loginForm.controls['email'];
    emailVal.setValue("invalid")
    expect(emailVal.valid).toBeFalsy();
    expect(emailVal.pristine).toBeTruthy();

    let password = component.loginForm.controls['pwd'];
    password.setValue("99")
    expect(emailVal.valid).toBeFalsy();
    expect(emailVal.pristine).toBeTruthy();
  });

  it('check default authentication status before authentication', () => {
    expect(component.authenticatedUser).toBe(false)
  })

  it('should autheticate the user', () => {
    const loginData = {
      "email": "bobby@bobby.com",
      "pwd": "poojapal@123"
    };
    component.loginForm.setValue(loginData);
    component.loadProfile()
    expect(loginServiceSpy.authenticateUser).toHaveBeenCalledWith(component.loginForm);
  });

  // it('should load profile for authenticatedUser', () => {
  //   component.loadProfile()
  //   expect(loginServiceSpy.loadProfileData).toBeUndefined()
  // });

  it('should BOOK session for the User', () => {
    const sessionData = {
    };
    component.bookSession(sessionData);
    expect(loginServiceSpy.bookSession).toHaveBeenCalledWith(sessionData);
  });

  it('should CANCEL session for the User', () => {
    const sessionData = {
    };
    component.cancelSession(sessionData);
    expect(loginServiceSpy.cancelSession).toHaveBeenCalledWith(sessionData);
  });

});
