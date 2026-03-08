package com.liber.book_read_management.service

import com.liber.book_read_management.dto.FileResponseDto
import com.liber.book_read_management.entities.File
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.FileRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID

@Service
class FileServiceImpl(
    private val fileRepository: FileRepository
) : FileService {

    @Value("\${file.upload-dir}")
    private lateinit var uploadDir: String
    @Value("\${myDomain}")
    private lateinit var myDomain: String

    override fun uploadFile(file: MultipartFile): FileResponseDto {
        if (file.isEmpty) {
            throw ApiException(ExceptionType.VALIDATION_ERROR)
        }

        val uploadPath: Path = Paths.get(uploadDir)
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }

        val fileExtension = file.originalFilename?.substringAfterLast('.', "")
        val storedFileName = UUID.randomUUID().toString() + "." + fileExtension
        val filePath: Path = uploadPath.resolve(storedFileName)
        file.transferTo(filePath.toFile())

        val fileEntity = File(
            originalFileName = file.originalFilename!!,
            storedFileName = storedFileName,
            filePath = filePath.toString(),
            contentType = file.contentType!!
        )
        val savedFile = fileRepository.save(fileEntity)

        return FileResponseDto(savedFile.id!!)
    }

    override fun getFilePath(fileId: Long?): String? {
        if (fileId == null) return null
        return fileRepository.findById(fileId)
            .map { file -> file.filePath }
            .orElse(null)
    }

    override fun getFileUrl(filePath: String?): String? {
        if (filePath.isNullOrBlank()) return null
        val fileName = Paths.get(filePath).fileName?.toString() ?: return null
        return "$myDomain/app/uploads/$fileName"
    }

    override fun loadFileResource(fileId: Long): Pair<Resource, String> {
        val file = fileRepository.findById(fileId)
            .orElseThrow { ApiException(ExceptionType.DATA_NOT_FOUND) }

        val resource = FileSystemResource(file.filePath)
        if (!resource.exists()) {
            throw ApiException(ExceptionType.DATA_NOT_FOUND)
        }

        return resource to file.contentType
    }

    override fun loadFileResourceByPath(filePath: String): Pair<Resource, String> {
        val file = fileRepository.findByFilePath(filePath)
            ?: throw ApiException(ExceptionType.DATA_NOT_FOUND)

        val resource = FileSystemResource(file.filePath)
        if (!resource.exists()) {
            throw ApiException(ExceptionType.DATA_NOT_FOUND)
        }

        return resource to file.contentType
    }

    override fun loadFileResourceByStoredFileName(fileName: String): Pair<Resource, String> {
        val file = fileRepository.findByStoredFileName(fileName)
            ?: throw ApiException(ExceptionType.DATA_NOT_FOUND)

        val resource = FileSystemResource(file.filePath)
        if (!resource.exists()) {
            throw ApiException(ExceptionType.DATA_NOT_FOUND)
        }

        return resource to file.contentType
    }
}
