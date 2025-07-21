package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.docs.CreateUserDocsDto
import org.careerseekers.userservice.dto.docs.CreateUserDocsTransferDto
import org.careerseekers.userservice.dto.docs.UpdateUserDocsDto
import org.careerseekers.userservice.entities.UserDocuments
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.UserDocumentsMapper
import org.careerseekers.userservice.repositories.UserDocsRepository
import org.careerseekers.userservice.utils.DocumentsApiResolver
import org.careerseekers.userservice.utils.SnilsValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class UserDocumentsService(
    val repository: UserDocsRepository,
    private val usersService: UsersService,
    private val documentsApiResolver: DocumentsApiResolver,
    private val userDocumentsMapper: UserDocumentsMapper,
    private val snilsValidator: SnilsValidator,
) {
    fun getAll() = repository.findAll()

    fun getById(id: Long, throwable: Boolean = true): UserDocuments? {
        val o = repository.findById(id)
        if (throwable && !o.isPresent) {
            throw NotFoundException("User documents with id $id not found")
        }
        return if (!o.isPresent) null else o.get()
    }

    fun getDocsByUserId(userId: Long, throwable: Boolean = true): UserDocuments? {
        return usersService.getById(userId, message = "User with id $userId not found").let {
            repository.findByUserId(it!!.id)
                ?: if (throwable) throw NotFoundException("Documents for user with if $userId not found") else null
        }
    }

    private fun createUserDocument(item: CreateUserDocsDto, user: Users): UserDocuments {
        snilsValidator.checkSnilsValid(item.snilsDto.snilsNumber)

        val transferDto = CreateUserDocsTransferDto(
            user = user,
            snilsNumber = item.snilsDto.snilsNumber,
            studyingPlace = item.studyingPlace,
            learningClass = item.learningClass,
            trainingGround = item.trainingGround,
            parentRole = item.parentRole,
            snilsId = loadDocId("uploadSnils", item.snilsDto.snilsFile),
            studyingCertificateId = loadDocId("uploadStudyingCertificate", item.studyingCertificateFile),
            additionalStudyingCertificateId = loadDocId(
                "uploadAdditionalStudyingCertificate",
                item.additionalStudyingCertificateFile
            ),
            consentToChildPdpId = loadDocId("uploadConsentToChildPDP", item.consentToChildPdpFile)
        )
        return userDocumentsMapper.userDocsFromDto(transferDto)
    }

    @Transactional
    fun create(item: CreateUserDocsDto): UserDocuments {
        val user = usersService.getById(item.userId, message = "User with id ${item.userId} not found.")!!
        getDocsByUserId(
            user.id,
            throwable = false
        )?.let { throw DoubleRecordException("This user already has documents. If you want to change it, use update method.") }

        val userDoc = createUserDocument(item, user)

        return repository.save(userDoc)
    }


    @Transactional
    fun update(item: UpdateUserDocsDto): String {
        getById(item.id).let { docs ->
            usersService.getById(docs!!.user.id, message = "User with id ${docs.user.id} not found.")

            item.snilsDto?.let { snilsDto ->
                val oldId = docs.snilsId
                loadDocId("uploadSnils", snilsDto.snilsFile).let {
                    docs.snilsId = it ?: throw BadRequestException("Snils ID could not be found.")
                }

                documentsApiResolver.deleteDocument(oldId)
                docs.snilsNumber = snilsDto.snilsNumber
            }

            item.studyingPlace?.let { docs.studyingPlace = it }

            item.studyingCertificateFile?.let {
                val oldId = docs.studyingCertificateId
                loadDocId("uploadStudyingCertificate", it).let {
                    docs.studyingCertificateId =
                        it ?: throw BadRequestException("Studying certificate ID could not be found.")
                }

                documentsApiResolver.deleteDocument(oldId)
            }

            item.learningClass?.let { docs.learningClass = it }
            item.trainingGround?.let { docs.trainingGround = it }

            item.additionalStudyingCertificateFile?.let {
                val oldId = docs.additionalStudyingCertificateId
                loadDocId("uploadAdditionalStudyingCertificate", it).let {
                    docs.additionalStudyingCertificateId =
                        it ?: throw BadRequestException("Additional studying certificate ID could not be found.")
                }

                documentsApiResolver.deleteDocument(oldId)
            }

            item.parentRole?.let { docs.parentRole = it }

            item.consentToChildPdpFile?.let {
                val oldId = docs.consentToChildPdpId
                loadDocId("uploadConsentToChildPDP", it).let {
                    docs.consentToChildPdpId = it ?: throw BadRequestException("Consent to child pdp ID not found.")
                }

                documentsApiResolver.deleteDocument(oldId)
            }
        }

        return "User documents updated successfully."
    }

    @Transactional
    fun deleteById(id: Long): String {
        getById(id)?.let {
            removeDocumentsFromDatabase(it)
            repository.delete(it)
        }

        return "User documents deleted successfully."
    }

    @Transactional
    fun deleteAll(): String {
        repository.deleteAll()

        return "Users documents deleted successfully."
    }

    private fun loadDocId(url: String, file: MultipartFile?): Long? =
        file?.let {
            val id = documentsApiResolver.loadDocument(url, it)?.id
            id?.let { documentsApiResolver.registerFileForRollback(it) }
            id
        }

    private fun removeDocumentsFromDatabase(userDocs: UserDocuments) {
        listOf(
            userDocs.snilsId,
            userDocs.studyingCertificateId,
            userDocs.additionalStudyingCertificateId,
            userDocs.consentToChildPdpId
        ).forEach { documentId ->
            documentsApiResolver.deleteDocument(documentId)
        }
    }
}