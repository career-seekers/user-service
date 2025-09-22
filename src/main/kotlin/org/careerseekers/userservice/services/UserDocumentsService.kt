package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.docs.CreateUserDocsDto
import org.careerseekers.userservice.dto.docs.CreateUserDocsTransferDto
import org.careerseekers.userservice.dto.docs.UpdateUserDocsDto
import org.careerseekers.userservice.entities.UserDocuments
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.ReviewStatus
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
import org.careerseekers.userservice.utils.Tested
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Tested(testedBy = "scobca", createdOn = "23.08.2025", reviewStatus = ReviewStatus.APPROVED)
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
            throw NotFoundException("Пользовательские документы с ID $id не найдены.")
        }
        return if (!o.isPresent) null else o.get()
    }

    fun getDocsByUserId(userId: Long, throwable: Boolean = true): UserDocuments? {
        return usersService.getById(userId, message = "Пользователь с ID $userId не найден.").let { user ->
            if (user!!.role == UsersRoles.USER) {
                repository.findByUserId(user.id)
                    ?: if (throwable) throw NotFoundException("Пользовательские документы с ID пользователя $userId не найдены.") else null
            } else {
                throw BadRequestException(
                    "У этого пользователя есть роль ${user.role}, а не ${UsersRoles.USER}. Пожалуйста, используйте другой контроллер для проверки его документов."
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
            consentToChildPdpId = documentsApiResolver.loadDocId("uploadConsentToChildPDP", item.consentToChildPdpFile),
            birthCertificateId = documentsApiResolver.loadDocId("uploadBirthCertificate", item.birthCertificateFile),
        )
        return userDocumentsMapper.userDocsFromDto(transferDto)
    }

    @Transactional
    override fun create(item: CreateUserDocsDto): UserDocuments {
        val user = usersService.getById(item.userId, message = "Пользователь с ID ${item.userId} не найден.")!!
        getDocsByUserId(
            user.id,
            throwable = false
        )?.let { throw DoubleRecordException("У этого пользователя уже есть документы. Если вы хотите изменить его, используйте метод обновления.") }

        val userDoc = createUserDocument(item, user)

        return repository.save(userDoc)
    }


    @Transactional
    override fun update(item: UpdateUserDocsDto): String {
        getById(item.id)!!.let { docs ->
            listOf(
                item.snilsNumber,
                item.snilsFile
            ).checkNullable("Параметры snilsNumber и snilsFile могут содержать только все нулевые значения или все ненулевые значения, отличные от нуля.")
            item.snilsNumber?.let { snilsValidator.checkSnilsValid(it) }

            item.snilsFile.let {
                val oldId = docs.snilsId
                documentsApiResolver.loadDocId("uploadSnils", item.snilsFile).let {
                    docs.snilsId = it ?: throw BadRequestException("Что-то пошло не так при загрузке СНИЛС.")
                }
                docs.snilsNumber = item.snilsNumber!!

                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }

            item.studyingPlace?.let { docs.studyingPlace = it }

            item.studyingCertificateFile?.let {
                val oldId = docs.studyingCertificateId
                documentsApiResolver.loadDocId("uploadStudyingCertificate", it).let { docId ->
                    docs.studyingCertificateId =
                        docId ?: throw BadRequestException("Справка из ОУ не найдена.")
                }

                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }

            item.learningClass?.let { docs.learningClass = it }
            item.trainingGround?.let { docs.trainingGround = it }

            item.additionalStudyingCertificateFile?.let {
                val oldId = docs.additionalStudyingCertificateId
                documentsApiResolver.loadDocId("uploadAdditionalStudyingCertificate", it).let { docId ->
                    docs.additionalStudyingCertificateId =
                        docId ?: throw BadRequestException("Справка из дополнительного ОУ не найдена.")
                }

                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }

            item.parentRole?.let { docs.parentRole = it }

            item.consentToChildPdpFile?.let {
                val oldId = docs.consentToChildPdpId
                documentsApiResolver.loadDocId("uploadConsentToChildPDP", it).let { docId ->
                    docs.consentToChildPdpId = docId ?: throw BadRequestException("Согласие на ОПД ребёнка не найдено.")
                }

                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }
        }

        return "Пользовательские документы успешно обновлены."
    }

    @Transactional
    override fun deleteById(id: Long): String {
        getById(id)?.let {

            it.user.userDocuments = null
            usersRepository.save(it.user)

            repository.delete(it)
            removeDocumentsFromDatabase(it)
        }

        return "Пользовательские документы успешно удалены."
    }

    @Transactional
    override fun deleteAll(): String {
        getAll().forEach { deleteById(it.id) }
        return "Все документы пользователей успешно удалены."
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