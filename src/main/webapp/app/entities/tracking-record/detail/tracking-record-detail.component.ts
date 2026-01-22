import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { ITrackingRecord } from '../tracking-record.model';

@Component({
  selector: 'jhi-tracking-record-detail',
  templateUrl: './tracking-record-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class TrackingRecordDetailComponent {
  trackingRecord = input<ITrackingRecord | null>(null);

  previousState(): void {
    window.history.back();
  }
}
