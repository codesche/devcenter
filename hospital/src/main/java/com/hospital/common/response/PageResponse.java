package com.hospital.common.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

/**
 * 페이징 처리된 응답 데이터를 담는 객체
 * Spring Data JPA의 Page 객체를 래핑하여 필요한 정보만 클라이언트에 전달
 *
 * @param <T> 리스트 아이템 타입
 */

@Getter
@Builder
public class PageResponse<T> {

    // 실제 데이터 리스트
    private final List<T> content;

    // 현재 페이지 번호 (0부터 시작)
    private final int page;

    // 페이지 크기
    private final int size;

    // 전체 요소 개수
    private final long totalElements;

    // 전체 페이지 개수
    private final long totalPages;

    // 첫 번째 페이지 여부
    private final boolean first;

    // 마지막 페이지 여부
    private final boolean last;

    // 다음 페이지 존재 여부
    private final boolean hasNext;

    // 이전 페이지 존재 여부
    private final boolean hasPrevious;

    // 비어있는 페이지 여부
    private final boolean empty;

    /**
     * Spring Data JPA Page 객체로부터 PageResponse 생성
     *
     * @param page Spring Data JPA Page 객체
     * @param <T>  데이터 타입
     * @return PageResponse 객체
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
            .content(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .hasNext(page.hasNext())
            .hasPrevious(page.hasPrevious())
            .empty(page.isEmpty())
            .build();
    }

    /**
     * 빈 PageResponse 생성
     *
     * @param <T> 데이터 타입
     * @return 빈 PageResponse 객체
     */
    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
            .content(List.of())
            .page(0)
            .size(0)
            .totalElements(0L)
            .totalPages(0)
            .first(true)
            .last(true)
            .hasNext(true)
            .hasPrevious(false)
            .empty(true)
            .build();
    }

    /**
     * 단일 페이지 PageResponse 생성
     *
     * @param content 데이터 리스트
     * @param <T>     데이터 타입
     * @return PageResponse 객체
     */
    public static <T> PageResponse<T> single(List<T> content) {
        return PageResponse.<T>builder()
            .content(content)
            .page(0)
            .size(content.size())
            .totalElements(content.size())
            .totalPages(content.isEmpty() ? 0 : 1)
            .first(true)
            .last(true)
            .hasNext(false)
            .hasPrevious(false)
            .empty(content.isEmpty())
            .build();
    }


}