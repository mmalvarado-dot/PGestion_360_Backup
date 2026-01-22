import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DataUtils } from 'app/core/util/data-util.service';

import { ChangeRequestDetailComponent } from './change-request-detail.component';

describe('ChangeRequest Management Detail Component', () => {
  let comp: ChangeRequestDetailComponent;
  let fixture: ComponentFixture<ChangeRequestDetailComponent>;
  let dataUtils: DataUtils;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangeRequestDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./change-request-detail.component').then(m => m.ChangeRequestDetailComponent),
              resolve: { changeRequest: () => of({ id: 26371 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ChangeRequestDetailComponent, '')
      .compileComponents();
    dataUtils = TestBed.inject(DataUtils);
    jest.spyOn(window, 'open').mockImplementation(() => null);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeRequestDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load changeRequest on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ChangeRequestDetailComponent);

      // THEN
      expect(instance.changeRequest()).toEqual(expect.objectContaining({ id: 26371 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });

  describe('byteSize', () => {
    it('should call byteSize from DataUtils', () => {
      // GIVEN
      jest.spyOn(dataUtils, 'byteSize');
      const fakeBase64 = 'fake base64';

      // WHEN
      comp.byteSize(fakeBase64);

      // THEN
      expect(dataUtils.byteSize).toHaveBeenCalledWith(fakeBase64);
    });
  });

  describe('openFile', () => {
    it('should call openFile from DataUtils', () => {
      const newWindow = { ...window };
      window.open = jest.fn(() => newWindow);
      window.onload = jest.fn(() => newWindow) as any;
      window.URL.createObjectURL = jest.fn() as any;
      // GIVEN
      jest.spyOn(dataUtils, 'openFile');
      const fakeContentType = 'fake content type';
      const fakeBase64 = 'fake base64';

      // WHEN
      comp.openFile(fakeBase64, fakeContentType);

      // THEN
      expect(dataUtils.openFile).toHaveBeenCalledWith(fakeBase64, fakeContentType);
    });
  });
});
