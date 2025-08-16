import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export default function AddBookPage() {
  return (
    <div className="container mx-auto py-8 flex justify-center">
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle>Add a New Book</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="title">Title</Label>
              <Input id="title" placeholder="Enter book title" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="author">Author</Label>
              <Input id="author" placeholder="Enter author's name" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="category">Category</Label>
              <Input id="category" placeholder="Enter book category" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="cover-image">Cover Image URL</Label>
              <Input id="cover-image" placeholder="Enter cover image URL" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="pdf-file">Book PDF</Label>
              <Input id="pdf-file" type="file" />
            </div>
            <Button type="submit" className="w-full">Add Book</Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}