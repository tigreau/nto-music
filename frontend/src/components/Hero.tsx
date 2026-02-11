import { Link } from "react-router-dom"
import { Truck, ShieldCheck, RotateCcw, CreditCard, ArrowRight, Zap, DollarSign, Users } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useEffect, useState } from "react"
import { Category } from "@/types"

const CATEGORIES = [
  { name: "Guitars & Plucked", slug: "guitars-plucked", href: "/?category=guitars-plucked" },
  { name: "Drums & Percussion", slug: "drums-percussion", href: "/?category=drums-percussion" },
  { name: "Keys & Synths", slug: "keys-synths", href: "/?category=keys-synths" },
  { name: "Wind & Brass", slug: "wind-brass", href: "/?category=wind-brass" },
  { name: "Bowed Strings", slug: "bowed-strings", href: "/?category=bowed-strings" },
]

export function Hero() {
  const [categories, setCategories] = useState(CATEGORIES.map(c => ({ ...c, count: 0 })));

  useEffect(() => {
    fetch('/api/categories')
      .then(res => res.json())
      .then((data: Category[]) => {
        const updated = CATEGORIES.map(fixed => {
          const match = data.find(d => d.slug === fixed.slug);
          return { ...fixed, count: match ? match.productCount : 0 };
        });
        setCategories(updated);
      })
      .catch(err => console.error("Failed to fetch categories", err));
  }, []);

  return (
    <>
      {/* Colorful Hero Banner */}
      <div className="relative overflow-hidden">
        {/* Split background */}
        <div className="absolute inset-0 bg-gradient-to-br from-[#7B1FA2] via-[#9C27B0] to-[#E91E63]" />
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHZpZXdCb3g9IjAgMCA2MCA2MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxnIGZpbGw9IiNmZmZmZmYiIGZpbGwtb3BhY2l0eT0iMC4wNSI+PGNpcmNsZSBjeD0iMzAiIGN5PSIzMCIgcj0iMiIvPjwvZz48L2c+PC9zdmc+')] opacity-50" />

        <div className="relative container mx-auto px-4 py-10 lg:py-12">
          <div className="grid lg:grid-cols-2 gap-10 lg:gap-16 items-stretch">
            {/* Left - Buy Side */}
            <div className="flex flex-col justify-between">
              <h1 className="font-[family-name:var(--font-display)] text-4xl sm:text-5xl lg:text-6xl text-white leading-tight mb-3">
                GET INTO MUSIC
              </h1>
              <p className="text-white/90 text-lg mb-6 leading-relaxed">
                By buying quality gear from real musicians. Every instrument on NTO Music is tested equipment that's been loved by people who know music. No markup, no pressure, just fair prices that support the community.
              </p>

              {/* Trust Badges Grid */}
              <div className="grid grid-cols-2 gap-3">
                <div className="flex items-center gap-2 bg-white/10 rounded-lg px-3 py-2">
                  <Truck className="w-4 h-4 text-secondary flex-shrink-0" />
                  <div>
                    <p className="text-white text-xs font-medium">Free Shipping</p>
                    <p className="text-white/60 text-xs">100+ EUR orders</p>
                  </div>
                </div>
                <div className="flex items-center gap-2 bg-white/10 rounded-lg px-3 py-2">
                  <ShieldCheck className="w-4 h-4 text-secondary flex-shrink-0" />
                  <div>
                    <p className="text-white text-xs font-medium">2-Year Warranty</p>
                    <p className="text-white/60 text-xs">Full coverage</p>
                  </div>
                </div>
                <div className="flex items-center gap-2 bg-white/10 rounded-lg px-3 py-2">
                  <RotateCcw className="w-4 h-4 text-secondary flex-shrink-0" />
                  <div>
                    <p className="text-white text-xs font-medium">30-Day Returns</p>
                    <p className="text-white/60 text-xs">Risk-free buying</p>
                  </div>
                </div>
                <div className="flex items-center gap-2 bg-white/10 rounded-lg px-3 py-2">
                  <CreditCard className="w-4 h-4 text-secondary flex-shrink-0" />
                  <div>
                    <p className="text-white text-xs font-medium">Payment Plans</p>
                    <p className="text-white/60 text-xs">0% interest</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Right - Sell Side */}
            <div className="bg-white/10 backdrop-blur-md rounded-2xl p-6 lg:p-8 border border-white/20 flex flex-col justify-between">
              <div>
                <h2 className="font-[family-name:var(--font-display)] text-2xl lg:text-3xl text-white mb-4">
                  GOT GEAR TO SELL?
                </h2>
                <p className="text-white/80 text-base leading-relaxed mb-6">
                  We handle everything. You list, we verify, they buy. Help someone else get into music while earning fair cash. Keep 95% of your sale price, that's 5x better than what pawn shops offer.
                </p>

                {/* Why sell here */}
                <div className="space-y-3 mb-6">
                  <div className="flex items-start gap-3">
                    <div className="w-8 h-8 rounded-full bg-secondary/20 flex items-center justify-center flex-shrink-0">
                      <Zap className="w-4 h-4 text-secondary" />
                    </div>
                    <div>
                      <p className="text-white font-medium text-sm">List in 2 minutes</p>
                      <p className="text-white/60 text-xs">No complicated forms or auction setup</p>
                    </div>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="w-8 h-8 rounded-full bg-secondary/20 flex items-center justify-center flex-shrink-0">
                      <DollarSign className="w-4 h-4 text-secondary" />
                    </div>
                    <div>
                      <p className="text-white font-medium text-sm">Only 5% fee</p>
                      <p className="text-white/60 text-xs">Keep more of what you earn (eBay takes 13%+)</p>
                    </div>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="w-8 h-8 rounded-full bg-secondary/20 flex items-center justify-center flex-shrink-0">
                      <Users className="w-4 h-4 text-secondary" />
                    </div>
                    <div>
                      <p className="text-white font-medium text-sm">Real music community</p>
                      <p className="text-white/60 text-xs">Buyers who actually care about your gear</p>
                    </div>
                  </div>
                </div>
              </div>

              <Button
                size="lg"
                className="w-full bg-secondary text-secondary-foreground hover:bg-secondary/90 font-semibold shadow-lg"
              >
                Sell Your Gear
                <ArrowRight className="w-4 h-4 ml-2" />
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Categories */}
      <div className="bg-card border-b border-border">
        <div className="container mx-auto px-4">
          <nav className="flex items-center justify-start lg:justify-center gap-1 py-2.5 overflow-x-auto">
            {categories.map((cat) => (
              <Link
                key={cat.slug}
                to={cat.href}
                className="flex items-center gap-1.5 px-4 py-2 text-sm font-medium text-foreground hover:text-primary hover:bg-primary/5 rounded-full transition-colors whitespace-nowrap"
              >
                {cat.name}
                <span className="text-xs text-muted-foreground bg-muted px-1.5 py-0.5 rounded-full">
                  {cat.count}
                </span>
              </Link>
            ))}
          </nav>
        </div>
      </div>
    </>
  )
}
