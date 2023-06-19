export interface UserInfo {
  id: number;
  username: string;
  password: string;
  fullName: string;
  birthday: string;
  gender: string;
  phoneNumber: string;
  email: string;
  code: string;
}

export interface Teacher extends UserInfo {}
export interface Student extends UserInfo {
  joinDate: string;
  course: number;
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
    id: number;
    content: string;
    isCorrected?: string;
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

export interface Test {
  id: number;
  createdAt: string;
  updatedAt?: string;
  totalPoint: number;
  testDay: string;
  subjectCode: string;
  subjectTitle: string;
  duration: number;
}
// constants

export const AUTH_USER = {
  admin: "admin",
  student: "student",
  teacher: "teacher",
};

export const LEVEL = [
  { key: "EASY", label: "EASY" },
  { key: "MEDIUM", label: "MEDIUM" },
  { key: "HARD", label: "HARD" },
];

export const ANSWERS = [
  { key: 1, name: "A" },
  { key: 2, name: "B" },
  { key: 3, name: "C" },
  { key: 4, name: "D" },
];

// functions
export const stringToBoolean = (stringValue: string) => {
  switch (stringValue?.toLowerCase()?.trim()) {
    case "true":
      return true;

    case "false":
      return false;

    default:
      return JSON.parse(stringValue);
  }
};
