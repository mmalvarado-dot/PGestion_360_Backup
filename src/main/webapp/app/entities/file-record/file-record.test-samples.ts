import { IFileRecord, NewFileRecord } from './file-record.model';

export const sampleWithRequiredData: IFileRecord = {
  id: 10013,
  fileName: 'angle wombat',
  filePath: 'fortunately out',
  fileType: 'institutionalize bind',
};

export const sampleWithPartialData: IFileRecord = {
  id: 7565,
  fileName: 'alert',
  filePath: 'innocently',
  fileType: 'against new till',
  content: '../fake-data/blob/hipster.png',
  contentContentType: 'unknown',
};

export const sampleWithFullData: IFileRecord = {
  id: 5939,
  fileName: 'nor dislocate afraid',
  filePath: 'babyish coagulate',
  fileType: 'circumnavigate for closely',
  content: '../fake-data/blob/hipster.png',
  contentContentType: 'unknown',
};

export const sampleWithNewData: NewFileRecord = {
  fileName: 'across circumnavigate',
  filePath: 'focused',
  fileType: 'fold ouch inside',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
