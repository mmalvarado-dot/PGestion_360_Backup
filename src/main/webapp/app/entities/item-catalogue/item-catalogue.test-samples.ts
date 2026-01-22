import { IItemCatalogue, NewItemCatalogue } from './item-catalogue.model';

export const sampleWithRequiredData: IItemCatalogue = {
  id: 30325,
  name: 'abaft',
  code: 'seal next',
  catalogueCode: 'boohoo hastily',
};

export const sampleWithPartialData: IItemCatalogue = {
  id: 18192,
  name: 'minus',
  code: 'huzzah',
  catalogueCode: 'cinder finally',
};

export const sampleWithFullData: IItemCatalogue = {
  id: 25699,
  name: 'when loftily spirited',
  code: 'valuable unfinished amid',
  catalogueCode: 'confound',
  active: false,
};

export const sampleWithNewData: NewItemCatalogue = {
  name: 'uh-huh ah pfft',
  code: 'where wonderfully',
  catalogueCode: 'what frankly properly',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
