import dayjs from 'dayjs/esm';
import { IResponsible } from 'app/entities/responsible/responsible.model';
import { IItemCatalogue } from 'app/entities/item-catalogue/item-catalogue.model';
import { prioridad } from 'app/entities/enumerations/prioridad.model';
import { Impacto } from 'app/entities/enumerations/impacto.model';

export interface IChangeRequest {
  id: number;
  title?: string | null;
  description?: string | null;
  createdDate?: dayjs.Dayjs | null;
  updatedDate?: dayjs.Dayjs | null;
  priority?: keyof typeof prioridad | null;
  impact?: keyof typeof Impacto | null;
  status?: string | null;
  fechaEntrega?: dayjs.Dayjs | null;
  observaciones?: string | null;
  archivoAdjunto?: string | null;
  archivoAdjuntoContentType?: string | null;
  solicitante?: string | null;
  departamento?: string | null;
  responsible?: Pick<IResponsible, 'id' | 'name'> | null;
  itemCatalogue?: Pick<IItemCatalogue, 'id'> | null;
}

export type NewChangeRequest = Omit<IChangeRequest, 'id'> & { id: null };
