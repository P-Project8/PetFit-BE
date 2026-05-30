import { useState } from 'react';
import {
  Home,
  Search,
  Shirt,
  Heart,
  User,
  ChevronLeft,
  ChevronRight,
  ShoppingCart,
  Star,
  Check,
  X,
  Menu,
  Bell,
  Settings,
  Plus,
  Minus,
  Trash2,
  Edit2,
  Camera,
  Share2,
  Download,
  Info,
  ArrowRight,
  ArrowBigRight,
  CheckCircle2,
  AlertCircle,
  Truck,
  CreditCard,
  Gift
} from 'lucide-react';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import ProductCard from '../components/product/ProductCard';
import CategoryTabs from '../components/common/CategoryTabs';
import PageHeader from '../components/layout/PageHeader';
import { Navbar } from '../components/layout/Navbar';
import AiStylingBanner from '../components/banner/AiStylingBanner';
import { products } from '../data/products';
import { mockCategories } from '../data/mockCategories';

export default function StyleGuidePage() {
  const [activeTab, setActiveTab] = useState('new');

  // Sample products for display
  const sampleProduct1 = products[0];
  const sampleProduct2 = products.find((p) => p.discountRate > 0) || products[1];

  return (
    <div className="min-h-screen bg-white pb-244">
      <PageHeader title="Style Guide (UI Overview)" />

      <div className="pt-16 px-6 space-y-12">
        {/* Color Palette Section */}
        <section>
          <h2 className="text-xl font-bold mb-4 text-[#14314F]">1. Colors & Typography</h2>
          <div className="grid grid-cols-2 gap-4">
            <div className="bg-[#14314F] text-white p-4 rounded-lg">
              <p className="font-bold">기본</p>
              <p className="text-sm opacity-80">#14314F</p>
            </div>
            <div className="bg-gray-900 text-white p-4 rounded-lg">
              <p className="font-bold">텍스트 기본</p>
              <p className="text-sm opacity-80">gray-900</p>
            </div>
            <div className="bg-white text-gray-900 border border-gray-200 p-4 rounded-lg">
              <p className="font-bold">배경</p>
              <p className="text-sm text-gray-500">white / gray-50</p>
            </div>
            <div className="bg-red-600 text-white p-4 rounded-lg">
              <p className="font-bold">강조 / 에러</p>
              <p className="text-sm opacity-80">red-600</p>
            </div>
          </div>
        </section>

        {/* Buttons Section */}
        <section>
          <h2 className="text-xl font-bold mb-4 text-[#14314F]">2. Buttons</h2>
          <div className="space-y-4">
            <div className="space-y-2">
              <p className="text-sm text-gray-500">Default (Primary)</p>
              <Button className="w-full bg-[#14314F]">등록하기</Button>
            </div>
            <div className="space-y-2">
              <p className="text-sm text-gray-500">Secondary</p>
              <Button variant="secondary" className="w-full">
                등록하기
              </Button>
            </div>
            <div className="space-y-2">
              <p className="text-sm text-gray-500">Ghost</p>
              <Button variant="ghost" className="w-full">
                등록하기
              </Button>
            </div>
            <div className="space-y-2">
              <p className="text-sm text-gray-500">Disabled</p>
              <Button disabled className="w-full">
                등록하기
              </Button>
            </div>
          </div>
        </section>

        {/* Inputs Section */}
        <section>
          <h2 className="text-xl font-bold mb-4 text-[#14314F]">3. Input Fields</h2>
          <div className="space-y-4">
            <div className="space-y-2">
              <p className="text-sm text-gray-500">Default Input</p>
              <Input placeholder="입력해주세요." />
            </div>
            <div className="space-y-2">
              <p className="text-sm text-gray-500">Input with Value</p>
              <Input defaultValue="입력해주세요.." />
            </div>
            <div className="space-y-2">
              <p className="text-sm text-gray-500">Disabled Input</p>
              <Input disabled placeholder="입력해주세요.." />
            </div>
          </div>
        </section>

        {/* Tabs Section */}
        <section>
          <h2 className="text-xl font-bold mb-4 text-[#14314F]">4. Navigation Tabs</h2>
          <div className="border border-gray-100 p-4 rounded-xl bg-white shadow-sm">
            <CategoryTabs
              categories={mockCategories}
              activeCategory={activeTab}
              onCategoryChange={setActiveTab}
            />
          </div>
        </section>

        {/* Product Cards Section */}
        <section>
          <h2 className="text-xl font-bold mb-4 text-[#14314F]">5. Product Cards</h2>
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <p className="text-sm text-gray-500 mb-2">기본 상품</p>
              <ProductCard product={sampleProduct1 as unknown as Parameters<typeof ProductCard>[0]['product']} />
            </div>
            <div className="space-y-2">
              <p className="text-sm text-gray-500 mb-2">할인 상품</p>
              <ProductCard product={sampleProduct2 as unknown as Parameters<typeof ProductCard>[0]['product']} />
            </div>
          </div>
        </section>

        {/* Global Navigation Section */}
        <section>
          <h2 className="text-xl font-bold mb-8 text-[#14314F]">6. Headers & Navigation</h2>
          
          <div className="space-y-8">
            <div className="border border-gray-200 rounded-xl overflow-hidden bg-gray-50 h-32 relative">
              <p className="absolute top-2 left-2 text-xs text-gray-400 z-10">Main Header</p>
              <div className="absolute top-0 left-0 right-0">
                 {/* Position absolute to simulate fixed header behavior within container if we wanted, 
                     but Header component uses fixed positioning. We'll simply render it here, 
                     note that it might stick to the top of the viewport. 
                     For style guide documentation, we might just want to show the PageHeader variants 
                     or explain that the Main Header is fixed.
                 */}
                 {/* Since Header uses 'fixed', it will appear at the top of the screen. 
                     To display it 'in-flow' for docs is tricky without modifying the component.
                     Let's show PageHeader variants instead, and maybe a mock of the main header content.
                 */}
                 <div className="px-5 pt-4 bg-white/60 backdrop-blur-md shadow-sm h-12 flex items-center justify-between">
                    {/* Mocked Main Header for static display */}
                    <div className="flex items-center">
                       <span className="font-['KaKamora'] text-lg text-[#14314F]">PetFit</span>
                    </div>
                    <div className="w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center">
                       <span className="text-xs">Cart</span>
                    </div>
                 </div>
              </div>
            </div>

            <div className="space-y-2">
              <p className="text-sm text-gray-500">Page Header (With Back Button)</p>
              <div className="border border-gray-200 rounded-lg overflow-hidden">
                <PageHeader title="상품 상세" onBackClick={() => {}} />
              </div>
            </div>

            <div className="space-y-4 pt-10">
               <p className="text-sm text-gray-500">Bottom Navigation Bar (Navbar)</p>
               <div className="relative h-20 bg-gray-100 rounded-xl flex items-center justify-center overflow-hidden">
                  {/* Navbar uses fixed position, so same issue. We can try to render it wrapper in a portal-free way if possible,
                      or just let it render fixed at the bottom of the screen (which it will).
                      Let's just render it normally so it appears at the bottom of the screen to show how it looks.
                  */}
                  <p className="text-sm text-gray-400">↓↓ Look at the bottom of the screen ↓↓</p>
                  <Navbar />
               </div>
            </div>
          </div>
        </section>

        {/* Banners & Cards Section */}
        <section>
          <h2 className="text-xl font-bold mb-4 text-[#14314F]">7. Banners & Feature Cards</h2>
          <div className="space-y-4">
            <div className="space-y-2">
               <p className="text-sm text-gray-500">AI Styling Banner</p>
               <div className="border border-gray-100 rounded-3xl overflow-hidden">
                  <AiStylingBanner />
               </div>
            </div>
          </div>
        </section>

        {/* Reviews Section */}
        <section>
          <h2 className="text-xl font-bold mb-4 text-[#14314F]">8. Reviews</h2>
          <div className="border border-gray-100 rounded-xl p-4 bg-white">
             {/* Mocking ReviewList - Importing ReviewList component if possible, or just mocking UI */}
             <div className="space-y-4">
               <div className="flex items-start gap-3 pb-4 border-b border-gray-50">
                 <div className="w-10 h-10 bg-gray-200 rounded-full flex-shrink-0" />
                 <div className="flex-1">
                   <div className="flex justify-between items-start">
                     <div>
                       <p className="text-sm font-bold text-gray-900">사용자123</p>
                       <div className="flex text-yellow-400 text-xs mt-0.5">★★★★★</div>
                     </div>
                     <span className="text-xs text-gray-400">2023.12.10</span>
                   </div>
                   <p className="text-sm text-gray-600 mt-2">
                     옷이 너무 예뻐요! 강아지가 좋아하네요. 사이즈도 딱 맞고 재질도 좋습니다.
                   </p>
                 </div>
               </div>
               <div className="flex items-start gap-3">
                 <div className="w-10 h-10 bg-gray-200 rounded-full flex-shrink-0" />
                 <div className="flex-1">
                   <div className="flex justify-between items-start">
                     <div>
                       <p className="text-sm font-bold text-gray-900">PetLover</p>
                       <div className="flex text-yellow-400 text-xs mt-0.5">★★★★☆</div>
                     </div>
                     <span className="text-xs text-gray-400">2023.12.09</span>
                   </div>
                   <p className="text-sm text-gray-600 mt-2">
                     배송 빠르고 좋아요. 다만 색상이 화면이랑 조금 달라요.
                   </p>
                 </div>
               </div>
             </div>
          </div>
        </section>

        {/* Icons Section */}
        <section>
          <h2 className="text-xl font-bold mb-4 text-[#14314F]">9. Icons (Lucide React)</h2>
          <div className="grid grid-cols-4 sm:grid-cols-6 md:grid-cols-8 gap-4">
            {[
              { icon: Home, label: 'Home' },
              { icon: Search, label: 'Search' },
              { icon: Shirt, label: 'Shirt' },
              { icon: Heart, label: 'Heart' },
              { icon: User, label: 'User' },
              { icon: ShoppingCart, label: 'Cart' },
              { icon: ChevronLeft, label: 'Left' },
              { icon: ChevronRight, label: 'Right' },
              { icon: Star, label: 'Star' },
              { icon: Check, label: 'Check' },
              { icon: X, label: 'X' },
              { icon: Menu, label: 'Menu' },
              { icon: Bell, label: 'Bell' },
              { icon: Settings, label: 'Settings' },
              { icon: Plus, label: 'Plus' },
              { icon: Minus, label: 'Minus' },
              { icon: Trash2, label: 'Trash' },
              { icon: Edit2, label: 'Edit' },
              { icon: Camera, label: 'Camera' },
              { icon: Share2, label: 'Share' },
              { icon: Download, label: 'Download' },
              { icon: Info, label: 'Info' },
              { icon: ArrowRight, label: 'Arrow' },
              { icon: ArrowBigRight, label: 'Big Arrow' },
              { icon: CheckCircle2, label: 'Check Circle' },
              { icon: AlertCircle, label: 'Alert' },
              { icon: Truck, label: 'Truck' },
              { icon: CreditCard, label: 'Card' },
              { icon: Gift, label: 'Gift' },
            ].map(({ icon: Icon, label }) => (
              <div key={label} className="flex flex-col items-center justify-center bg-white ">
                <Icon className="w-6 h-6 text-gray-700 mb-2" />
              </div>
            ))}
          </div>
        </section>

      </div>
    </div>
  );
}
