import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IChangeRequest } from 'app/entities/change-request/change-request.model';
import { ChangeRequestService } from 'app/entities/change-request/service/change-request.service';
import { FileRecordService } from '../service/file-record.service';
import { IFileRecord } from '../file-record.model';
import { FileRecordFormService } from './file-record-form.service';

import { FileRecordUpdateComponent } from './file-record-update.component';

describe('FileRecord Management Update Component', () => {
  let comp: FileRecordUpdateComponent;
  let fixture: ComponentFixture<FileRecordUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fileRecordFormService: FileRecordFormService;
  let fileRecordService: FileRecordService;
  let changeRequestService: ChangeRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FileRecordUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FileRecordUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FileRecordUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fileRecordFormService = TestBed.inject(FileRecordFormService);
    fileRecordService = TestBed.inject(FileRecordService);
    changeRequestService = TestBed.inject(ChangeRequestService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call ChangeRequest query and add missing value', () => {
      const fileRecord: IFileRecord = { id: 25181 };
      const changeRequest: IChangeRequest = { id: 26371 };
      fileRecord.changeRequest = changeRequest;

      const changeRequestCollection: IChangeRequest[] = [{ id: 26371 }];
      jest.spyOn(changeRequestService, 'query').mockReturnValue(of(new HttpResponse({ body: changeRequestCollection })));
      const additionalChangeRequests = [changeRequest];
      const expectedCollection: IChangeRequest[] = [...additionalChangeRequests, ...changeRequestCollection];
      jest.spyOn(changeRequestService, 'addChangeRequestToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ fileRecord });
      comp.ngOnInit();

      expect(changeRequestService.query).toHaveBeenCalled();
      expect(changeRequestService.addChangeRequestToCollectionIfMissing).toHaveBeenCalledWith(
        changeRequestCollection,
        ...additionalChangeRequests.map(expect.objectContaining),
      );
      expect(comp.changeRequestsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const fileRecord: IFileRecord = { id: 25181 };
      const changeRequest: IChangeRequest = { id: 26371 };
      fileRecord.changeRequest = changeRequest;

      activatedRoute.data = of({ fileRecord });
      comp.ngOnInit();

      expect(comp.changeRequestsSharedCollection).toContainEqual(changeRequest);
      expect(comp.fileRecord).toEqual(fileRecord);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFileRecord>>();
      const fileRecord = { id: 10709 };
      jest.spyOn(fileRecordFormService, 'getFileRecord').mockReturnValue(fileRecord);
      jest.spyOn(fileRecordService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fileRecord });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fileRecord }));
      saveSubject.complete();

      // THEN
      expect(fileRecordFormService.getFileRecord).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(fileRecordService.update).toHaveBeenCalledWith(expect.objectContaining(fileRecord));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFileRecord>>();
      const fileRecord = { id: 10709 };
      jest.spyOn(fileRecordFormService, 'getFileRecord').mockReturnValue({ id: null });
      jest.spyOn(fileRecordService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fileRecord: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fileRecord }));
      saveSubject.complete();

      // THEN
      expect(fileRecordFormService.getFileRecord).toHaveBeenCalled();
      expect(fileRecordService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFileRecord>>();
      const fileRecord = { id: 10709 };
      jest.spyOn(fileRecordService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fileRecord });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fileRecordService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareChangeRequest', () => {
      it('should forward to changeRequestService', () => {
        const entity = { id: 26371 };
        const entity2 = { id: 20589 };
        jest.spyOn(changeRequestService, 'compareChangeRequest');
        comp.compareChangeRequest(entity, entity2);
        expect(changeRequestService.compareChangeRequest).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
