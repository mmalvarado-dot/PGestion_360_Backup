export interface IDepartment {
  id: number;
  departmentName?: string | null;
  field?: string | null;
  parentDepartment?: Pick<IDepartment, 'id'> | null;
}

export type NewDepartment = Omit<IDepartment, 'id'> & { id: null };
