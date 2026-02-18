export interface CheckoutRequestPayload {
  paymentMethod: string;
  street: string;
  number: string;
  postalCode: string;
  city: string;
  country: string;
}

export interface CheckoutResult {
  orderId: number;
  totalAmount: number;
  paymentStatus: string;
  transactionId: string;
}
