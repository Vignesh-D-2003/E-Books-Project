import * as React from "react"
import { Slot } from "@radix-ui/react-slot"
import { cva } from "class-variance-authority";

import { cn } from "@/lib/utils"

const buttonVariants = cva(
  "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive",
  {
    variants: {
      variant: {
        default:
          "bg-gradient-to-r from-orange-400 via-pink-500 to-purple-500 text-white shadow-lg hover:from-orange-500 hover:via-pink-600 hover:to-purple-600",
        destructive:
          "bg-gradient-to-r from-red-500 via-orange-500 to-yellow-500 text-white shadow-lg hover:from-red-600 hover:via-orange-600 hover:to-yellow-600 focus-visible:ring-destructive/20 dark:focus-visible:ring-destructive/40 dark:bg-destructive/60",
        outline:
          "border-2 border-purple-500 bg-white text-purple-700 shadow-md hover:bg-purple-50 hover:text-purple-900 dark:bg-input/30 dark:border-input dark:hover:bg-input/50",
        secondary:
          "bg-gradient-to-r from-green-400 via-blue-400 to-indigo-400 text-white shadow-lg hover:from-green-500 hover:via-blue-500 hover:to-indigo-500",
        ghost:
          "bg-transparent text-pink-500 hover:bg-pink-50 hover:text-pink-700 dark:hover:bg-pink-100/50",
        link: "text-blue-600 underline-offset-4 hover:underline hover:text-blue-800",
      },
      size: {
        default: "h-9 px-4 py-2 has-[>svg]:px-3",
        sm: "h-8 rounded-md gap-1.5 px-3 has-[>svg]:px-2.5",
        lg: "h-10 rounded-md px-6 has-[>svg]:px-4",
        icon: "size-9",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  }
)

function Button({
  className,
  variant,
  size,
  asChild = false,
  ...props
}) {
  const Comp = asChild ? Slot : "button"

  return (
    (<Comp
      data-slot="button"
      className={cn(buttonVariants({ variant, size, className }))}
      {...props} />)
  );
}

export { Button, buttonVariants }
