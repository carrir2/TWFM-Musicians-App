import { Component, OnInit, Input, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'app-artist',
  templateUrl: './artist.component.html',
  styleUrls: ['./artist.component.css']
})
export class ArtistComponent implements OnInit {

  constructor() { }

  ngOnInit(): void { }

  @Input() allInfo: any = {};
  @Input() name: string = 'Artist';
  @Input() description: string = 'Artist description...';
  @Input() links: string[] = [''];
  @Input() totalPerformances: number = 0;
  @Input() totalAvailability: number = 0;
  @Input() totalSelections: number = 0;
  @Input() select: string = 'Select';
  @Input() slotid: number = -1;
  @Input() userid: number = -1; 
  @Input() status: string = ""; 

  info() {
    let keys: string[] = ["email", "contactName", "lastMinute", "phoneNumber", "paymentHandle", "comments"];
    let string: string = "";
    keys.forEach( (key: string) => {
      string += key + ": " + this.allInfo[key] + "\n";
    });
    alert(string);
  }

  @Output() email = new EventEmitter<any>();
  notify() {
    // api/v1/registration/schedule/invite/{slotid}/{userid}
    this.email.emit([this.slotid, this.userid])
  }

  @Output() deselected = new EventEmitter<any>();
  @Output() selected = new EventEmitter<any>();
  toggleSelect() {
    if (this.select == "Select") {
      if (confirm("Are you sure you would like to select " + this.name + "?") == true) {
        this.selected.emit([this.userid, this.slotid]);
      }
    } else {
      if (confirm("Are you sure you would like to deselect " + this.name + "?") == true) {
        this.deselected.emit([this.userid, this.slotid]);
      }
      
    }
  }
}
