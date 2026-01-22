import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TrackingRecordDetailComponent } from './tracking-record-detail.component';

describe('TrackingRecord Management Detail Component', () => {
  let comp: TrackingRecordDetailComponent;
  let fixture: ComponentFixture<TrackingRecordDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackingRecordDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./tracking-record-detail.component').then(m => m.TrackingRecordDetailComponent),
              resolve: { trackingRecord: () => of({ id: 14377 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TrackingRecordDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TrackingRecordDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load trackingRecord on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TrackingRecordDetailComponent);

      // THEN
      expect(instance.trackingRecord()).toEqual(expect.objectContaining({ id: 14377 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
