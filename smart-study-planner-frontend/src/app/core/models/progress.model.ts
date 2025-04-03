export interface Progress {
  id: number;
  taskId: number;
  userId: number;
  studyDate: Date;
  duration: number; // minutes
  notes: string;
}