package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.docs.CreateUserDocsDto
import org.careerseekers.userservice.dto.docs.CreateUserDocsTransferDto
import org.careerseekers.userservice.dto.docs.UpdateUserDocsDto
import org.careerseekers.userservice.entities.UserDocuments
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.converters.extensions.checkNullable
import org.careerseekers.userservice.mappers.UserDocumentsMapper
import org.careerseekers.userservice.repositories.UserDocsRepository
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.interfaces.crud.ICreateService
import org.careerseekers.userservice.services.interfaces.crud.IDeleteService
import org.careerseekers.userservice.services.interfaces.crud.IReadService
import org.careerseekers.userservice.services.interfaces.crud.IUpdateService
import org.careerseekers.userservice.utils.DocumentsApiResolver
import org.careerseekers.userservice.utils.SnilsValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDocumentsService(
    override val repository: UserDocsRepository,
    private val usersRepository: UsersRepository,
    private val usersService: UsersService,
    private val documentsApiResolver: DocumentsApiResolver,
    private val userDocumentsMapper: UserDocumentsMapper,
    private val snilsValidator: SnilsValidator,
) : IReadService<UserDocuments, Long>,
    ICreateService<UserDocuments, Long, CreateUserDocsDto>,
    IUpdateService<UserDocuments, Long, UpdateUserDocsDto>,
    IDeleteService<UserDocuments, Long> {

    override fun getAll() = repository.findAll()

    fun getById(id: Long, throwable: Boolean = true): UserDocuments? {
        val o = repository.findById(id)
        if (throwable && !o.isPresent) {
            throw NotFoundException("User documents with id $id not found")
        }
        return if (!o.isPresent) null else o.get()
    }

    fun getDocsByUserId(userId: Long, throwable: Boolean = true): UserDocuments? {
        return usersService.getById(userId, message = "User with id $userId not found").let { user ->
            if (user!!.role == UsersRoles.USER) {
                repository.findByUserId(user.id)
                    ?: if (throwable) throw NotFoundException("Documents for user with if $userId not found") else null
            } else {
                throw BadRequestException(
                    "This user has role ${user.role}, not ${UsersRoles.USER}. Please use another controller to check his documents."
                )
            }
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
            snilsId = documentsApiResolver.loadDocId("uploadSnils", item.snilsDto.snilsFile),
            studyingCertificateId = documentsApiResolver.loadDocId(
                "uploadStudyingCertificate",
                item.studyingCertificateFile
            ),
            additionalStudyingCertificateId = documentsApiResolver.loadDocId(
                "uploadAdditionalStudyingCertificate",
                item.additionalStudyingCertificateFile
            ),
            consentToChildPdpId = documentsApiResolver.loadDocId("uploadConsentToChildPDP", item.consentToChildPdpFile)
        )
        return userDocumentsMapper.userDocsFromDto(transferDto)
    }

    @Transactional
    override fun create(item: CreateUserDocsDto): UserDocuments {
        val user = usersService.getById(item.userId, message = "User with id ${item.userId} not found.")!!
        getDocsByUserId(
            user.id,
            throwable = false
        )?.let { throw DoubleRecordException("This user already has documents. If you want to change it, use update method.") }

        val userDoc = createUserDocument(item, user)

        return repository.save(userDoc)
    }


    @Transactional
    override fun update(item: UpdateUserDocsDto): String {
        getById(item.id)!!.let { docs ->
            listOf(
                item.snilsNumber,
                item.snilsFile
            ).checkNullable("Parameters snilsNumber and snilsFile can be only all null values or all non-null values.")
            item.snilsNumber?.let { snilsValidator.checkSnilsValid(it) }

            item.snilsFile.let {
                val oldId = docs.snilsId
                documentsApiResolver.loadDocId("uploadSnils", item.snilsFile).let {
                    docs.snilsId = it ?: throw BadRequestException("Something went wrong while uploading snils.")
                }
                docs.snilsNumber = item.snilsNumber!!

                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }

            item.studyingPlace?.let { docs.studyingPlace = it }

            item.studyingCertificateFile?.let {
                val oldId = docs.studyingCertificateId
                documentsApiResolver.loadDocId("uploadStudyingCertificate", it).let {
                    docs.studyingCertificateId =
                        it ?: throw BadRequestException("Studying certificate ID could not be found.")
                }

                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }

            item.learningClass?.let { docs.learningClass = it }
            item.trainingGround?.let { docs.trainingGround = it }

            item.additionalStudyingCertificateFile?.let {
                val oldId = docs.additionalStudyingCertificateId
                documentsApiResolver.loadDocId("uploadAdditionalStudyingCertificate", it).let {
                    docs.additionalStudyingCertificateId =
                        it ?: throw BadRequestException("Additional studying certificate ID could not be found.")
                }

                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }

            item.parentRole?.let { docs.parentRole = it }

            item.consentToChildPdpFile?.let {
                val oldId = docs.consentToChildPdpId
                documentsApiResolver.loadDocId("uploadConsentToChildPDP", it).let {
                    docs.consentToChildPdpId = it ?: throw BadRequestException("Consent to child pdp ID not found.")
                }

                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }
        }

        return "User documents updated successfully."
    }

    @Transactional
    override fun deleteById(id: Long): String {
        getById(id)?.let {

            it.user.userDocuments = null
            usersRepository.save(it.user)

            repository.delete(it)
            removeDocumentsFromDatabase(it)
        }

        return "User documents deleted successfully."
    }

    @Transactional
    override fun deleteAll(): String {
        getAll().forEach { deleteById(it.id) }
        return "Users documents deleted successfully."
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