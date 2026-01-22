import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IResponsible } from '../responsible.model';

@Component({
  selector: 'jhi-responsible-detail',
  templateUrl: './responsible-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ResponsibleDetailComponent {
  responsible = input<IResponsible | null>(null);

  previousState(): void {
    window.history.back();
  }
}
