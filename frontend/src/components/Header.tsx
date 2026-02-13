import { Link } from "react-router-dom"
import { User, Heart, ShoppingCart, Search } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"

import { useCart } from "@/context/CartContext"
import { Notifications } from "./Notifications"

// ... imports

interface HeaderProps {
  isAuthenticated?: boolean
  isAdmin?: boolean
  onLogout?: () => void
}

export function Header({ isAuthenticated, isAdmin, onLogout }: HeaderProps) {
  const { cartTotalItems } = useCart();

  return (
    <header className="sticky top-0 z-50 bg-gradient-to-r from-[#1a1225] via-[#2d1b4e] to-[#1a1225]">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between gap-4 h-16">
          {/* Logo */}
          <Link to="/" className="flex-shrink-0 text-white font-[family-name:var(--font-display)] text-2xl tracking-wider italic">
            NTO MUSIC
          </Link>

          {/* Search Bar - Centered */}
          <div className="hidden sm:flex flex-1 justify-center max-w-md">
            <div className="relative w-full">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                type="search"
                placeholder="Search instruments, gear, brands..."
                className="w-full h-10 pl-10 pr-4 rounded-full bg-white/10 border-white/20 text-white placeholder:text-white/50 focus:bg-white/15 focus:border-white/40"
              />
            </div>
          </div>

          {/* Actions */}
          <div className="flex items-center gap-1">
            {isAuthenticated && (
              <>
                <Notifications isAuthenticated={isAuthenticated} />

                <Button variant="ghost" size="sm" className="text-white/80 hover:text-white hover:bg-white/10" asChild>
                  <Link to="/user-profile">
                    <User className="w-5 h-5" />
                    <span className="sr-only">Account</span>
                  </Link>
                </Button>
                <Button variant="ghost" size="sm" className="text-white/80 hover:text-white hover:bg-white/10">
                  <Heart className="w-5 h-5" />
                  <span className="sr-only">Favorites</span>
                </Button>
                {!isAdmin && (
                  <Button variant="ghost" size="sm" className="text-white/80 hover:text-white hover:bg-white/10 relative" asChild>
                    <Link to="/cart">
                      <ShoppingCart className="w-5 h-5" />
                      {cartTotalItems > 0 && (
                        <span className="absolute -top-1 -right-1 w-4 h-4 bg-secondary text-secondary-foreground text-xs rounded-full flex items-center justify-center">
                          {cartTotalItems}
                        </span>
                      )}
                      <span className="sr-only">Cart</span>
                    </Link>
                  </Button>
                )}
                {isAdmin && (
                  <Button variant="ghost" size="sm" className="text-white/80 hover:text-white hover:bg-white/10" asChild>
                    <Link to="/admin">Admin</Link>
                  </Button>
                )}
                <Button
                  variant="ghost"
                  size="sm"
                  className="text-white/80 hover:text-white hover:bg-white/10"
                  onClick={onLogout}
                  asChild
                >
                  <Link to="/login">Log Out</Link>
                </Button>
              </>
            )}
            {!isAuthenticated && (
              <Button variant="ghost" size="sm" className="text-white/80 hover:text-white hover:bg-white/10" asChild>
                <Link to="/login">Log In</Link>
              </Button>
            )}
          </div>
        </div>
      </div>
    </header>
  )
}
