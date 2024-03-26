import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators, FormArray} from '@angular/forms';
import { BackendService } from "../services/backend.service";
import { MarketDaysService } from './../services/market-days.service';
declare let $: any;

@Component({
  selector: 'app-musician-application',
  templateUrl: './musician-application.component.html',
  styleUrls: ['./musician-application.component.css']
})
export class MusicianApplicationComponent implements OnInit {
  applicationForm : FormGroup = new FormGroup('');

  constructor(
    private formBuilder: FormBuilder,
    private backend: BackendService,
    private days: MarketDaysService,
  ) { }  
  
  get availabilitiesFormArray() {
    return this.applicationForm.controls['availabilities'] as FormArray;
  }

  extArray: FormControl<FormControl<string | null>[] | null> = new FormControl([new FormControl('')]);
  ngOnInit(): void {
    let dates: Date[][] = this.days.createDates();
    dates.forEach( (e: Date[]) => {
      e.forEach( (date: Date) => {
        this.dates.push(date);
      });
    });

    this.applicationForm = this.formBuilder.group({
      'email': new FormControl('', [
        Validators.required
      ]),
      'contactName': new FormControl('', [
        Validators.required
      ]),  
      'bandName': new FormControl('', [
        Validators.required
      ]),
      'setup': new FormControl('', [
        Validators.required
      ]),
      'lastMinute': new FormControl('', [
        Validators.required
      ]),
      'phoneNumber': new FormControl(''),
      'paymentHandle': new FormControl('', [
        Validators.required
      ]),
      'gigLength': new FormControl('', [
        Validators.required
      ]),
      'gigTime': new FormControl('', [
        Validators.required
      ]),
      'availabilities':  new FormArray([]),
      'comments': new FormControl(''),
      'externalLinks' : this.extArray,
      'MA1': new FormControl('', [
        Validators.required
      ]),
      'MA2': new FormControl('', [
        Validators.required
      ]),
      'MA3': new FormControl('', [
        Validators.required
      ]),
      'MA4': new FormControl('', [
        Validators.required
      ]),
      'MA5': new FormControl('', [
        Validators.required
      ]),
      'MA6': new FormControl('', [
        Validators.required
      ])
    });
    this.applyDates();
    this.fillPrevious();    
  }

  previous: Boolean = false;
  fillPrevious() {
    // try to fill form with previous response
    this.backend.post("api/v1/registration/musician/", {}).then( (e:any) => {
      console.log(e);
      // check if user has submitted a previous application
      if (e != undefined || Object.keys(e).length != 17) {
        this.previous = true;
        this.applicationForm.get("email")?.setValue(e['email']);
        this.applicationForm.get("bandName")?.setValue(e['bandName']);
        this.applicationForm.get("setup")?.setValue(e['setup']);
        this.applicationForm.get("lastMinute")?.setValue(e['lastMinute']);
        this.applicationForm.get("phoneNumber")?.setValue(e['phoneNumber']);
        this.applicationForm.get("paymentHandle")?.setValue(e['paymentHandle']);
        this.applicationForm.get("gigLength")?.setValue(e['gigLength']);
        this.applicationForm.get("gigTime")?.setValue(e['gigTime']);
        this.applicationForm.get("comments")?.setValue(e['comments']);
        this.applicationForm.get("contactName")?.setValue(e['contactName']);

        // agreements
        this.MA.forEach( (e:string) => {
          this.applicationForm.get(e)?.setValue("yes");
        });

        for (let i=0; i< e['externalLinks'].length; ++i) {
          this.addExt(e['externalLinks'][i]);
        }
        this.removeExt(0);
        let c=0;
        for (let control of this.availabilitiesFormArray['controls']) {
          if (e['availabilities'].includes(this.writeDate(this.dates[c]) )) {
            control.setValue(true);
          }
          ++c;
        }
      } else {
        this.previous = false;
      }
    });
  }

  writeDate(d:Date): string {
    let month = String(d.getMonth() + 1).padStart(2, '0');
    let day = String(d.getDate()).padStart(2, '0');
    let year = d.getUTCFullYear();
    return `${year}-${month}-${day}`;
  }

