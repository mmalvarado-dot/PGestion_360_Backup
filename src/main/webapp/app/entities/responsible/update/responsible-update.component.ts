import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IResponsible } from '../responsible.model';
import { ResponsibleService } from '../service/responsible.service';
import { ResponsibleFormGroup, ResponsibleFormService } from './responsible-form.service';

@Component({
  selector: 'jhi-responsible-update',
  templateUrl: './responsible-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ResponsibleUpdateComponent implements OnInit {
  isSaving = false;
  responsible: IResponsible | null = null;

  protected responsibleService = inject(ResponsibleService);
  protected responsibleFormService = inject(ResponsibleFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ResponsibleFormGroup = this.responsibleFormService.createResponsibleFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ responsible }) => {
      this.responsible = responsible;
      if (responsible) {
        this.updateForm(responsible);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const responsible = this.responsibleFormService.getResponsible(this.editForm);
    if (responsible.id !== null) {
      this.subscribeToSaveResponse(this.responsibleService.update(responsible));
    } else {
      this.subscribeToSaveResponse(this.responsibleService.create(responsible));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResponsible>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(responsible: IResponsible): void {
    this.responsible = responsible;
    this.responsibleFormService.resetForm(this.editForm, responsible);
  }
}
