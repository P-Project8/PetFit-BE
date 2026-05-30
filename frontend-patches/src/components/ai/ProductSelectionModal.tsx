import { useState, useMemo } from 'react';
import { X } from 'lucide-react';
import { useProductStore } from '../../store/productStore';
import ProductGrid from '../product/ProductGrid';
import CategoryTabs from '../common/CategoryTabs';
import { productCategories } from '../../data/mockCategories';
import type { Product } from '../../data/products';
import { usePagination } from '@/hooks/usePagination';
import Pagination from '../common/Pagination';

interface ProductSelectionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSelect: (product: Product) => void;
}

// 전체 카테고리 추가
const categoriesWithAll = [{ id: 'all', label: 'All' }, ...productCategories];

export default function ProductSelectionModal({
  isOpen,
  onClose,
  onSelect,
}: ProductSelectionModalProps) {
  const products = useProductStore((state) => state.products);
  const [selectedCategory, setSelectedCategory] = useState('all');

  // 카테고리별 필터링
  const filteredProducts = useMemo(() => {
    if (selectedCategory === 'all') {
      return products;
    }
    return products.filter((product) => product.category === selectedCategory);
  }, [products, selectedCategory]);

  const {
    currentPage,
    totalPages,
    paginatedItems,
    goToPage,
    canGoNext,
    canGoPrev,
  } = usePagination({
    items: filteredProducts,
    itemsPerPage: 12,
  });

  if (!isOpen) return null;

  function handleProductClick(product: Product) {
    onSelect(product);
    onClose();
  }

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center sm:justify-center">
      {/* Backdrop */}
      <div className="absolute inset-0 bg-black/40" onClick={onClose}></div>

      {/* Modal Content */}
      <div className="relative w-full bg-white rounded-t-2xl sm:rounded-2xl h-[80vh] overflow-hidden flex flex-col">
        {/* Header */}
        <div className="sticky top-0 bg-white px-4 pt-4 pb-2 flex items-center justify-between z-20">
          <h2 className="text-lg font-bold text-gray-900">옷 선택하기</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Category Tabs */}
        <div className="sticky z-10">
          <CategoryTabs
            categories={categoriesWithAll}
            activeCategory={selectedCategory}
            onCategoryChange={setSelectedCategory}
            className="shadow-xs"
          />
        </div>

        {/* Product Grid */}
        <div className="flex-1 overflow-y-auto p-4">
          <ProductGrid
            products={paginatedItems as unknown as Parameters<typeof ProductGrid>[0]['products']}
            onProductClick={handleProductClick as unknown as Parameters<typeof ProductGrid>[0]['onProductClick']}
          />
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={goToPage}
            canGoNext={canGoNext}
            canGoPrev={canGoPrev}
          />
        </div>
      </div>
      {/* Custom Scrollbar Hide */}
      <style>{`
        .scrollbar-hide::-webkit-scrollbar {
          display: none;
        }
        .scrollbar-hide {
          -ms-overflow-style: none;
          scrollbar-width: none;
        }
      `}</style>
    </div>
  );
}
