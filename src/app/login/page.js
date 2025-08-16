import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import Link from "next/link";

export default function Login() {
  return (
    <div className="flex min-h-screen bg-gradient-to-r from-orange-50 via-pink-50 to-purple-50">
      {/* LEFT FORM */}
      <div className="flex-1 flex justify-center items-center">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle className="text-center">Welcome Back</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-center text-pink-600 mb-4">Login to your account</p>
            <form className="flex flex-col gap-4">
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" placeholder="Email" />
              <Label htmlFor="password">Password</Label>
              <Input id="password" type="password" placeholder="Password" />
              <Button type="submit" className="w-full mt-2">Login</Button>
            </form>
            <p className="text-center mt-4 text-sm">
              Don't have an account?{' '}
              <Link href="/signup" className="text-pink-500 hover:underline">Sign up</Link>
            </p>
          </CardContent>
        </Card>
      </div>
      {/* RIGHT IMAGE */}
      <div className="flex-1 hidden md:flex bg-purple-100 items-center justify-center">
        <img src="https://picsum.photos/800/900" alt="Books" className="w-full h-full object-cover rounded-xl" />
      </div>
    </div>
  );
}
