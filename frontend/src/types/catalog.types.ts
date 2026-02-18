export interface Brand {
  id: number;
  name: string;
  slug: string;
  logoUrl: string | null;
}

export interface Category {
  id: number;
  name: string;
  slug: string;
  description?: string;
  productCount: number;
  subCategories?: Category[];
}

export interface Review {
  id: number;
  userName: string;
  rating: number;
  comment: string;
  productName: string;
  productThumbnailUrl: string | null;
  verifiedPurchase: boolean;
  datePosted: string;
}

export interface CategoryReviews {
  categoryName: string;
  averageRating: number | null;
  reviewCount: number;
  reviews: Review[];
}
