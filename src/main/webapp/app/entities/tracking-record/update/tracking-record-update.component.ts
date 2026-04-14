import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable, forkJoin, of } from 'rxjs';
import { finalize, map, switchMap } from 'rxjs/operators';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import SharedModule from 'app/shared/shared.module';
import { ITrackingRecord } from '../tracking-record.model';
import { TrackingRecordService } from '../service/tracking-record.service';
import { TrackingRecordFormGroup, TrackingRecordFormService } from './tracking-record-form.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IChangeRequest } from 'app/entities/change-request/change-request.model';
import { ChangeRequestService } from 'app/entities/change-request/service/change-request.service';
import { IDepartment } from 'app/entities/department/department.model';
import { DepartmentService } from 'app/entities/department/service/department.service';

import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import dayjs from 'dayjs';

interface PendingFile {
  file: File;
  contentType: string;
}

@Component({
  selector: 'jhi-tracking-record-update',
  templateUrl: './tracking-record-update.component.html',
  standalone: true,
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TrackingRecordUpdateComponent implements OnInit {
  isSaving = false;
  trackingRecord: ITrackingRecord | null = null;
  isPrefilled = false;

  // Variable libre para la etapa
  etapaTexto: string = '';

  usersSharedCollection: IUser[] = [];
  changeRequestsSharedCollection: IChangeRequest[] = [];
  departmentsSharedCollection: IDepartment[] = [];

  // Textos para mostrar en pantalla
  prefilledDepartmentName = 'Cargando...';
  prefilledUserName = 'Cargando...';

  selectedFiles: PendingFile[] = [];
  minDate: NgbDateStruct;

  protected trackingRecordService = inject(TrackingRecordService);
  protected trackingRecordFormService = inject(TrackingRecordFormService);
  protected userService = inject(UserService);
  protected changeRequestService = inject(ChangeRequestService);
  protected departmentService = inject(DepartmentService);
  protected activatedRoute = inject(ActivatedRoute);

  editForm: TrackingRecordFormGroup = this.trackingRecordFormService.createTrackingRecordFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);
  compareChangeRequest = (o1: IChangeRequest | null, o2: IChangeRequest | null): boolean =>
    this.changeRequestService.compareChangeRequest(o1, o2);
  compareDepartment = (o1: IDepartment | null, o2: IDepartment | null): boolean => {
    return o1 && o2 ? o1.id === o2.id : o1 === o2;
  };

  constructor() {
    const today = new Date();
    this.minDate = { year: today.getFullYear(), month: today.getMonth() + 1, day: today.getDate() };
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ trackingRecord }) => {
      this.trackingRecord = trackingRecord;
      if (trackingRecord) {
        this.updateForm(trackingRecord);
      }
      this.loadRelationshipsOptions();
    });

    this.activatedRoute.queryParams.subscribe(params => {
      const reqId = params['reqId'];
      if (reqId) {
        this.preLlenarDatosSolicitud(Number(reqId));
      }
    });
  }

  preLlenarDatosSolicitud(id: number): void {
    this.changeRequestService.find(id).subscribe(res => {
      const cr: any = res.body;
      if (cr) {
        // Nos aseguramos de que el estado empate exactamente con los value del select
        let status = cr.status || cr.estado || 'PENDIENTE';
        if (status === 'EN PROCESO') status = 'EN_PROCESO';

        // 🚀 CORRECCIÓN: Asignamos directamente lo que venga (string u objeto) sin bloquearlo a null
        let formDept = cr.department || cr.departamento || null;
        let formUser = cr.user || cr.usuario || null;

        this.editForm.patchValue({
          changeRequest: cr,
          status: status,
          department: formDept,
          user: formUser,
        });

        this.isPrefilled = true;

        this.prefilledDepartmentName =
          typeof cr.departamento === 'string'
            ? cr.departamento
            : typeof cr.department === 'string'
              ? cr.department
              : formDept?.departmentName || formDept?.nombre || formDept?.name || formDept?.id || 'No asignado';

        this.prefilledUserName =
          typeof cr.user === 'string' ? cr.user : formUser?.login || formUser?.username || formUser?.nombre || 'No asignado';
      }
    });
  }

  setFileData(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target && target.files && target.files.length > 0) {
      Array.from(target.files).forEach(file => {
        this.selectedFiles.push({ file: file, contentType: file.type });
      });
      target.value = '';
    }
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    const valoresFormulario = this.editForm.getRawValue();

    // 🛑 BARRERA DE HORMIGÓN (Se mantiene intacta)
    if (
      !this.etapaTexto ||
      this.etapaTexto.trim() === '' ||
      !valoresFormulario.comments ||
      valoresFormulario.comments.trim() === '' ||
      !valoresFormulario.changeDate ||
      !valoresFormulario.status
    ) {
      alert(
        '⚠️ ¡ALTO! No puedes guardar.\n\nPor favor, asegúrate de llenar:\n- La Fecha del Avance\n- El Estado de la Solicitud\n- La Etapa de la Solicitud\n- Los Comentarios',
      );
      return;
    }

    this.isSaving = true;
    const trackingRecord = this.trackingRecordFormService.getTrackingRecord(this.editForm);

    // 🕒 Fecha y hora local
    trackingRecord.changeDate = dayjs() as any;
    (trackingRecord as any).status = valoresFormulario.status;

    // 1. Obtenemos el comentario base y armamos el texto
    let comentarioFinal = trackingRecord.comments ? trackingRecord.comments : '';
    if (this.etapaTexto && this.etapaTexto.trim() !== '') {
      comentarioFinal = `[Etapa: ${this.etapaTexto.trim()}] \n${comentarioFinal}`;
    }
    if (this.selectedFiles && this.selectedFiles.length > 0) {
      const textoArchivos = this.selectedFiles.map(pf => `Se adjuntó el archivo: ${pf.file.name}`).join('\n');
      comentarioFinal = `${comentarioFinal}\n${textoArchivos}`.trim();
    }
    trackingRecord.comments = comentarioFinal;

    // MAGIA: Obtenemos la solicitud padre para actualizarla después
    const solicitudPadre: any = valoresFormulario.changeRequest;
    const nuevoEstado = valoresFormulario.status;

    // 🚀 SOLUCIÓN DEFINITIVA: Buscar los OBJETOS reales para que el servidor no dé "Bad Request"
    if (solicitudPadre) {
      // 1. Rescatar y armar el objeto del DEPARTAMENTO
      const rawDept = solicitudPadre.department || solicitudPadre.departamento || (trackingRecord as any).department;
      let objDept = null;

      if (typeof rawDept === 'string') {
        // Si es un texto, lo buscamos en la lista desplegable de departamentos para sacar su objeto con ID
        objDept = this.departmentsSharedCollection.find(d => d.departmentName === rawDept || (d as any).nombre === rawDept);
      } else if (typeof rawDept === 'object') {
        objDept = rawDept; // Si ya es objeto, lo dejamos tal cual
      }
      (trackingRecord as any).department = objDept;

      // 2. Rescatar y armar el objeto del USUARIO
      const rawUser = solicitudPadre.user || solicitudPadre.usuario || (trackingRecord as any).user;
      let objUser = null;

      if (typeof rawUser === 'string') {
        // Si es un texto, lo buscamos en la lista de usuarios
        objUser = this.usersSharedCollection.find(
          u => u.login === rawUser || (u as any).nombre === rawUser || (u as any).username === rawUser,
        );
      } else if (typeof rawUser === 'object') {
        objUser = rawUser;
      }
      (trackingRecord as any).user = objUser;
    }

    // 🧹 LIMPIEZA ESTRICTA (Seguro de vida contra el error 400 Bad Request)
    (trackingRecord as any).actionType = null;
    if ((trackingRecord as any).department && typeof (trackingRecord as any).department !== 'object') {
      (trackingRecord as any).department = null;
    }
    if ((trackingRecord as any).user && typeof (trackingRecord as any).user !== 'object') {
      (trackingRecord as any).user = null;
    }

    // Guardamos el Tracking Record y le pasamos los datos del Padre para que los actualice simultáneamente
    if (trackingRecord.id !== null && trackingRecord.id !== undefined) {
      this.subscribeToSaveResponse(this.trackingRecordService.update(trackingRecord), solicitudPadre, nuevoEstado);
    } else {
      this.subscribeToSaveResponse(this.trackingRecordService.create(trackingRecord), solicitudPadre, nuevoEstado);
    }
  }

  // MODIFICADO: Ahora esta función recibe la solicitud padre para actualizarla en la base de datos
  protected subscribeToSaveResponse(
    result: Observable<HttpResponse<ITrackingRecord>>,
    solicitudPadre: any,
    nuevoEstado: string | null | undefined,
  ): void {
    result
      .pipe(
        switchMap((res: HttpResponse<ITrackingRecord>) => {
          const savedRecord = res.body!;

          // Creamos una lista de tareas secundarias que el servidor debe hacer antes de regresarnos a la pantalla anterior
          const tareasSecundarias = [];

          // Tarea 1: Subir los archivos (si los hay)
          if (this.selectedFiles.length > 0 && savedRecord.id) {
            const fileSaveObservables = this.selectedFiles.map(pf => this.trackingRecordService.uploadFile(savedRecord.id!, pf.file));
            tareasSecundarias.push(forkJoin(fileSaveObservables));
          }

          // Tarea 2: Actualizar la Solicitud Principal con el nuevo Estado
          if (solicitudPadre && nuevoEstado) {
            solicitudPadre.status = nuevoEstado;
            solicitudPadre.estado = nuevoEstado; // Lo mandamos doble por si la base de datos usa la variable en español
            tareasSecundarias.push(this.changeRequestService.update(solicitudPadre));
          }

          // Ejecutar las tareas simultáneamente
          if (tareasSecundarias.length > 0) {
            return forkJoin(tareasSecundarias).pipe(map(() => res));
          }

          return of(res);
        }),
        finalize(() => (this.isSaving = false)),
      )
      .subscribe({
        next: () => this.previousState(),
        error: () => (this.isSaving = false),
      });
  }

  protected updateForm(trackingRecord: ITrackingRecord): void {
    this.trackingRecord = trackingRecord;
    this.trackingRecordFormService.resetForm(this.editForm, trackingRecord);
    this.loadRelationshipsOptions();
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .subscribe(u => {
        this.usersSharedCollection = u;
      });

    this.changeRequestService
      .query()
      .pipe(map((res: HttpResponse<IChangeRequest[]>) => res.body ?? []))
      .subscribe(c => {
        this.changeRequestsSharedCollection = c;
      });

    this.departmentService
      .query()
      .pipe(map((res: HttpResponse<IDepartment[]>) => res.body ?? []))
      .subscribe(d => {
        this.departmentsSharedCollection = d;
      });
  }
}
