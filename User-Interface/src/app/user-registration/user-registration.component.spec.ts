import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserRegistrationComponent } from './user-registration.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ScheduleSessionService } from '../services/schedule-session.service'
/*
  .spec files are test files
  contains the unit tests for user registration component 
*/
describe("UserRegistrationComponent", () => {
  let component: UserRegistrationComponent;
  let fixture: ComponentFixture<UserRegistrationComponent>;
  /* mocked dependency injections */
  let authServiceSpy = jasmine.createSpyObj('ScheduleSessionService', ['registerNewMember', 'registerNewTrainer', 'AClicked']);

  /* making the set up to access the component methods */
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

  /* executed once before each test runs */
  beforeEach(() => {
    fixture = TestBed.createComponent(UserRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /* Each "it" statemenets define a specific test */
  it('To test if the UserRegistrationComponent Launches', () => {
    expect(component).toBeTruthy();
  });

  it('To tests if all fields of registration form are filled', () => {
    component.profileForm.setValue({
      "firstName": "Bobby",
      "lastName": "Pal",
      "email": "bobby@bobby.com",
      "password": "poojapal@123",
      "type": "memebr"

    });
    expect(component.profileForm.valid).toEqual(true);
  });

  it('To tests if the fields of registration are empty', () => {
    component.profileForm.setValue({
      "firstName": "",
      "lastName": "",
      "email": "",
      "password": "",
      "type": ""
    });
    expect(component.profileForm.valid).toBe(false);
  });


  it('Mandatory Field(Email, Password) is entered', () => {
    let emailValue = component.profileForm.controls['email'];
    emailValue.setValue("abc@gmail.com")
    expect(emailValue.errors).toBeNull();
    let password = component.profileForm.controls['password'];
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
    let password = component.profileForm.controls['password'];
    password.setValue("")
    expect(password.valid).toBeFalsy();
    expect(password.pristine).toBeTruthy();

  });

  // it('should allow user registration', () => {
  //   const formData = {
  //     "firstName": "Bobby",
  //     "lastName": "Pal",
  //     "email": "bobby@bobby.com",
  //     "password": "poojapal@123",
  //     "type": "trainer"
  //   };
  //   component.profileForm.setValue(formData);
  //   expect(component.registerNewUser(component.profileForm)).toBeDefined;

  //   // expect(authServiceSpy.registerNewMember).toBeDefined;
  // })

});
