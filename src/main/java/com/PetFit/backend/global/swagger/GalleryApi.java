package com.PetFit.backend.global.swagger;

import com.PetFit.backend.gallery.presentation.dto.request.CreateCommentRequest;
import com.PetFit.backend.gallery.presentation.dto.request.CreateGalleryRequest;
import com.PetFit.backend.gallery.presentation.dto.response.GalleryCommentResponse;
import com.PetFit.backend.gallery.presentation.dto.response.GalleryResponse;
import com.PetFit.backend.gallery.presentation.dto.response.LikeToggleResponse;
import com.PetFit.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Gallery", description = "Pet-Gallery 커뮤니티 API")
public interface GalleryApi {

    @Operation(summary = "게시물 생성", description = "AI 스타일링 결과를 갤러리에 공유합니다. 인증 필수.")
    BaseResponse<GalleryResponse> createGallery(String userId, CreateGalleryRequest request);

    @Operation(summary = "피드 조회 (최신순)", description = "전체 사용자의 게시물을 최신순으로 조회합니다. 인증 불필요.")
    BaseResponse<Page<GalleryResponse>> getFeed(String userId, Pageable pageable);

    @Operation(summary = "인기 게시물 조회",
            description = "좋아요 수 + 댓글 수 + 최근성(7일 이내 +5) 가중치로 정렬한 인기 피드.")
    BaseResponse<Page<GalleryResponse>> getPopular(String userId, Pageable pageable);

    @Operation(summary = "내 게시물 목록", description = "로그인 사용자가 작성한 게시물 목록.")
    BaseResponse<Page<GalleryResponse>> getMyGalleries(String userId, Pageable pageable);

    @Operation(summary = "게시물 상세")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
    })
    BaseResponse<GalleryResponse> getGallery(String userId, @Parameter(description = "게시물 ID") Long galleryId);

    @Operation(summary = "게시물 삭제 (soft delete)")
    BaseResponse<Void> deleteGallery(String userId, @Parameter(description = "게시물 ID") Long galleryId);

    @Operation(summary = "좋아요 토글",
            description = "이미 누른 상태면 취소, 안 누른 상태면 추가. 인증 필수.")
    BaseResponse<LikeToggleResponse> toggleLike(String userId, @Parameter(description = "게시물 ID") Long galleryId);

    @Operation(summary = "댓글 목록 조회", description = "오래된 순으로 정렬.")
    BaseResponse<Page<GalleryCommentResponse>> getComments(
            @Parameter(description = "게시물 ID") Long galleryId, Pageable pageable);

    @Operation(summary = "댓글 작성", description = "인증 필수.")
    BaseResponse<GalleryCommentResponse> createComment(
            String userId,
            @Parameter(description = "게시물 ID") Long galleryId,
            CreateCommentRequest request);

    @Operation(summary = "댓글 삭제 (soft delete)", description = "본인 댓글만 삭제 가능.")
    BaseResponse<Void> deleteComment(String userId, @Parameter(description = "댓글 ID") Long commentId);
}
