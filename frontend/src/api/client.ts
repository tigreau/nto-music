export { fetchJson, fetchVoid } from '@/api/core';

export {
  type AuthResponse,
  getStoredUser,
  setStoredUser,
  clearStoredUser,
  login,
  register,
  logout,
  verifySession,
} from '@/api/authApi';

export {
  fetchProducts,
  fetchProduct,
  createProduct,
  patchProduct,
  deleteProduct,
  applyDiscount,
} from '@/api/productsApi';

export { fetchCategories, fetchCategoryReviews, createSubcategory } from '@/api/categoriesApi';

export { fetchBrands } from '@/api/brandsApi';

export { fetchCartItems, addToCart, deleteCartItem, clearCart } from '@/api/cartApi';

export { submitCheckout } from '@/api/checkoutApi';

export { fetchUserProfile, updateUserProfile } from '@/api/userApi';

export {
  markNotificationRead,
  markAllNotificationsRead,
  deleteNotification,
} from '@/api/notificationsApi';

export {
  uploadProductImage,
  deleteProductImage,
  setPrimaryProductImage,
  reorderProductImages,
} from '@/api/productImagesApi';
