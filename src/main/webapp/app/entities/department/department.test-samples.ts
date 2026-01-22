import { IDepartment, NewDepartment } from './department.model';

export const sampleWithRequiredData: IDepartment = {
  id: 32001,
  departmentName: 'phew',
};

export const sampleWithPartialData: IDepartment = {
  id: 26867,
  departmentName: 'pfft',
};

export const sampleWithFullData: IDepartment = {
  id: 13276,
  departmentName: 'endow',
  field: 'headline save',
};

export const sampleWithNewData: NewDepartment = {
  departmentName: 'peaceful if',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
