import { Link, useLocation } from "react-router-dom"
import { Truck, ShieldCheck, RotateCcw, CreditCard, ArrowRight, Zap, DollarSign, Users } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useMemo } from "react"
import { useCategories } from '@/hooks/useApi';
import { mapCategoryCounts, NavCategory } from '@/lib/productView';

const CATEGORIES: NavCategory[] = [
  { name: "Guitars & Plucked", slug: "guitars-plucked", href: "/?category=guitars-plucked" },
  { name: "Drums & Percussion", slug: "drums-percussion", href: "/?category=drums-percussion" },
  { name: "Keys & Synths", slug: "keys-synths", href: "/?category=keys-synths" },
  { name: "Wind & Brass", slug: "wind-brass", href: "/?category=wind-brass" },
  { name: "Bowed Strings", slug: "bowed-strings", href: "/?category=bowed-strings" },
]

export function Hero() {
  const { data: categoriesData = [] } = useCategories();
  const categories = useMemo(
    () => mapCategoryCounts(CATEGORIES, categoriesData),
    [categoriesData],
  );
  const location = useLocation();
  const selectedCategory = useMemo(
    () => new URLSearchParams(location.search).get('category'),
    [location.search],
  );

  return (
    <>
      {/* Hero Banner - Solid Solarized Colors */}
      <div className="relative overflow-hidden bg-[#073642]">
        <div className="relative container mx-auto px-4 py-12 lg:py-16">
          <div className="grid lg:grid-cols-2 gap-10 lg:gap-16 items-stretch">
            {/* Left - Buy Side */}
            <div className="flex flex-col justify-between">
              <h1 className="font-[family-name:var(--font-display)] text-5xl sm:text-6xl lg:text-7xl text-[#fdf6e3] leading-none mb-4 tracking-tight">
                GET INTO MUSIC
              </h1>
              <p className="text-[#93a1a1] text-lg mb-8 leading-relaxed font-medium">
                By buying quality gear from real musicians. Every instrument on NTO Music is tested equipment that's been loved by people who know music. No markup, no pressure, just fair prices that support the community.
              </p>

              {/* Trust Badges Grid */}
              <div className="grid grid-cols-2 gap-4">
                <div className="flex items-start gap-3 bg-[#002b36] rounded-lg px-4 py-3 border border-[#073642]">
                  <Truck className="w-5 h-5 text-[#cb4b16] flex-shrink-0 mt-0.5" />
                  <div>
                    <p className="text-[#fdf6e3] text-sm font-semibold">Free Shipping</p>
                    <p className="text-[#839496] text-xs">100+ EUR orders</p>
                  </div>
                </div>
                <div className="flex items-start gap-3 bg-[#002b36] rounded-lg px-4 py-3 border border-[#073642]">
                  <ShieldCheck className="w-5 h-5 text-[#cb4b16] flex-shrink-0 mt-0.5" />
                  <div>
                    <p className="text-[#fdf6e3] text-sm font-semibold">1-Year Warranty</p>
                    <p className="text-[#839496] text-xs">Full coverage</p>
                  </div>
                </div>
                <div className="flex items-start gap-3 bg-[#002b36] rounded-lg px-4 py-3 border border-[#073642]">
                  <RotateCcw className="w-5 h-5 text-[#cb4b16] flex-shrink-0 mt-0.5" />
                  <div>
                    <p className="text-[#fdf6e3] text-sm font-semibold">14-Day Returns</p>
                    <p className="text-[#839496] text-xs">Risk-free buying</p>
                  </div>
                </div>
                <div className="flex items-start gap-3 bg-[#002b36] rounded-lg px-4 py-3 border border-[#073642]">
                  <CreditCard className="w-5 h-5 text-[#cb4b16] flex-shrink-0 mt-0.5" />
                  <div>
                    <p className="text-[#fdf6e3] text-sm font-semibold">Payment Plans</p>
                    <p className="text-[#839496] text-xs">0% interest</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Right - Sell Side */}
            <div className="bg-[#002b36] rounded-2xl p-8 border border-[#073642] flex flex-col justify-between">
              <div>
                <h2 className="font-[family-name:var(--font-display)] text-3xl lg:text-4xl text-[#fdf6e3] mb-4 tracking-tight">
                  GOT GEAR TO SELL?
                </h2>
                <p className="text-[#93a1a1] text-base leading-relaxed mb-6 font-medium">
                  We handle everything. You list, we verify, they buy. Help someone else get into music while earning fair cash.
                </p>

                {/* Why sell here */}
                <div className="space-y-4 mb-8">
                  <div className="flex items-start gap-3">
                    <div className="w-10 h-10 rounded-full bg-[#073642] flex items-center justify-center flex-shrink-0 border border-[#586e75]">
                      <Zap className="w-5 h-5 text-[#b58900]" />
                    </div>
                    <div>
                      <p className="text-[#fdf6e3] font-semibold text-sm">Easy listing</p>
                      <p className="text-[#839496] text-xs">Tell us the brand, condition, extras and we handle the rest.</p>
                    </div>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="w-10 h-10 rounded-full bg-[#073642] flex items-center justify-center flex-shrink-0 border border-[#586e75]">
                      <DollarSign className="w-5 h-5 text-[#b58900]" />
                    </div>
                    <div>
                      <p className="text-[#fdf6e3] font-semibold text-sm">Not sure about the offer?</p>
                      <p className="text-[#839496] text-xs">We keep your instrument safe and can return it at no extra cost if you pass.</p>
                    </div>
                  </div>
                  <div className="flex items-start gap-3">
                    <div className="w-10 h-10 rounded-full bg-[#073642] flex items-center justify-center flex-shrink-0 border border-[#586e75]">
                      <Users className="w-5 h-5 text-[#b58900]" />
                    </div>
                    <div>
                      <p className="text-[#fdf6e3] font-semibold text-sm">Real music community</p>
                      <p className="text-[#839496] text-xs">Buyers who actually care about your gear</p>
                    </div>
                  </div>
                </div>
              </div>

              <Button
                asChild
                size="lg"
                className="w-full bg-[#b58900] text-[#fdf6e3] hover:bg-[#cb4b16] font-bold shadow-lg text-base h-12"
              >
                <Link to="/sell">
                  Sell Your Gear
                  <ArrowRight className="w-5 h-5 ml-2" />
                </Link>
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Categories */}
      <div className="bg-[#eee8d5] border-b border-[#93a1a1]">
        <div className="container mx-auto px-4">
          <nav className="flex items-center justify-start lg:justify-center gap-2 py-3 overflow-x-auto">
            {categories.map((cat) => {
                const isActive = selectedCategory === cat.slug;
                return (
                  <Link
                    key={cat.slug}
                    to={cat.href}
                    className={`flex items-center gap-2 px-4 py-2 text-sm font-semibold transition-colors whitespace-nowrap rounded-md border ${isActive ? 'text-[#268bd2] bg-[#fdf6e3] border-[#93a1a1]' : 'text-[#073642] border-transparent hover:text-[#268bd2] hover:bg-[#fdf6e3] hover:border-[#93a1a1]'}`}
                  >
                    {cat.name}
                    <span className="text-xs text-[#586e75] bg-[#fdf6e3] px-2 py-0.5 rounded-full font-bold border border-[#93a1a1]">
                      {cat.count}
                    </span>
                  </Link>
                );
              })}
          </nav>
        </div>
      </div>
    </>
  )
}
