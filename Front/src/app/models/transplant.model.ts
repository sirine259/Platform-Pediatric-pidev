// Modèles compatibles avec le backend Spring Boot

export enum TransplantStatus {
  SCHEDULED = 'SCHEDULED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  POSTPONED = 'POSTPONED'
}

export enum TransplantType {
  LIVING_DONOR = 'LIVING_DONOR',
  DECEASED_DONOR = 'DECEASED_DONOR',
  PREEMPTIVE = 'PREEMPTIVE',
  SIMULTANEOUS = 'SIMULTANEOUS'
}

export enum SurgeryApproach {
  OPEN = 'OPEN',
  LAPAROSCOPIC = 'LAPAROSCOPIC',
  ROBOTIC_ASSISTED = 'ROBOTIC_ASSISTED',
  MINIMALLY_INVASIVE = 'MINIMALLY_INVASIVE'
}

export enum KidneySource {
  LEFT = 'LEFT',
  RIGHT = 'RIGHT',
  BOTH = 'BOTH',
  EN_BLOC = 'EN_BLOC'
}

export interface Donor {
  id: number;
  name: string;
  age: number;
  bloodType: string;
  // autres champs selon l'entité backend
}

export interface Recipient {
  id: number;
  name: string;
  age: number;
  bloodType: string;
  // autres champs selon l'entité backend
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  // autres champs selon l'entité backend
}

export interface Transplant {
  id: number;
  donor: Donor;
  recipient: Recipient;
  scheduledDate: string;
  actualDate?: string;
  status: TransplantStatus;
  surgeon?: User;
  hospital: string;
  preOperativeNotes?: string;
  postOperativeNotes?: string;
  complications?: string;
  isSuccessful?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface KidneyTransplant {
  id: number;
  transplant: Transplant;
  donor: Donor;
  recipient: Recipient;
  surgeon: User;
  nephrologist?: User;
  surgeryDate: string;
  actualStartTime?: string;
  actualEndTime?: string;
  surgeryDuration?: number;
  transplantType: TransplantType;
  surgeryApproach: SurgeryApproach;
  kidneySource: KidneySource;
  donorKidneyType?: string;
  recipientKidneyType?: string;
  coldIschemiaTime?: number;
  warmIschemiaTime?: number;
  anastomosisTime?: number;
  vascularAnastomosis?: string;
  ureteralImplantation?: string;
  surgicalTechnique?: string;
  hospital?: string;
  operatingRoom?: string;
  anesthesiaType?: string;
  anesthesiaDuration?: string;
  estimatedBloodLoss?: number;
  bloodProductsUsed?: number;
  intraOperativeComplications?: string;
  immediatePostOpComplications?: string;
  hospitalStayDuration?: number;
  postOpMedications?: string;
  immunosuppressionProtocol?: string;
  hlaTyping?: string;
  crossmatchResults?: string;
  panelReactiveAntibodies?: string;
  peakCreatinineLevel?: number;
  creatininePeakDate?: string;
  baselineCreatinineLevel?: number;
  lastDialysisDate?: string;
  delayedGraftFunction?: boolean;
  primaryGraftFunction?: boolean;
  acuteRejection?: boolean;
  rejectionDate?: string;
  rejectionType?: string;
  rejectionTreatment?: string;
  surgicalSiteInfection?: boolean;
  infectionDate?: string;
  infectionTreatment?: string;
  graftFailure?: boolean;
  graftFailureDate?: string;
  failureCause?: string;
  patientSurvival?: boolean;
  patientDeathDate?: string;
  deathCause?: string;
  graftSurvivalMonths?: number;
  qualityOfLifeScore?: string;
  surgicalNotes?: string;
  postOperativeNotes?: string;
  followUpPlan?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PostTransplantFollowUp {
  id: number;
  kidneyTransplant: KidneyTransplant;
  doctor: User;
  followUpDate: string;
  clinicalNotes?: string;
  complications?: string;
  creatinineLevel?: number;
  gfr?: number;
  bloodPressure?: string;
  medicationAdjustments?: string;
  labResults?: string;
  immunosuppressiveTreatment?: string;
  observations?: string;
  isFollowUpComplete: boolean;
  nextFollowUpDate?: string;
  recommendations?: string;
  followUpType?: string;
  patientAttended?: boolean;
  patientFeedback?: string;
  createdAt: string;
  updatedAt: string;
}
