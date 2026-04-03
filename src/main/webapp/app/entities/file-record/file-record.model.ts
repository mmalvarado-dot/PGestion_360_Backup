import { IChangeRequest } from 'app/entities/change-request/change-request.model';

export interface IFileRecord {
  id: number;
  fileName?: string | null;
  filePath?: string | null;
  fileType?: string | null;
  content?: string | null;
  contentContentType?: string | null;
  uploadDate?: any | null; // <-- ¡Aquí está la magia para que Angular acepte la fecha!
  changeRequest?: Pick<IChangeRequest, 'id'> | null;
}

export type NewFileRecord = Omit<IFileRecord, 'id'> & { id: null };
