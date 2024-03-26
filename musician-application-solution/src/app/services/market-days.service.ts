import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MarketDaysService {

  constructor() { }

  daysInMonth(month: number, year: number) {
    return new Date(year, month, 0).getDate();
  }

  saturdaysInMonth(d: Date): Date[] {
    let sat = new Array();
    let getTot = this.daysInMonth(d.getMonth(),d.getFullYear()); //Get total days in a month
    for(let i=1; i<=getTot; i++){ // days in month
      let newDate = new Date(d.getFullYear(),d.getMonth(),i)
      if (newDate.getDay() == 6) {
        // Saturday
        sat.push(newDate);
      }
    }
    return sat;
  }

  createDates(): Date[][] {
    let dates: Date[][] = []; 
    let d: Date = new Date();
    let month = d.getMonth();

    // determine which season of dates should be available
    if (3 >= month && month <= 8) { 
      // season:    May   <= today <= October
      // available: April <= today <= September
      // Outdoors
      d.setMonth(4) // May
    } else {
      // season:    November <= today <= April
      // available: October <= today <= March
      // Indoors
      d.setMonth(10) // November
    }
    let offset = 0;
    for (let m = 0; m < 6; ++m) {
      if (month + m > 11 && offset == 0) {
        // wrap around to next year
        d.setFullYear(d.getFullYear() + 1);
        offset = -12;
      }
      d.setMonth(month + m + offset);
      dates.push(this.saturdaysInMonth(d));
    }
    return dates;
  }
}
