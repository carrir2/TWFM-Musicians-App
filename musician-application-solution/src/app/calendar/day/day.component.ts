import { Component, OnInit, Input, Output, EventEmitter, SimpleChanges, OnChanges } from '@angular/core';
import { BackendService } from '../../services/backend.service';

@Component({
  selector: 'app-day',
  templateUrl: './day.component.html',
  styleUrls: ['./day.component.css']
})
export class DayComponent implements OnInit {

  constructor(
    private backend: BackendService
  ) { }

  @Input() date: Date = new Date();
  @Input() update: number = 0;
  @Output() updateParent = new EventEmitter<any>();
  ngOnChanges(changes: SimpleChanges) {
    if (changes['update'].previousValue == 0 && changes['update'].currentValue == 1) {
      this.viewDay();
      this.updateParent.emit();
    }
  }

  ngOnInit(): void {
  }

  writeDate(): string {
    let month = String(this.date.getMonth() + 1).padStart(2, '0');
    let day = String(this.date.getDate()).padStart(2, '0');
    let year = this.date.getUTCFullYear();
    return `${year}${month}${day}`;
  }


  @Output() scheduleSlots = new EventEmitter<any>();
  // @Output() queueItems = new EventEmitter<any>();
  viewDay(): void {
    this.backend.get(`api/v1/registration/schedule/${this.writeDate()}`).then( e => {
      // console.log(e);
      this.scheduleSlots.emit([e, this.writeDate()]);
    }).catch( e => {
      alert("Something went wrong fetching the queue");
    });   
    
    // this.backend.get(`api/v1/registration/musicianqueue/${this.writeDate()}`).then( e => {
    //   // console.log(e);
    //   this.queueItems.emit(e);
    // }).catch( e => {
    //   alert("Something went wrong fetching the queue");
    // }); 
  }
}
