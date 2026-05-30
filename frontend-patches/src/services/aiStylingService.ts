/**
 * AI 스타일링 서비스 - PetFit 백엔드 API를 통해 Gemini 호출.
 * Base64 이미지를 백엔드로 전송하면, 백엔드가 S3 저장과 이력 관리까지 처리.
 */
import axios from 'axios';
import { BASE_URL } from './api';

interface StyleApiResponse {
  isSuccess: boolean;
  code: string;
  message: string;
  result: {
    stylingId: number;
    resultImageUrl: string;
    resultImageBase64: string;
  };
}

/**
 * Base64 또는 URL을 백엔드 친화적인 순수 Base64로 변환.
 */
async function toBase64(input: string): Promise<string> {
  if (input.startsWith('data:')) {
    const idx = input.indexOf(',');
    return idx >= 0 ? input.slice(idx + 1) : input;
  }
  const response = await fetch(input);
  const blob = await response.blob();
  return new Promise<string>((resolve, reject) => {
    const reader = new FileReader();
    reader.onloadend = () => {
      const data = reader.result as string;
      const idx = data.indexOf(',');
      resolve(idx >= 0 ? data.slice(idx + 1) : data);
    };
    reader.onerror = reject;
    reader.readAsDataURL(blob);
  });
}

/**
 * AI 가상 피팅 생성.
 * @param petImage 반려동물 사진 (Base64 / data URL / URL)
 * @param clothingImage 옷 사진 (Base64 / data URL / URL)
 * @param productId 옵션: 연관 상품 ID
 * @returns 결과 이미지 (data: URL 형태로 표시 가능)
 */
export async function generateStylingImage(
  petImage: string,
  clothingImage: string,
  productId?: number
): Promise<string> {
  const petImageBase64 = await toBase64(petImage);
  const clothImageBase64 = await toBase64(clothingImage);

  const accessToken = localStorage.getItem('accessToken');
  const headers = accessToken ? { Authorization: `Bearer ${accessToken}` } : {};

  try {
    const { data } = await axios.post<StyleApiResponse>(
      `${BASE_URL}/api/ai/styling`,
      {
        petImageBase64,
        clothImageBase64,
        productId: productId ?? null,
      },
      { headers }
    );

    const { resultImageBase64, resultImageUrl } = data.result;

    // S3 URL이 있으면 그걸 우선, 없으면 Base64 data URL을 반환
    if (resultImageUrl) return resultImageUrl;
    if (resultImageBase64) return `data:image/png;base64,${resultImageBase64}`;
    throw new Error('이미지를 생성하지 못했습니다. 다시 시도해주세요.');
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const msg = error.response?.data?.message ?? error.message;
      if (msg?.includes('INVALID_INPUT') || msg?.includes('강아지')) {
        throw new Error('강아지나 옷 사진이 아닌 것 같습니다. 다시 확인해주세요.');
      }
      throw new Error(msg || 'AI 스타일링 요청에 실패했습니다.');
    }
    throw error;
  }
}
