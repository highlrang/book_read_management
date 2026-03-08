package com.liber.book_read_management.entities

import com.liber.book_read_management.entities.Book
import com.liber.book_read_management.enums.CategoryGroup
import com.liber.book_read_management.enums.BookReadStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "BOOK_READ_LOG")
class BookReadLog (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,
    @Column(name = "user_id")
    var userId : Long,
    @Column(name = "book_isbn")

    var bookIsbn: String,
    @Column(name = "book_title")
    var bookTitle: String,
    @Column(name = "book_author")
    var bookAuthor: String,
    @Column(name = "book_thumbnail_image")
    var bookThumbnailImage: String,

    @Column(name = "total_page")
    var totalPage: Int = 0,
    @Enumerated(EnumType.STRING)
    @Column(name = "read_status")
    var readStatus: BookReadStatus = BookReadStatus.READING,
    @Column(name = "progress_percentage")
    var progressPercentage: Int = 0,
    @Column(name = "category_path")
    var categoryPath: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "category_group")
    var categoryGroup: CategoryGroup? = null,

) : BaseTimeEntity() {

    companion object {
        fun of(userId: Long, book: Book) : BookReadLog {
            return BookReadLog(
                userId = userId,
                bookIsbn = book.isbn,
                bookTitle = book.title,
                bookAuthor = book.author,
                bookThumbnailImage = book.cover ?: "",
                totalPage = book.totalPage ?: 0,
                categoryPath = book.categoryPath,
                categoryGroup = book.categoryGroup
            )
        }
    }
}
