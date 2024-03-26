import { TestBed } from '@angular/core/testing';

import { MarketDaysService } from './market-days.service';

describe('MarketDaysService', () => {
  let service: MarketDaysService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MarketDaysService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
