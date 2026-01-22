import { IResponsible, NewResponsible } from './responsible.model';

export const sampleWithRequiredData: IResponsible = {
  id: 8490,
  name: 'whether eek round',
  position: 'yuppify',
};

export const sampleWithPartialData: IResponsible = {
  id: 24212,
  name: 'into considerate',
  position: 'provided',
};

export const sampleWithFullData: IResponsible = {
  id: 5089,
  name: 'potentially blah',
  position: 'likewise intrepid consequently',
};

export const sampleWithNewData: NewResponsible = {
  name: 'overcooked obstruct',
  position: 'plugin collaboration roadway',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
