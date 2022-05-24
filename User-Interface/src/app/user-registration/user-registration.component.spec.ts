import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserRegistrationComponent } from './user-registration.component';
import { ReactiveFormsModule } from '@angular/forms';

import { ScheduleSessionService } from '../services/schedule-session.service'

describe("UserRegistrationComponent", () => {
  let component: UserRegistrationComponent;
  let fixture: ComponentFixture<UserRegistrationComponent>;

  let authServiceSpy = jasmine.createSpyObj('ScheduleSessionService', ['registerUser']);
  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserRegistrationComponent],
      imports: [ReactiveFormsModule],
      providers: [
        {
          provide: ScheduleSessionService, useValue: authServiceSpy
        }
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('To test if the UserRegistrationComponent Launches', () => {
    expect(component).toBeTruthy();
  });

  it('To tets if all fields of registration form are filled', () => {
    component.profileForm.setValue({
      "firstName": "Bobby",
      "lastName": "Pal",
      "email": "bobby@bobby.com",
      "pwd": "poojapal@123",
      "type": "trainer"
    });
    expect(component.profileForm.valid).toEqual(true);
  });

  it('To tets if the fields of registration are empty', () => {
    component.profileForm.setValue({
      "firstName": "",
      "lastName": "",
      "email": "",
      "pwd": "",
      "type": ""
    });
    expect(component.profileForm.valid).toBe(false);
  });


  it('Mandatory Field(Email, Password) is entered', () => {
    let emailValue = component.profileForm.controls['email'];
    emailValue.setValue("abc@gmail.com")
    expect(emailValue.errors).toBeNull();
    let password = component.profileForm.controls['pwd'];
    password.setValue("okman4")
    expect(password.errors).toBeNull();

  });
  it('Email format checks (N)', () => {
    let emailVal = component.profileForm.controls['email'];
    emailVal.setValue("invalidEmail")
    expect(emailVal.valid).toBeFalsy();
    expect(emailVal.pristine).toBeTruthy();

  });
  it('Password format checks (N)', () => {
    let password = component.profileForm.controls['pwd'];
    password.setValue("")
    expect(password.valid).toBeFalsy();
    expect(password.pristine).toBeTruthy();

  });

  it('should allow user registration', () => {
    const formData = {
      "firstName": "Bobby",
      "lastName": "Pal",
      "email": "bobby@bobby.com",
      "pwd": "poojapal@123",
      "type": "trainer"
    };
    component.profileForm.setValue(formData);
    component.registerNewUser(component.profileForm);

    expect(authServiceSpy.registerUser).toHaveBeenCalledWith(formData);
  })

});
