import { ICatalogue, NewCatalogue } from './catalogue.model';

export const sampleWithRequiredData: ICatalogue = {
  id: 15807,
  name: 'gullible',
  code: 'but more gee',
};

export const sampleWithPartialData: ICatalogue = {
  id: 6723,
  name: 'tangible bookend somber',
  code: 'brr independence',
};

export const sampleWithFullData: ICatalogue = {
  id: 5490,
  name: 'longingly wilderness',
  code: 'but likewise reschedule',
  status: false,
};

export const sampleWithNewData: NewCatalogue = {
  name: 'barring function',
  code: 'hmph',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
