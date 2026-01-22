import { ICatalogue } from 'app/entities/catalogue/catalogue.model';

export interface IItemCatalogue {
  id: number;
  name?: string | null;
  code?: string | null;
  catalogueCode?: string | null;
  active?: boolean | null;
  catalogue?: Pick<ICatalogue, 'id'> | null;
}

export type NewItemCatalogue = Omit<IItemCatalogue, 'id'> & { id: null };
