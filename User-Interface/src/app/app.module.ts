import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { UserRegistrationComponent } from './user-registration/user-registration.component';
import { UserLoginComponent } from './user-login/user-login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { ScheduleSessionService } from './services/schedule-session.service';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs'
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatMenuModule } from '@angular/material/menu';
import { ChartsModule } from 'ng2-charts';
import { MatDialogModule } from '@angular/material/dialog';

/*

  An NgModule is a class marked by the @NgModule decorator.
  @NgModule takes a metadata object that describes how to compile a component's template and how to create an injector at runtime. 
  It identifies the module's own components, directives, and pipes, making some of them public, through the exports property, so that external components can use them. 
  @NgModule can also add service providers to the application dependency injectors.

*/
@NgModule({
  declarations: [
    AppComponent,
    UserRegistrationComponent,
    UserLoginComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    BrowserAnimationsModule,
    MatMenuModule,
    ChartsModule,
    MatDialogModule,
    RouterModule.forRoot([
      { path: 'logout', component: AppComponent },
      { path: 'register', component: UserRegistrationComponent },
      { path: 'login', component: UserLoginComponent }
    ])
  ],
  providers: [ScheduleSessionService],
  bootstrap: [AppComponent]
})
export class AppModule { }
