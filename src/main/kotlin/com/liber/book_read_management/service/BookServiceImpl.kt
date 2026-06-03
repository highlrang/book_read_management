package com.liber.book_read_management.service

import com.liber.book_read_management.client.AladinClient
import com.liber.book_read_management.dto.BookCategoryResponse
import com.liber.book_read_management.dto.BookDetailResponse
import com.liber.book_read_management.dto.BookPageInfoResponse
import com.liber.book_read_management.dto.BookSearchRequest
import com.liber.book_read_management.dto.BookSearchResponse
import com.liber.book_read_management.dto.PageResponse
import com.liber.book_read_management.enums.BookCategory
import com.liber.book_read_management.util.BookCategoryCidMapper
import org.springframework.stereotype.Service

@Service
class BookServiceImpl(
    var aladinClient: AladinClient,
    private val naverBookImageService: NaverBookImageService
) : BookService {

    override fun searchBook(bookSearchRequest: BookSearchRequest): PageResponse<List<BookSearchResponse>> {
        val categoryId = bookSearchRequest.category?.let { category ->
            BookCategoryCidMapper.getCid(bookSearchRequest.searchTarget, category)
        }

        val aladinBookSearchResponse =
            if (bookSearchRequest.query == null)
                aladinClient.getItemList(
                    categoryId = categoryId,
                    searchTarget = bookSearchRequest.searchTarget.value,
                    maxResult = bookSearchRequest.size,
                    start = bookSearchRequest.page
                )
            else
                aladinClient.searchItem(
                    query = bookSearchRequest.query,
                    queryType = bookSearchRequest.queryType.value,
                    categoryId = categoryId,
                    searchTarget = bookSearchRequest.searchTarget.value,
                    maxResult = bookSearchRequest.size,
                    start = bookSearchRequest.page,
                    sort = bookSearchRequest.sort.value
                )

        val aladinItems = aladinBookSearchResponse.item ?: emptyList()
        val naverCovers = naverBookImageService.resolveListCovers(aladinItems)
        val bookSearchResponseList = aladinItems.mapIndexed { index, item ->
            BookSearchResponse.of(item).apply {
                cover = naverCovers[index] ?: item.cover
            }
        }

        return PageResponse(
            bookSearchRequest.page,
            bookSearchRequest.size,
            PageResponse.calTotalPage(aladinBookSearchResponse.totalResults, bookSearchRequest.size),
            bookSearchResponseList
        )
    }

    override fun getBookCategories(): List<BookCategoryResponse> {
        return BookCategory.values()
            .map { category ->
                BookCategoryResponse(
                    code = category.name,
                    label = toBookCategoryLabel(category)
                )
            }
    }

    override fun getBookDetail(isbn: String): BookDetailResponse {
        val aladinBookDetailResponse = aladinClient.getItem(itemItem = isbn)
        return toBookDetailResponse(aladinBookDetailResponse)
    }

    override fun getBookDetailForStorage(isbn: String): BookDetailResponse {
        return getBookDetail(isbn)
    }

    override fun getBookPageInfo(isbn: String): BookPageInfoResponse {
        val aladinBookDetailResponse = aladinClient.getItem(itemItem = isbn)
        val item = aladinBookDetailResponse.item?.firstOrNull()

        return BookPageInfoResponse(
            isbn = isbn,
            totalPage = item?.subInfo?.itemPage
        )
    }

    private fun toBookDetailResponse(
        aladinBookDetailResponse: com.liber.book_read_management.dto.aladin.AladinBookDetailResponse
    ): BookDetailResponse {
        val item = aladinBookDetailResponse.item?.firstOrNull()
        val resolvedCover = naverBookImageService.resolveCover(item?.isbn13, item?.isbn)

        return BookDetailResponse.of(aladinBookDetailResponse, resolvedCover)
    }

    private fun toBookCategoryLabel(category: BookCategory): String {
        return when (category) {
            BookCategory.NOVEL -> "소설"
            BookCategory.ESSAY -> "에세이"
            BookCategory.BUSINESS -> "경영/경제"
            BookCategory.SELF_DEVELOPMENT -> "자기계발"
            BookCategory.HUMANITIES -> "인문"
            BookCategory.SOCIETY -> "사회"
            BookCategory.HISTORY -> "역사"
            BookCategory.SCIENCE -> "과학"
            BookCategory.IT -> "기술/IT"
            BookCategory.ART -> "예술/문화"
            BookCategory.TRAVEL -> "여행"
            BookCategory.HOBBY -> "취미"
            BookCategory.LANGUAGE -> "외국어"
            BookCategory.COMIC -> "만화"
        }
    }

}
