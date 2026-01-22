import dayjs from 'dayjs/esm';

import { ITrackingRecord, NewTrackingRecord } from './tracking-record.model';

export const sampleWithRequiredData: ITrackingRecord = {
  id: 18861,
  changeDate: dayjs('2026-01-20'),
  status: 'evince why',
};

export const sampleWithPartialData: ITrackingRecord = {
  id: 32162,
  changeDate: dayjs('2026-01-20'),
  status: 'amongst',
  comments: 'whenever regarding by',
};

export const sampleWithFullData: ITrackingRecord = {
  id: 26510,
  changeDate: dayjs('2026-01-20'),
  status: 'humor on',
  comments: 'besmirch whether',
};

export const sampleWithNewData: NewTrackingRecord = {
  changeDate: dayjs('2026-01-20'),
  status: 'nab whoa',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
