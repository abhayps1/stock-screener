import { TestBed } from '@angular/core/testing';

import { UpcomingIpoService } from './ipo.service';

describe('UpcomingIpoService', () => {
  let service: UpcomingIpoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UpcomingIpoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
