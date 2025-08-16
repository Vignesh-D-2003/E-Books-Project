import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import Link from "next/link";

export default function Signup() {
  return (
    <div className="flex min-h-screen bg-gradient-to-r from-orange-50 via-pink-50 to-purple-50">
      {/* LEFT FORM */}
      <div className="flex-1 flex justify-center items-center">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle className="text-center">Create Account</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-center text-pink-600 mb-4">Join and start reading today</p>
            <form className="flex flex-col gap-4">
              <Label htmlFor="name">Full Name</Label>
              <Input id="name" type="text" placeholder="Full Name" />
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" placeholder="Email" />
              <Label htmlFor="password">Password</Label>
              <Input id="password" type="password" placeholder="Password" />
              <Label htmlFor="confirm">Confirm Password</Label>
              <Input id="confirm" type="password" placeholder="Confirm Password" />
              <Button type="submit" className="w-full mt-2">Sign Up</Button>
            </form>
            <p className="text-center mt-4 text-sm">
              Already have an account?{' '}
              <Link href="/login" className="text-pink-500 hover:underline">Login</Link>
            </p>
          </CardContent>
        </Card>
      </div>
      {/* RIGHT IMAGE */}
      <div className="flex-1 hidden md:flex bg-purple-100 items-center justify-center">
        <img src="https://picsum.photos/800/901" alt="Reading" className="w-full h-full object-cover rounded-xl" />
      </div>
    </div>
  );
}
