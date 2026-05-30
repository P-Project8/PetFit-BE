import { create } from 'zustand';
import { products as mockProducts, type Product } from '../data/products';

/**
 * 임시 productStore.
 * 향후 백엔드 API로 전환 예정이며, 현재는 mock 데이터를 기반으로
 * 일부 화면(스타일가이드, 마이페이지의 주문내역, AI 스타일링 추천)에서만 사용한다.
 */
interface ProductStore {
  products: Product[];
  getProductById: (id: number) => Product | undefined;
}

export const useProductStore = create<ProductStore>(() => ({
  products: mockProducts,
  getProductById: (id: number) => mockProducts.find((p) => p.id === id),
}));
