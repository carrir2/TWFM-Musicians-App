import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { BackendService } from "../services/backend.service";
declare let $: any;

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  constructor(
    private formBuilder: FormBuilder,
    private backend: BackendService
  ) { }


  signupForm : FormGroup = new FormGroup('');
  ngOnInit(): void {
    this.signupForm = this.formBuilder.group({
      'contactName': new FormControl('', [
        Validators.required
      ]),
      'email': new FormControl('', [
        Validators.required
      ]),
      'password': new FormControl('', [
        Validators.required
      ])
    });
  }

  // Modal
  errors: string[] = [];
  closeModal() : void {
    $('#signupModal').modal("hide");
    this.errors = [];
  }

  signupAttempt() {
    if (!this.signupForm.valid) {
      this.errors.push("You must enter an email and password");
      // apply red border to all required inputs
      $('.ng-untouched').removeClass("ng-untouched").addClass("ng-touched");
      $('#signupModal').modal("show");
    } else {
      let formJSON: any = {};
      for (const field in this.signupForm.controls) {
        formJSON[field] = this.signupForm.controls[field].value;
      }
      this.backend.post("api/v1/registration/", formJSON).then( e => {
        console.log("posted:", e);
      }).catch( e => {
        console.log("error posting form", e);
      });
    }
  }
}
