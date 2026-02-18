import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { ShoppingCart, ChevronDown, ChevronLeft, ChevronRight } from "lucide-react"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { ErrorState } from '@/components/state/ErrorState';
import { LoadingState } from '@/components/state/LoadingState';
import { getApiErrorPolicy } from '@/lib/apiError';
import { getCategoryImage } from '@/lib/categoryUtils';
import { filterProductsByCategory, sortProductsByOption } from '@/lib/productView';
import { useProducts } from '@/hooks/useApi';
import { Product } from '@/types';

type FeaturedProduct = Product & { description?: string };

interface FeaturedProductsProps {
  onAddToCart?: (productId: number) => void
  onProductClick?: (product: FeaturedProduct) => void
  isAdmin?: boolean
  categoryFilter?: string | null
}

const ITEMS_PER_PAGE = 12

export function FeaturedProducts({ onAddToCart, onProductClick, isAdmin, categoryFilter }: FeaturedProductsProps) {
  const [sortBy, setSortBy] = useState("Recommended")
  const [currentPage, setCurrentPage] = useState(1)
  const {
    data: productsPage,
    isLoading,
    isError,
    error,
    refetch,
  } = useProducts({ size: 200, page: 0, sort: 'recommended' });
  const products = (productsPage?.content ?? []) as FeaturedProduct[];

  useEffect(() => {
    setCurrentPage(1)
  }, [categoryFilter])

  // Filter products by category if filter is applied
  const filteredProducts = filterProductsByCategory(products, categoryFilter)

  // Sort products based on selected option
  const sortedProducts = sortProductsByOption(filteredProducts, sortBy)

  const totalPages = Math.ceil(sortedProducts.length / ITEMS_PER_PAGE)
  const startItem = (currentPage - 1) * ITEMS_PER_PAGE
  const endItem = Math.min(currentPage * ITEMS_PER_PAGE, sortedProducts.length)
  const paginatedProducts = sortedProducts.slice(startItem, endItem)

  const sortOptions = ["Recommended", "Price: Low to High", "Price: High to Low", "Newest"]

  const handleAddToCart = (e: React.MouseEvent, productId: number) => {
    e.stopPropagation()
    if (onAddToCart) {
      onAddToCart(productId)
    }
  }

  const handleProductClick = (product: FeaturedProduct) => {
    if (onProductClick) {
      onProductClick(product)
    }
  }

  // Generate page numbers to display
  const getPageNumbers = () => {
    const pages: (number | string)[] = []
    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) pages.push(i)
    } else {
      if (currentPage <= 4) {
        pages.push(1, 2, 3, 4, 5, '...', totalPages)
      } else if (currentPage >= totalPages - 3) {
        pages.push(1, '...', totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1, totalPages)
      } else {
        pages.push(1, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages)
      }
    }
    return pages
  }

  if (isLoading) {
    return (
      <section className="py-8">
        <div className="container mx-auto px-4">
          <LoadingState message="Loading products..." className="py-20" />
        </div>
      </section>
    )
  }

  if (isError) {
    return (
      <section className="py-8">
        <div className="container mx-auto px-4">
          <ErrorState
            message={getApiErrorPolicy(error).message}
            onRetry={() => { refetch(); }}
            className="py-20"
          />
        </div>
      </section>
    )
  }

  return (
    <section className="py-4">
      <div className="container mx-auto px-4">
        {/* Header with count and sort */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-baseline gap-2">
            <h1 className="text-lg font-semibold text-foreground">
              {categoryFilter ? `${categoryFilter}` : "Products"}
            </h1>
            <span className="text-sm text-muted-foreground">{filteredProducts.length} products</span>
          </div>

          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" className="bg-transparent">
                Sort: {sortBy}
                <ChevronDown className="w-4 h-4 ml-2" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="bg-[#fdf6e3] border-2 border-[#93a1a1]">
              {sortOptions.map((option) => (
                <DropdownMenuItem
                  key={option}
                  onClick={() => {
                    setSortBy(option)
                    setCurrentPage(1) // Reset to first page when sorting changes
                  }}
                  className={sortBy === option ? "bg-[#eee8d5] text-[#073642]" : "text-[#073642] hover:bg-[#eee8d5] hover:text-[#073642]"}
                >
                  {option}
                </DropdownMenuItem>
              ))}
            </DropdownMenuContent>

          </DropdownMenu>
        </div>

        {/* Product Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          {paginatedProducts.map((product) => (
            <div
              key={product.id}
              className="bg-[#eee8d5] rounded-xl border-2 border-[#93a1a1] p-0 overflow-hidden hover:shadow-lg transition-all duration-300 group cursor-pointer hover:border-[#268bd2] hover:-translate-y-1"
              onClick={() => handleProductClick(product)}
            >
              {/* Image Placeholder */}
              <div className="aspect-square bg-[#fdf6e3] flex items-center justify-center relative overflow-hidden border-b-2 border-[#93a1a1]">
                <div className="absolute inset-0 bg-gradient-to-tr from-[#268bd2]/10 to-transparent opacity-0 group-hover:opacity-100 transition-opacity z-10" />

                {getCategoryImage(product.categoryName) ? (
                  <img
                    src={getCategoryImage(product.categoryName)!}
                    alt={product.name}
                    className="w-full h-full object-contain p-4 mix-blend-multiply dark:mix-blend-normal"
                  />
                ) : (
                  <span className="text-4xl">🎵</span>
                )}

                {product.categoryName && (
                  <Badge className="absolute top-2 left-2 bg-[#b58900] text-[#fdf6e3] border-[#073642] shadow-sm">
                    {product.categoryName}
                  </Badge>
                )}
              </div>

              {/* Content */}
              <div className="p-5">
                <h3 className="font-bold text-[#073642] text-lg mb-1 group-hover:text-[#268bd2] transition-colors leading-tight">
                  {product.name}
                </h3>
                <p className="text-sm text-[#586e75] mb-4 line-clamp-2 font-medium">
                  {product.description || 'No description available'}
                </p>

                <div className="flex items-center justify-between mt-auto">
                  <span className="font-extrabold text-xl text-[#268bd2]">
                    {product.price ? `${product.price.toFixed(2)} EUR` : 'Price on request'}
                  </span>
                  {!isAdmin && (
                    <Button
                      size="sm"
                      className="bg-[#268bd2] text-[#fdf6e3] hover:bg-[#2aa198] shadow-sm border-2 border-[#073642]"
                      onClick={(e) => handleAddToCart(e, product.id)}
                    >
                      <ShoppingCart className="w-4 h-4" />
                    </Button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>

        {products.length === 0 ? (
          <div className="text-center py-20">
            <p className="text-muted-foreground">No products available.</p>
          </div>
        ) : (
          <>


            {/* Pagination */}
            {totalPages > 1 && (
              <div className="flex items-center justify-between mt-6 pt-4 border-t border-border">
                <p className="text-sm text-muted-foreground">
                  {startItem + 1}-{endItem} of {filteredProducts.length} products
                </p>
                <div className="flex items-center gap-1">
                  <Button
                    variant="outline"
                    size="icon"
                    className="w-8 h-8 bg-transparent"
                    disabled={currentPage === 1}
                    onClick={() => setCurrentPage(p => p - 1)}
                  >
                    <ChevronLeft className="w-4 h-4" />
                  </Button>
                  {getPageNumbers().map((page, index) => (
                    typeof page === 'number' ? (
                      <button
                        key={index}
                        type="button"
                        onClick={() => setCurrentPage(page)}
                        className={`w-8 h-8 rounded text-sm font-medium transition-colors ${page === currentPage
                          ? "bg-primary text-primary-foreground"
                          : "hover:bg-muted text-foreground"
                          }`}
                      >
                        {page}
                      </button>
                    ) : (
                      <span key={index} className="w-8 h-8 flex items-center justify-center text-muted-foreground">
                        {page}
                      </span>
                    )
                  ))}
                  <Button
                    variant="outline"
                    size="icon"
                    className="w-8 h-8 bg-transparent"
                    disabled={currentPage === totalPages}
                    onClick={() => setCurrentPage(p => p + 1)}
                  >
                    <ChevronRight className="w-4 h-4" />
                  </Button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </section>
  )
}
