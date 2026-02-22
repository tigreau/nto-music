export interface UserProfile {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  street: string;
  number: string;
  postalCode: string;
  city: string;
  country: string;
}

export interface AuthUser {
  userId: number;
  email: string;
  firstName: string;
  lastName?: string;
  role: string;
}
