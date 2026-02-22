import { FormEvent } from 'react';
import { Link } from 'react-router-dom';
import { ShoppingCart, Search, ChevronDown } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Notifications } from '@/components/Notifications';

interface HeaderViewProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  cartTotalItems: number;
  displayName: string;
  fullName: string;
  onLogout: () => void;
  showSearch: boolean;
  searchTerm: string;
  onSearchTermChange: (value: string) => void;
  onSearchSubmit: () => void;
}

export function HeaderView({
  isAuthenticated,
  isAdmin,
  cartTotalItems,
  displayName,
  fullName,
  onLogout,
  showSearch,
  searchTerm,
  onSearchTermChange,
  onSearchSubmit,
}: HeaderViewProps) {
  const submitSearch = (event: FormEvent) => {
    event.preventDefault();
    onSearchSubmit();
  };

  return (
    <header className="sticky top-0 z-50 bg-[#002b36] border-b border-[#073642]">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between gap-4 h-16">
          <Link
            to="/"
            className="flex-shrink-0 text-[#fdf6e3] font-[family-name:var(--font-display)] text-2xl tracking-wider"
          >
            NTO MUSIC
          </Link>

          {showSearch && (
            <form onSubmit={submitSearch} className="hidden sm:flex flex-1 justify-center max-w-md gap-2">
              <div className="relative w-full">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#586e75]" />
                <Input
                  type="search"
                  placeholder="Search instruments, gear, brands..."
                  value={searchTerm}
                  onChange={(event) => onSearchTermChange(event.target.value)}
                  className="w-full h-10 pl-10 pr-4 bg-[#073642] border-[#073642] text-[#93a1a1] placeholder:text-[#586e75] focus:bg-[#073642] focus:border-[#268bd2] focus:ring-1 focus:ring-[#268bd2]"
                />
              </div>
              <Button type="submit" size="sm" className="h-10 px-4 bg-[#073642] hover:bg-[#0b4f60] text-[#fdf6e3]">
                Search
              </Button>
            </form>
          )}
          {!showSearch && <div className="hidden sm:block flex-1" />}

          <div className="flex items-center gap-1">
            {isAuthenticated && (
              <>
                <Notifications isAuthenticated={isAuthenticated} />

                {!isAdmin && (
                  <Button
                    variant="ghost"
                    size="sm"
                    className="text-[#93a1a1] hover:text-[#fdf6e3] hover:bg-[#073642] relative"
                    asChild
                  >
                    <Link to="/cart">
                      <ShoppingCart className="w-5 h-5" />
                      {cartTotalItems > 0 && (
                        <span className="absolute -top-1 -right-1 w-5 h-5 bg-[#cb4b16] text-[#fdf6e3] text-xs rounded-full flex items-center justify-center font-semibold">
                          {cartTotalItems}
                        </span>
                      )}
                      <span className="sr-only">Cart</span>
                    </Link>
                  </Button>
                )}

                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="text-[#93a1a1] hover:text-[#fdf6e3] hover:bg-[#073642] gap-2"
                    >
                      <span className="text-sm font-medium">{displayName}</span>
                      <ChevronDown className="w-4 h-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent
                    align="end"
                    className="w-56 bg-[#002b36] border-[#073642] text-[#93a1a1]"
                  >
                    <DropdownMenuLabel className="text-[#fdf6e3]">{fullName}</DropdownMenuLabel>
                    <DropdownMenuSeparator className="bg-[#073642]" />
                    {isAdmin && (
                      <DropdownMenuItem
                        asChild
                        className="focus:bg-[#073642] focus:text-[#fdf6e3] cursor-pointer"
                      >
                        <Link to="/admin">Admin Dashboard</Link>
                      </DropdownMenuItem>
                    )}
                    <DropdownMenuItem
                      asChild
                      className="focus:bg-[#073642] focus:text-[#fdf6e3] cursor-pointer"
                    >
                      <Link to="/user-profile">Edit Profile</Link>
                    </DropdownMenuItem>
                    <DropdownMenuSeparator className="bg-[#073642]" />
                    <DropdownMenuItem
                      onClick={onLogout}
                      className="focus:bg-[#073642] focus:text-[#cb4b16] text-[#cb4b16] cursor-pointer"
                    >
                      Log Out
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </>
            )}
            {!isAuthenticated && (
              <Button
                variant="ghost"
                size="sm"
                className="text-[#93a1a1] hover:text-[#fdf6e3] hover:bg-[#073642]"
                asChild
              >
                <Link to="/login">Log In</Link>
              </Button>
            )}
          </div>
        </div>
        {showSearch && (
          <form onSubmit={submitSearch} className="sm:hidden pb-3 flex items-center gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#586e75]" />
              <Input
                type="search"
                placeholder="Search gear..."
                value={searchTerm}
                onChange={(event) => onSearchTermChange(event.target.value)}
                className="w-full h-10 pl-10 pr-4 bg-[#073642] border-[#073642] text-[#93a1a1] placeholder:text-[#586e75] focus:bg-[#073642] focus:border-[#268bd2] focus:ring-1 focus:ring-[#268bd2]"
              />
            </div>
            <Button type="submit" size="sm" className="h-10 px-4 bg-[#073642] hover:bg-[#0b4f60] text-[#fdf6e3]">
              Search
            </Button>
          </form>
        )}
      </div>
    </header>
  );
}
