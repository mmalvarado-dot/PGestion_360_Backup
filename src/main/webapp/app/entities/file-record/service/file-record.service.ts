import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFileRecord, NewFileRecord } from '../file-record.model';

export type PartialUpdateFileRecord = Partial<IFileRecord> & Pick<IFileRecord, 'id'>;

export type EntityResponseType = HttpResponse<IFileRecord>;
export type EntityArrayResponseType = HttpResponse<IFileRecord[]>;

@Injectable({ providedIn: 'root' })
export class FileRecordService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/file-records');

  create(fileRecord: NewFileRecord): Observable<EntityResponseType> {
    return this.http.post<IFileRecord>(this.resourceUrl, fileRecord, { observe: 'response' });
  }

  update(fileRecord: IFileRecord): Observable<EntityResponseType> {
    return this.http.put<IFileRecord>(`${this.resourceUrl}/${this.getFileRecordIdentifier(fileRecord)}`, fileRecord, {
      observe: 'response',
    });
  }

  partialUpdate(fileRecord: PartialUpdateFileRecord): Observable<EntityResponseType> {
    return this.http.patch<IFileRecord>(`${this.resourceUrl}/${this.getFileRecordIdentifier(fileRecord)}`, fileRecord, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFileRecord>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFileRecord[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFileRecordIdentifier(fileRecord: Pick<IFileRecord, 'id'>): number {
    return fileRecord.id;
  }

  compareFileRecord(o1: Pick<IFileRecord, 'id'> | null, o2: Pick<IFileRecord, 'id'> | null): boolean {
    return o1 && o2 ? this.getFileRecordIdentifier(o1) === this.getFileRecordIdentifier(o2) : o1 === o2;
  }

  addFileRecordToCollectionIfMissing<Type extends Pick<IFileRecord, 'id'>>(
    fileRecordCollection: Type[],
    ...fileRecordsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const fileRecords: Type[] = fileRecordsToCheck.filter(isPresent);
    if (fileRecords.length > 0) {
      const fileRecordCollectionIdentifiers = fileRecordCollection.map(fileRecordItem => this.getFileRecordIdentifier(fileRecordItem));
      const fileRecordsToAdd = fileRecords.filter(fileRecordItem => {
        const fileRecordIdentifier = this.getFileRecordIdentifier(fileRecordItem);
        if (fileRecordCollectionIdentifiers.includes(fileRecordIdentifier)) {
          return false;
        }
        fileRecordCollectionIdentifiers.push(fileRecordIdentifier);
        return true;
      });
      return [...fileRecordsToAdd, ...fileRecordCollection];
    }
    return fileRecordCollection;
  }
}
