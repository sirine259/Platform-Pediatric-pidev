export interface KidneyTransplant {
  id: number;
  transplantDate: string;
  status: 'PLANNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  medicalRecord: {
    id: number;
    patient: {
      firstName: string;
      lastName: string;
      medicalRecordNumber: string;
    };
  };
  doctor: {
    id: number;
    user: {
      firstName: string;
      lastName: string;
    };
    specialization: string;
    licenseNumber: string;
  };
  postTransplantFollowUps?: PostTransplantFollowUp[];
  createdAt: string;
  updatedAt: string;
}

export interface PostTransplantFollowUp {
  id: number;
  followUpDate: string;
  immunosuppressiveTreatment: string;
  observations: string;
  clinicalNotes: string;
  complications: string;
  creatinineLevel: number;
  gfr: number;
  bloodPressure: string;
  medicationAdjustments: string;
  labResults: string;
  isFollowUpComplete: boolean;
  nextFollowUpDate: string;
  recommendations: string;
  followUpType: string;
  patientAttended: boolean;
  patientFeedback: string;
  kidneyTransplant: {
    id: number;
    transplantDate: string;
  };
  doctor: {
    id: number;
    user: {
      firstName: string;
      lastName: string;
    };
    specialization: string;
  };
  createdAt: string;
  updatedAt: string;
}
