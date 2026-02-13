import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"

import { cn } from "@/lib/utils"

const badgeVariants = cva(
  "inline-flex items-center rounded-md border-2 px-2.5 py-0.5 text-xs font-bold transition-colors focus:outline-none focus:ring-2 focus:ring-[#268bd2] focus:ring-offset-2",
  {
    variants: {
      variant: {
        default:
          "border-[#073642] bg-[#268bd2] text-[#fdf6e3] shadow hover:bg-[#2aa198]",
        secondary:
          "border-[#073642] bg-[#cb4b16] text-[#fdf6e3] hover:bg-[#b58900]",
        destructive:
          "border-[#073642] bg-[#dc322f] text-[#fdf6e3] shadow hover:bg-[#cb4b16]",
        outline: "text-[#073642] border-[#93a1a1] bg-[#fdf6e3]",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
)

export interface BadgeProps
  extends React.HTMLAttributes<HTMLDivElement>,
  VariantProps<typeof badgeVariants> { }

function Badge({ className, variant, ...props }: BadgeProps) {
  return (
    <div className={cn(badgeVariants({ variant }), className)} {...props} />
  )
}

export { Badge, badgeVariants }
