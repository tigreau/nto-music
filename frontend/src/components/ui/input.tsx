import * as React from "react"

import { cn } from "@/lib/utils"

const Input = React.forwardRef<HTMLInputElement, React.ComponentProps<"input">>(
  ({ className, type, ...props }, ref) => {
    return (
      <input
        type={type}
        className={cn(
          "flex h-9 w-full rounded-md border border-[#93a1a1] bg-[#eee8d5] px-3 py-1 text-base font-medium text-[#073642] shadow-sm transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-[#073642] placeholder:text-[#93a1a1] placeholder:font-normal focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#268bd2] focus-visible:border-[#268bd2] disabled:cursor-not-allowed disabled:opacity-50 md:text-base",
          className
        )}
        ref={ref}
        {...props}
      />
    )
  }
)
Input.displayName = "Input"

export { Input }
