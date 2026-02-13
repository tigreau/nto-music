import { Link } from "react-router-dom"
import { Facebook, Instagram, Youtube } from "lucide-react"

export function Footer() {
  return (
    <footer className="bg-[#002b36] text-white">
      <div className="container mx-auto px-4 py-10">
        <div className="flex flex-col md:flex-row md:items-start justify-between gap-8">
          {/* Brand */}
          <div className="max-w-xs">
            <Link to="/" className="font-[family-name:var(--font-display)] text-xl tracking-wider italic">
              NTO MUSIC
            </Link>
            <p className="text-white/50 text-sm mt-3">
              Quality instruments at affordable prices.
            </p>
            <div className="flex gap-4 mt-4">
              <a href="#" className="text-white/50 hover:text-white transition-colors" aria-label="Facebook">
                <Facebook className="w-4 h-4" />
              </a>
              <a href="#" className="text-white/50 hover:text-white transition-colors" aria-label="Instagram">
                <Instagram className="w-4 h-4" />
              </a>
              <a href="#" className="text-white/50 hover:text-white transition-colors" aria-label="YouTube">
                <Youtube className="w-4 h-4" />
              </a>
            </div>
          </div>

          {/* Links */}
          <div className="flex flex-wrap gap-x-12 gap-y-6 text-sm">
            <div>
              <h4 className="font-medium mb-3">Shop</h4>
              <ul className="space-y-2 text-white/50">
                <li><Link to="/" className="hover:text-white transition-colors">All Products</Link></li>
                <li><Link to="/" className="hover:text-white transition-colors">Guitars</Link></li>
                <li><Link to="/" className="hover:text-white transition-colors">Drums</Link></li>
                <li><Link to="/" className="hover:text-white transition-colors">Keys</Link></li>
              </ul>
            </div>
            <div>
              <h4 className="font-medium mb-3">Support</h4>
              <ul className="space-y-2 text-white/50">
                <li><a href="#" className="hover:text-white transition-colors">Help Center</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Shipping</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Returns</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-medium mb-3">Company</h4>
              <ul className="space-y-2 text-white/50">
                <li><a href="#" className="hover:text-white transition-colors">About</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Privacy</a></li>
                <li><a href="#" className="hover:text-white transition-colors">Terms</a></li>
              </ul>
            </div>
          </div>
        </div>

        <div className="border-t border-white/10 mt-8 pt-6 text-center text-xs text-white/30">
          <p>&copy; {new Date().getFullYear()} NTO Music. All rights reserved.</p>
        </div>
      </div>
    </footer>
  )
}