  applyDates(): void {
    this.dates.forEach( (d: Date) => {
      this.availabilitiesFormArray.push(new FormControl(false));
    });
  }

  addExt(str:string = "") {
    this.extArray.value?.push(new FormControl(str)); 
  }
  removeExt(index: number) {
    this.extArray.value?.splice(index, 1);
  }

  // Modal
  modalTitle: String = '';
  errors: string[] = [];
  closeModal() : void {
    $('#myModal').modal("hide");
    this.errors = [];
    this.modalTitle = '';
  }  

  // Musician Agreements
  MA: string[] = ['MA1', 'MA2', 'MA3', 'MA4', 'MA5', 'MA6'];
  agreementsGood(): boolean {
    return !this.MA.some((e) => {
      if (this.applicationForm.get(e)?.value != 'yes') {
        return true;
      }
      return false;
    });
  }

  hasDuplicates(array: any[]) {
    var valuesSoFar = [];
    for (var i = 0; i < array.length; ++i) {
        var value = array[i];
        if (valuesSoFar.indexOf(value) !== -1) {
          return true;
        }
        valuesSoFar.push(value);
    }
    return false;
  }

  async formCompletion(): Promise<void> {
    if (!this.applicationForm.valid) {
      // not all required fields were filled out
      this.errors.push("Please fill out any required fields");
      // apply red border to all required inputs
      $('.ng-untouched').removeClass("ng-untouched").addClass("ng-touched");
      // apply to non-standard inputs (multiple select)
      let reqMultipleChoice: string[] = ["lastMinute", "gigLength", "gigTime"];
      reqMultipleChoice.forEach((e: string) => {
        $('.' + e).removeClass("red-border"); // remove previous invalid borders
        if (!this.applicationForm.get(e)?.valid) {
          $('.' + e).addClass("red-border");
        }
      });
    }
    if (!this.agreementsGood()) {
      // didn't accept all agreements
      this.errors.push("You must select yes for all musician agreements to be considered");
    } 
    if (this.applicationForm.get('lastMinute')?.value == "yes" && !(this.applicationForm.get('phoneNumber')?.value)) {
      // if musician selects yes to being contacted last minute
      // they must provide a phone number to do so
      this.errors.push("You must provide a phone number (or select no to being contacted last minute)");
    } 
    // gather and clean up form data
    let formJSON: any = {};
    for (const field in this.applicationForm.controls) {
      if (!this.MA.includes(field)) formJSON[field] = this.applicationForm.controls[field].value;
    }
    const selectedOrderIds: String[] = this.applicationForm.value.availabilities.map((checked: any, i: number) => checked ? this.dates[i].toLocaleDateString('en-US') : null).filter((v: any )=> v !== null);
    formJSON.availabilities = selectedOrderIds;

    let res: String[] = [];
    for (let i=0; i<formJSON.externalLinks!.length; ++i) {
      let val: String = formJSON.externalLinks![i].value;
      res.push(val);
    }
    formJSON.externalLinks = res;

    // more error checking with clean values
    if (selectedOrderIds.length == 0) {
      // User did not select any availabilities
      this.errors.push("You have not selected any date availabilities");
    }
    if (this.hasDuplicates(res)) {
      // duplicate external links selected
      this.errors.push("You have submitted the same external link twice");
    }

    if (this.errors.length) {
      this.modalTitle = "Form Incomplete";
      $('#myModal').modal("show");
    } else { 
      // all good
      console.log(formJSON);
      this.backend.post("api/v1/registration/form", formJSON).then( e => {
        // successful submission
        if (this.previous) {
          alert("You have successfully saved your application");
        } else {
          alert("You have successfully submitted your application");
        }
      }).catch( e => {
        alert("Something went wrong submitting your form, please contact us for assistance");
      });
    }
  }  

  dateOptions = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
  dates: Date[] = [];

  showDropdown: boolean = true;
  showCheckboxes() {
    let checkboxes: HTMLElement = document.getElementById("checkBoxes") as HTMLElement
    if (this.showDropdown) {
      checkboxes.style.display = "block";
      this.showDropdown = false;
  } else {
      checkboxes.style.display = "none";
      this.showDropdown = true;
  }
  }
}
