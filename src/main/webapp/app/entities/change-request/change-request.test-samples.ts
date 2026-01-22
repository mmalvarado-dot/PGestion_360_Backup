import dayjs from 'dayjs/esm';

import { IChangeRequest, NewChangeRequest } from './change-request.model';

export const sampleWithRequiredData: IChangeRequest = {
  id: 19911,
  title: 'wherever',
  description: 'supposing apud ecliptic',
  createdDate: dayjs('2026-01-20'),
  status: 'bah',
};

export const sampleWithPartialData: IChangeRequest = {
  id: 26629,
  title: 'concerning',
  description: 'more pulverize rubric',
  createdDate: dayjs('2026-01-20'),
  updatedDate: dayjs('2026-01-20'),
  priority: 'BAJA',
  status: 'yowza ack',
  fechaEntrega: dayjs('2026-01-20'),
  observaciones: '../fake-data/blob/hipster.txt',
  archivoAdjunto: '../fake-data/blob/hipster.png',
  archivoAdjuntoContentType: 'unknown',
};

export const sampleWithFullData: IChangeRequest = {
  id: 24188,
  title: 'scorn foolhardy fervently',
  description: 'book inside intellect',
  createdDate: dayjs('2026-01-20'),
  updatedDate: dayjs('2026-01-20'),
  priority: 'ALTA',
  impact: 'MEDIO',
  status: 'obvious',
  fechaEntrega: dayjs('2026-01-20'),
  observaciones: '../fake-data/blob/hipster.txt',
  archivoAdjunto: '../fake-data/blob/hipster.png',
  archivoAdjuntoContentType: 'unknown',
  solicitante: 'amidst mmm calmly',
  departamento: 'polyester',
};

export const sampleWithNewData: NewChangeRequest = {
  title: 'meanwhile ha exasperation',
  description: 'chow however',
  createdDate: dayjs('2026-01-20'),
  status: 'private blah',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
