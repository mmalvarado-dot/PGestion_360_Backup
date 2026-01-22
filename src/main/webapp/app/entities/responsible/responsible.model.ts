export interface IResponsible {
  id: number;
  name?: string | null;
  position?: string | null;
}

export type NewResponsible = Omit<IResponsible, 'id'> & { id: null };
