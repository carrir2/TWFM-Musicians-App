import { Component, OnInit, ViewChild } from '@angular/core';
import { MarketDaysService } from './../services/market-days.service';
import { BackendService } from './../services/backend.service';
declare let $: any;

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {

  constructor(
    private days: MarketDaysService,
    private backend: BackendService
  ) { }

  dates: Date[][] = [];
  update: number[][] = []; // an array of 0's corresponding to a date that should be updated in the child
  ngOnInit(): void {
    this.dates = this.days.createDates();
    this.negateUpdate();
  }

  negateUpdate(): void {
    this.update = [];
    this.dates.forEach((month: Date[]) => {
      let m: any[] = [];
      month.forEach((day: Date) => {
        m.push(0);
      });
      this.update.push(m);
    });
  }
  
  selectedDay: number = -1;
  viewDay(id: number): void {
    $(".selected-day").removeClass("selected-day");
    $(`#day-${id}`).addClass("selected-day");
    this.selectedDay = id;
  }

  schedule: any[] = [];
  morningQueue: any[] = [];
  afternoonQueue: any[] = [];
  // [{"time":"AM","date":"2022-11-19","status":"unfilled","bandName":null,"userid":null,"stage":"Stage A","slotid":1}]
  recieveSchedule(schedule: any[], date: string) {
    this.schedule = schedule;
    console.log(schedule);
    this.schedule.forEach( (e:any) => {
      this.backend.get(`api/v1/registration/musicianqueue/${date}/morning`).then((e: any) => {
        this.morningQueue = e;
      });
      this.backend.get(`api/v1/registration/musicianqueue/${date}/afternoon`).then((e: any) => {
        this.afternoonQueue = e;
      });
    });
  }

  updateChild(): void {
    let dayNum: number = 0;
    let i: number = 0;
    let j: number = 0;
    this.dates.forEach((arr: Date[]) => {
      j=0;
      arr.forEach((d: Date) => {
        ++dayNum;
        if (dayNum == this.selectedDay) {
          this.update[i][j] = 1;
        }
        ++j
      });
      ++i;
    });
  }

  select(userid: number, slotid: number) {
    // make any necessary backened calls for an artist selection
    this.backend.post(`api/v1/registration/schedule/select/${slotid}/${userid}`, {}).then( e => {
      this.updateChild();
    }).catch( e => {
      alert(JSON.stringify(e));
    }); 
  }

  deselect(userid: number, slotid: number) {
    // make any necessary backened calls for an artist deselection
    this.backend.post(`api/v1/registration/schedule/unselect/${slotid}/${userid}`, {}).then( e => {
      this.updateChild();
    }).catch( e => {
      alert(JSON.stringify(e));
    });   
  }

  emailArtist(slotid: any, userid: any) {
    // api/v1/registration/schedule/invite/{slotid}/{userid}
    this.backend.post(`api/v1/registration/schedule/invite/${slotid}/${userid}`, {}).then( e => {
      console.log(`api/v1/registration/schedule/invite/${slotid}/${userid}`, e);
      this.updateChild();
    });
  }
}
