export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: Role;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface Role {
  id: string;
  name: string;
  permissions: string[];
}

export interface Patient {
  id: string;
  firstName: string;
  lastName: string;
  dateOfBirth: Date;
  medicalRecordNumber: string;
  parentId?: string;
  assignedDoctorId?: string;
  assignedNurseId?: string;
}

export interface Doctor {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  specialization: string;
  licenseNumber: string;
}

export interface Nurse {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  licenseNumber: string;
  department: string;
}

export interface Parent {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  childrenIds: string[];
}

export interface Administrator {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  permissions: string[];
}
