import { IChangeRequest } from 'app/entities/change-request/change-request.model';

export interface IFileRecord {
  id: number;
  fileName?: string | null;
  filePath?: string | null;
  fileType?: string | null;
  content?: string | null;
  contentContentType?: string | null;
  changeRequest?: Pick<IChangeRequest, 'id'> | null;
}

export type NewFileRecord = Omit<IFileRecord, 'id'> & { id: null };
