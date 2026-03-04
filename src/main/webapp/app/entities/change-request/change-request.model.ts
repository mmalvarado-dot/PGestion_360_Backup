import dayjs from 'dayjs/esm';
import { IItemCatalogue } from 'app/entities/item-catalogue/item-catalogue.model';
import { prioridad } from 'app/entities/enumerations/prioridad.model';
import { Impacto } from 'app/entities/enumerations/impacto.model';

// Agregamos una interfaz muy básica para el Usuario (para que Angular no llore)
export interface IUser {
  id: number;
  login: string;
}

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
  user?: Pick<IUser, 'id' | 'login'> | null; // <--- ¡AQUÍ ESTÁ LA MAGIA!
  itemCatalogue?: Pick<IItemCatalogue, 'id'> | null;
}

export type NewChangeRequest = Omit<IChangeRequest, 'id'> & { id: null };
