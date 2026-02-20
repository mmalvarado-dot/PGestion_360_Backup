import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IResponsible } from 'app/entities/responsible/responsible.model';
import { IChangeRequest } from 'app/entities/change-request/change-request.model';
// 1. AGREGAMOS ESTA IMPORTACIÓN
import { IDepartment } from 'app/entities/department/department.model';

export interface ITrackingRecord {
  id: number;
  changeDate?: dayjs.Dayjs | null;
  status?: string | null;
  comments?: string | null;

  user?: Pick<IUser, 'id' | 'login'> | null;

  responsible?: Pick<IResponsible, 'id' | 'name'> | null;

  changeRequest?: Pick<IChangeRequest, 'id'> | null;

  // 2. CORREGIMOS ESTO:
  // Cambiamos 'string' por 'IDepartment' (o Pick si prefieres solo traer id y nombre)
  department?: IDepartment | null;
}

export type NewTrackingRecord = Omit<ITrackingRecord, 'id'> & { id: null };
