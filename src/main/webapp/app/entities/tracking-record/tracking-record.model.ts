import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IResponsible } from 'app/entities/responsible/responsible.model';
import { IChangeRequest } from 'app/entities/change-request/change-request.model';

export interface ITrackingRecord {
  id: number;
  changeDate?: dayjs.Dayjs | null;
  status?: string | null;
  comments?: string | null;
  user?: Pick<IUser, 'id'> | null;
  responsible?: Pick<IResponsible, 'id'> | null;
  changeRequest?: Pick<IChangeRequest, 'id'> | null;
}

export type NewTrackingRecord = Omit<ITrackingRecord, 'id'> & { id: null };
