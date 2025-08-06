package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.docs.CreateMentorDocsDto
import org.careerseekers.userservice.dto.docs.CreateMentorDocsTransferDto
import org.careerseekers.userservice.dto.docs.UpdateMentorDocsDto
import org.careerseekers.userservice.entities.MentorDocuments
import org.careerseekers.userservice.entities.Users
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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
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
    private val basicNotFoundMessage: String = "Mentor documents not found."

    fun getDocsByUserId(userId: Long, throwable: Boolean = true): MentorDocuments? {
        return usersService.getById(userId, message = "User with id $userId not found").let { user ->
            if (user!!.role == UsersRoles.MENTOR) {
                repository.findByUserId(user.id)
                    ?: if (throwable) throw NotFoundException("Documents for user with if $userId not found") else null
            } else {
                throw BadRequestException(
                    "This user has role ${user.role}, not ${UsersRoles.MENTOR}. Please use another controller to check his documents."
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
        val user = usersService.getById(item.userId, message = "User with id ${item.userId} not found.")!!

        if (user.role != UsersRoles.MENTOR) {
            throw BadRequestException(
                "This user has role ${user.role}, not ${UsersRoles.MENTOR}. Please use another controller to create his documents."
            )
        }

        getDocsByUserId(user.id, throwable = false)?.let {
            throw DoubleRecordException("This user already has documents. If you want to change it, use update method.")
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

        return "Mentor documents updated successfully."
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun deleteById(id: Long): String {
        getById(id, message = basicNotFoundMessage)?.let {
            it.user.mentorDocuments = null
            usersRepository.save(it.user)

            repository.delete(it)
            documentsApiResolver.deleteDocument(it.consentToMentorPdpId, throwable = false)
        }

        return "Mentor documents deleted successfully."
    }

    @Transactional
    override fun deleteAll(): String {
        getAll().forEach { deleteById(it.id) }
        return "All mentors documents deleted successfully"
    }
}