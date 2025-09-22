package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.docs.CreateMentorDocsDto
import org.careerseekers.userservice.dto.docs.CreateMentorDocsTransferDto
import org.careerseekers.userservice.dto.docs.UpdateMentorDocsDto
import org.careerseekers.userservice.entities.MentorDocuments
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.ReviewStatus
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.MentorsDocumentsMapper
import org.careerseekers.userservice.repositories.MentorDocsRepository
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.interfaces.crud.ICreateService
import org.careerseekers.userservice.services.interfaces.crud.IDeleteService
import org.careerseekers.userservice.services.interfaces.crud.IReadService
import org.careerseekers.userservice.services.interfaces.crud.IUpdateService
import org.careerseekers.userservice.utils.DocumentsApiResolver
import org.careerseekers.userservice.utils.Tested
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Tested(testedBy = "scobca", createdOn = "23.08.2025", reviewStatus = ReviewStatus.APPROVED)
class MentorDocumentsService(
    override val repository: MentorDocsRepository,
    private val usersRepository: UsersRepository,
    private val usersService: UsersService,
    private val documentsApiResolver: DocumentsApiResolver,
    private val mentorsDocumentsMapper: MentorsDocumentsMapper,
) : IReadService<MentorDocuments, Long>,
    ICreateService<MentorDocuments, Long, CreateMentorDocsDto>,
    IUpdateService<MentorDocuments, Long, UpdateMentorDocsDto>,
    IDeleteService<MentorDocuments, Long> {
    private val basicNotFoundMessage: String = "Документы наставника не найдены."

    fun getDocsByUserId(userId: Long, throwable: Boolean = true): MentorDocuments? {
        return usersService.getById(userId, message = "Пользователь с ID $userId не найден.").let { user ->
            if (user!!.role == UsersRoles.MENTOR) {
                repository.findByUserId(user.id)
                    ?: if (throwable) throw NotFoundException("Пользовательские документы с ID пользователя $userId не найдены.") else null
            } else {
                throw BadRequestException(
                    "У этого пользователя есть роль ${user.role}, а не ${UsersRoles.MENTOR}. Пожалуйста, используйте другого администратора для проверки его документов."
                )
            }
        }
    }

    private fun createMentorDocument(item: CreateMentorDocsDto, user: Users): MentorDocuments {
        val transferDto = CreateMentorDocsTransferDto(
            user = user,
            institution = item.institution,
            post = item.post,
            consentToMentorPdpId = documentsApiResolver.loadDocId(
                "uploadConsentToMentorPDP",
                item.consentToMentorPdp
            )
        )

        return mentorsDocumentsMapper.mentorDocsFromDto(transferDto)
    }

    @Transactional
    override fun create(item: CreateMentorDocsDto): MentorDocuments {
        val user = usersService.getById(item.userId, message = "Пользователь с ID ${item.userId} не найден.")!!

        if (user.role != UsersRoles.MENTOR) {
            throw BadRequestException(
                "У этого пользователя есть роль ${user.role}, а не ${UsersRoles.MENTOR}. Пожалуйста, используйте другой контроллер для создания его документов."
            )
        }

        getDocsByUserId(user.id, throwable = false)?.let {
            throw DoubleRecordException("У этого пользователя уже есть документы. Если вы хотите изменить его, используйте метод обновления.")
        }

        return repository.save(createMentorDocument(item, user))
    }

    @Transactional
    override fun update(item: UpdateMentorDocsDto): String {
        getById(item.id, message = basicNotFoundMessage)!!.let { docs ->
            item.institution?.let { docs.institution = it }
            item.post?.let { docs.post = it }

            item.consentToMentorPdp?.let {
                val oldId = docs.consentToMentorPdpId

                documentsApiResolver.loadDocId("uploadConsentToMentorPDP", item.consentToMentorPdp)?.let {
                    docs.consentToMentorPdpId = it
                }
                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }
        }

        return "Документы наставника обновлены успешно."
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun deleteById(id: Long): String {
        getById(id, message = basicNotFoundMessage)?.let {
            it.user.mentorDocuments = null
            usersRepository.save(it.user)

            repository.delete(it)
            documentsApiResolver.deleteDocument(it.consentToMentorPdpId, throwable = false)
        }

        return "Документы наставника удалены успешно."
    }

    @Transactional
    override fun deleteAll(): String {
        getAll().forEach { deleteById(it.id) }
        return "Все документы наставника удалены успешно."
    }
}