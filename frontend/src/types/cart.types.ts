export interface CartProduct {
  id: number;
  name: string;
  slug: string;
  price: number;
  thumbnailUrl: string | null;
  category: { id: number; name: string; slug: string } | null;
  brand: { id: number; name: string; slug: string } | null;
}

export interface CartItem {
  id: number;
  quantity: number;
  product: CartProduct;
  subTotal: number;
}
