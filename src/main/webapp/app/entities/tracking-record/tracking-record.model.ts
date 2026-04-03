import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IChangeRequest } from 'app/entities/change-request/change-request.model';
import { IDepartment } from 'app/entities/department/department.model';

export interface ITrackingRecord {
  id: number;
  changeDate?: dayjs.Dayjs | null;
  status?: string | null;
  comments?: string | null;

  user?: Pick<IUser, 'id' | 'login'> | null;

  changeRequest?: Pick<IChangeRequest, 'id'> | null;

  department?: IDepartment | null;
}

export type NewTrackingRecord = Omit<ITrackingRecord, 'id'> & { id: null };
