export interface UserInfo {
  id: number;
  fullName: string;
  birthday: string;
  gender: string;
  joinDate?: string;
  phoneNumber: string;
  email: string;
  code: string;
}

export interface MenuItem {
  title: string;
  icon: string;
  path?: string;
}

export interface Question {
  id: number;
  topicText?: string;
  topicImage?: string;
  createdDate?: string;
  level: string;
  answers: {
    content: string;
    corrected?: boolean;
  }[];
}

export interface ExamResult {
  id: number;
  examCompletionDate: string;
  examName: string;
  result: string;
  examTime: string;
}

export interface Subject {
  id: number;
  title: string;
  code: string;
  description: string;
  credit: number;
  chapterQuantity?: number;
  questionQuantity?: number;
}

export interface Chapter {
  id: number;
  title: string;
  order: number;
}
// constants

export const AUTH_USER = {
  admin: "admin",
  student: "student",
  teacher: "teacher",
};
