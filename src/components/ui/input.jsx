import * as React from "react"

import { cn } from "@/lib/utils"

function Input({
  className,
  type,
  ...props
}) {
  return (
    (<input
      type={type}
      data-slot="input"
      className={cn(
        "file:text-purple-700 placeholder:text-pink-400 selection:bg-orange-300 selection:text-white dark:bg-input/30 border-2 border-purple-300 flex h-9 w-full min-w-0 rounded-md bg-gradient-to-r from-orange-50 via-pink-50 to-purple-50 px-3 py-1 text-base shadow-md transition-[color,box-shadow] outline-none file:inline-flex file:h-7 file:border-0 file:bg-transparent file:text-sm file:font-medium disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50 md:text-sm",
        "focus-visible:border-pink-400 focus-visible:ring-pink-200 focus-visible:ring-[3px]",
        "aria-invalid:ring-red-400 dark:aria-invalid:ring-red-600 aria-invalid:border-red-400",
        className
      )}
      {...props} />)
  );
}

export { Input }
