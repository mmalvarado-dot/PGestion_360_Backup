import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IResponsible } from 'app/entities/responsible/responsible.model';
import { ResponsibleService } from 'app/entities/responsible/service/responsible.service';
import { IChangeRequest } from 'app/entities/change-request/change-request.model';
import { ChangeRequestService } from 'app/entities/change-request/service/change-request.service';
import { ITrackingRecord } from '../tracking-record.model';
import { TrackingRecordService } from '../service/tracking-record.service';
import { TrackingRecordFormService } from './tracking-record-form.service';

import { TrackingRecordUpdateComponent } from './tracking-record-update.component';

describe('TrackingRecord Management Update Component', () => {
  let comp: TrackingRecordUpdateComponent;
  let fixture: ComponentFixture<TrackingRecordUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackingRecordFormService: TrackingRecordFormService;
  let trackingRecordService: TrackingRecordService;
  let userService: UserService;
  let responsibleService: ResponsibleService;
  let changeRequestService: ChangeRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TrackingRecordUpdateComponent],
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
      .overrideTemplate(TrackingRecordUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackingRecordUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackingRecordFormService = TestBed.inject(TrackingRecordFormService);
    trackingRecordService = TestBed.inject(TrackingRecordService);
    userService = TestBed.inject(UserService);
    responsibleService = TestBed.inject(ResponsibleService);
    changeRequestService = TestBed.inject(ChangeRequestService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const trackingRecord: ITrackingRecord = { id: 16399 };
      const user: IUser = { id: 3944 };
      trackingRecord.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackingRecord });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('should call Responsible query and add missing value', () => {
      const trackingRecord: ITrackingRecord = { id: 16399 };
      const responsible: IResponsible = { id: 24783 };
      trackingRecord.responsible = responsible;

      const responsibleCollection: IResponsible[] = [{ id: 24783 }];
      jest.spyOn(responsibleService, 'query').mockReturnValue(of(new HttpResponse({ body: responsibleCollection })));
      const additionalResponsibles = [responsible];
      const expectedCollection: IResponsible[] = [...additionalResponsibles, ...responsibleCollection];
      jest.spyOn(responsibleService, 'addResponsibleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackingRecord });
      comp.ngOnInit();

      expect(responsibleService.query).toHaveBeenCalled();
      expect(responsibleService.addResponsibleToCollectionIfMissing).toHaveBeenCalledWith(
        responsibleCollection,
        ...additionalResponsibles.map(expect.objectContaining),
      );
      expect(comp.responsiblesSharedCollection).toEqual(expectedCollection);
    });

    it('should call ChangeRequest query and add missing value', () => {
      const trackingRecord: ITrackingRecord = { id: 16399 };
      const changeRequest: IChangeRequest = { id: 26371 };
      trackingRecord.changeRequest = changeRequest;

      const changeRequestCollection: IChangeRequest[] = [{ id: 26371 }];
      jest.spyOn(changeRequestService, 'query').mockReturnValue(of(new HttpResponse({ body: changeRequestCollection })));
      const additionalChangeRequests = [changeRequest];
      const expectedCollection: IChangeRequest[] = [...additionalChangeRequests, ...changeRequestCollection];
      jest.spyOn(changeRequestService, 'addChangeRequestToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackingRecord });
      comp.ngOnInit();

      expect(changeRequestService.query).toHaveBeenCalled();
      expect(changeRequestService.addChangeRequestToCollectionIfMissing).toHaveBeenCalledWith(
        changeRequestCollection,
        ...additionalChangeRequests.map(expect.objectContaining),
      );
      expect(comp.changeRequestsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const trackingRecord: ITrackingRecord = { id: 16399 };
      const user: IUser = { id: 3944 };
      trackingRecord.user = user;
      const responsible: IResponsible = { id: 24783 };
      trackingRecord.responsible = responsible;
      const changeRequest: IChangeRequest = { id: 26371 };
      trackingRecord.changeRequest = changeRequest;

      activatedRoute.data = of({ trackingRecord });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContainEqual(user);
      expect(comp.responsiblesSharedCollection).toContainEqual(responsible);
      expect(comp.changeRequestsSharedCollection).toContainEqual(changeRequest);
      expect(comp.trackingRecord).toEqual(trackingRecord);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackingRecord>>();
      const trackingRecord = { id: 14377 };
      jest.spyOn(trackingRecordFormService, 'getTrackingRecord').mockReturnValue(trackingRecord);
      jest.spyOn(trackingRecordService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackingRecord });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackingRecord }));
      saveSubject.complete();

      // THEN
      expect(trackingRecordFormService.getTrackingRecord).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackingRecordService.update).toHaveBeenCalledWith(expect.objectContaining(trackingRecord));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackingRecord>>();
      const trackingRecord = { id: 14377 };
      jest.spyOn(trackingRecordFormService, 'getTrackingRecord').mockReturnValue({ id: null });
      jest.spyOn(trackingRecordService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackingRecord: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackingRecord }));
      saveSubject.complete();

      // THEN
      expect(trackingRecordFormService.getTrackingRecord).toHaveBeenCalled();
      expect(trackingRecordService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackingRecord>>();
      const trackingRecord = { id: 14377 };
      jest.spyOn(trackingRecordService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackingRecord });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackingRecordService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareResponsible', () => {
      it('should forward to responsibleService', () => {
        const entity = { id: 24783 };
        const entity2 = { id: 5616 };
        jest.spyOn(responsibleService, 'compareResponsible');
        comp.compareResponsible(entity, entity2);
        expect(responsibleService.compareResponsible).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
