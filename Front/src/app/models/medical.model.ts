export interface MedicalRecord {
  id: string;
  patientId: string;
  doctorId: string;
  diagnosis: string;
  treatment: string;
  notes: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface Appointment {
  id: string;
  patientId: string;
  doctorId: string;
  date: Date;
  time: string;
  status: 'scheduled' | 'completed' | 'cancelled';
  notes?: string;
}

export interface Prescription {
  id: string;
  patientId: string;
  doctorId: string;
  medication: string;
  dosage: string;
  frequency: string;
  startDate: Date;
  endDate: Date;
  instructions: string;
  isActive: boolean;
}

export interface LabResult {
  id: string;
  patientId: string;
  testType: string;
  result: string;
  normalRange: string;
  unit: string;
  testDate: Date;
  doctorId: string;
  notes?: string;
}

export interface Dialysis {
  id: string;
  patientId: string;
  nurseId: string;
  date: Date;
  duration: number;
  parameters: DialysisParameters;
  notes: string;
}

export interface DialysisParameters {
  bloodFlowRate: number;
  dialysateFlowRate: number;
  ultrafiltration: number;
  conductivity: number;
  temperature: number;
}

export interface KidneyTransplant {
  id: string;
  patientId: string;
  doctorId: string;
  transplantDate: Date;
  donorType: 'living' | 'deceased';
  status: 'planned' | 'completed' | 'failed';
  notes: string;
}

export interface GrowthMonitoring {
  id: string;
  patientId: string;
  nurseId: string;
  date: Date;
  weight: number;
  height: number;
  bmi: number;
  headCircumference?: number;
  notes: string;
}

export interface PostTransplantFollowUp {
  id: string;
  transplantId: string;
  patientId: string;
  doctorId: string;
  followUpDate: Date;
  creatinineLevel: number;
  gfr: number;
  bloodPressure: string;
  rejectionStatus: 'none' | 'acute' | 'chronic';
  medications: string[];
  notes: string;
}

export interface Treatment {
  id: string;
  patientId: string;
  doctorId: string;
  name: string;
  description: string;
  startDate: Date;
  endDate?: Date;
  status: 'active' | 'completed' | 'paused';
  frequency: string;
}

export interface Message {
  id: string;
  senderId: string;
  receiverId: string;
  subject: string;
  content: string;
  timestamp: Date;
  isRead: boolean;
  priority: 'low' | 'medium' | 'high';
}

export interface Post {
  id: string;
  authorId: string;
  title: string;
  content: string;
  category: string;
  createdAt: Date;
  updatedAt: Date;
  isPublished: boolean;
}

export interface Forum {
  id: string;
  name: string;
  description: string;
  category: string;
  posts: Post[];
  moderators: string[];
}
