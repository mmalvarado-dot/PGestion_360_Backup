export interface ICatalogue {
  id: number;
  name?: string | null;
  code?: string | null;
  status?: boolean | null;
}

export type NewCatalogue = Omit<ICatalogue, 'id'> & { id: null };
