import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITrackingRecord } from '../tracking-record.model';
import { TrackingRecordService } from '../service/tracking-record.service';

@Component({
  templateUrl: './tracking-record-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TrackingRecordDeleteDialogComponent {
  trackingRecord?: ITrackingRecord;

  protected trackingRecordService = inject(TrackingRecordService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.trackingRecordService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
