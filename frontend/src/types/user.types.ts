export interface UserProfile {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
}

export interface AuthUser {
  userId: number;
  email: string;
  firstName: string;
  lastName?: string;
  role: string;
}
