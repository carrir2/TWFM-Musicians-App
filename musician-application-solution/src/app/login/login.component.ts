import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { BackendService } from "../services/backend.service";
declare let $: any;
declare let google: any;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  constructor(
    private formBuilder: FormBuilder,
    private backend: BackendService
  ) { }

  loginForm : FormGroup = new FormGroup('');
  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      'email': new FormControl('', [
        Validators.required
      ]),
      'password': new FormControl('', [
        Validators.required
      ])
    });
    google.accounts.id.initialize({
      client_id: "755256089864-hf89vbc6uee31ig7rhk3j0bmlpahbu60",
      callback: this.googleLogin
    });
    google.accounts.id.renderButton(
      document.getElementById("googleLogIn"),
      { theme: "outline", size: "large" }  // customization attributes
    );
  }

  // Modal
  errors: string[] = [];
  closeModal() : void {
    $('#loginModal').modal("hide");
    this.errors = [];
  }


  // backend object returned on successful login:
  // { 
  //   "error": String, 
  //   "id": String, 
  //   "email": String, 
  //   "contactName": String, 
  //   "token": String, 
  //   "appUserRole": String, 
  //   "locked": boolean, 
  //   "enabled": String
  //   }
  
  loginAttempt() {
    if (!this.loginForm.valid) {
      this.errors.push("You must enter an email and password");
      // apply red border to all required inputs
      $('.ng-untouched').removeClass("ng-untouched").addClass("ng-touched");
      $('#loginModal').modal("show");
    } else {
      
      let formJSON: any = {};
      for (const field in this.loginForm.controls) {
        formJSON[field] = this.loginForm.controls[field].value;
      }

      this.backend.post("api/v1/registration/login", formJSON).then( (e:any) => {
        this.backend.login(e)
        if (e["error"] == "none") { // active
          if (e["appUserRole"] == "USER") {
            // go to application page
            this.navEmitter.emit("Apply");
          } else if (e["appUserRole"] == "ADMIN") {
            // go to callendar page
            this.navEmitter.emit("Calendar");
          }
        } else { // TODO: there are definitely multiple error cases

        }

      }).catch( e => {
        console.log(formJSON, "error posting form", e);
      });
    }
  }

  googleLogin = (response: any) => {
    this.backend.post("api/v1/registration/loginGoogle", response).then( (e:any) => {
      this.backend.login(e)
      if (e["error"] == "none") { // active
        if (e["appUserRole"] == "USER") {
          // go to application page
          this.navEmitter.emit("Apply");
        } else if (e["appUserRole"] == "ADMIN") {
          // go to callendar page
          this.navEmitter.emit("Calendar");
        }
      }
    }).catch( (e: any) => {
      console.log(response, "error posting login", e);
    });
  }

  @Output() navEmitter = new EventEmitter();
  signup() {
    this.navEmitter.emit("Signup");
  }
}
