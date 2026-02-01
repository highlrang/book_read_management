package com.liber.book_read_management.entities

import jakarta.persistence.*

@Entity
@Table(name = "FILE")
class File(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "original_file_name", nullable = false)
    var originalFileName: String,

    @Column(name = "stored_file_name", nullable = false)
    var storedFileName: String,

    @Column(name = "file_path", nullable = false)
    var filePath: String,

    @Column(name = "content_type", nullable = false)
    var contentType: String,

) : BaseTimeEntity()
