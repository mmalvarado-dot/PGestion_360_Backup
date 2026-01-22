import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ICatalogue } from '../catalogue.model';

@Component({
  selector: 'jhi-catalogue-detail',
  templateUrl: './catalogue-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class CatalogueDetailComponent {
  catalogue = input<ICatalogue | null>(null);

  previousState(): void {
    window.history.back();
  }
}
