package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.docs.CreateTutorDocsDto
import org.careerseekers.userservice.dto.docs.CreateTutorDocsTransferDto
import org.careerseekers.userservice.dto.docs.UpdateTutorDocsDto
import org.careerseekers.userservice.entities.TutorDocuments
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.TutorDocumentsMapper
import org.careerseekers.userservice.repositories.TutorDocsRepository
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
class TutorDocumentsService(
    override val repository: TutorDocsRepository,
    private val usersRepository: UsersRepository,
    private val usersService: UsersService,
    private val documentsApiResolver: DocumentsApiResolver,
    private val tutorDocumentsMapper: TutorDocumentsMapper,
) : IReadService<TutorDocuments, Long>,
    ICreateService<TutorDocuments, Long, CreateTutorDocsDto>,
    IUpdateService<TutorDocuments, Long, UpdateTutorDocsDto>,
    IDeleteService<TutorDocuments, Long> {
    private val basicNotFoundMessage: String = "Tutor documents not found."

    fun getDocsByUserId(userId: Long, throwable: Boolean = true): TutorDocuments? {
        return usersService.getById(userId, message = "User with id $userId not found").let { user ->
            if (user!!.role == UsersRoles.TUTOR) {
                repository.findByUserId(user.id)
                    ?: if (throwable) throw NotFoundException("Documents for user with if $userId not found") else null
            } else {
                throw BadRequestException(
                    "This user has role ${user.role}, not ${UsersRoles.TUTOR}. Please use another controller to check his documents."
                )
            }
        }
    }

    private fun createTutorDocument(item: CreateTutorDocsDto, user: Users): TutorDocuments {
        val transferDto = CreateTutorDocsTransferDto(
            user = user,
            institution = item.institution,
            post = item.post,
            consentToTutorPdpId = documentsApiResolver.loadDocId(
                "uploadConsentToTutorPDP",
                item.consentToTutorPdp
            )
        )

        return tutorDocumentsMapper.tutorDocsFromDto(transferDto)
    }

    @Transactional
    override fun create(item: CreateTutorDocsDto): TutorDocuments {
        val user = usersService.getById(item.userId, message = "User with id ${item.userId} not found.")!!

        if (user.role != UsersRoles.TUTOR) {
            throw BadRequestException(
                "This user has role ${user.role}, not ${UsersRoles.TUTOR}. Please use another controller to create his documents."
            )
        }

        getDocsByUserId(user.id, throwable = false)?.let {
            throw DoubleRecordException("This user already has documents. If you want to change it, use update method.")
        }

        return repository.save(createTutorDocument(item, user))
    }

    @Transactional
    override fun update(item: UpdateTutorDocsDto): String {
        getById(item.id, message = basicNotFoundMessage)!!.let { docs ->
            item.institution?.let { docs.institution = it }
            item.post?.let { docs.post = it }

            item.consentToTutorPdp?.let {
                val oldId = docs.consentToTutorPdpId

                documentsApiResolver.loadDocId("uploadConsentToMentorPDP", item.consentToTutorPdp)?.let {
                    docs.consentToTutorPdpId = it
                }
                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }
        }

        return "Tutor documents updated successfully."
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun deleteById(id: Long): String {
        getById(id, message = basicNotFoundMessage)?.let {
            it.user.tutorDocuments = null
            usersRepository.save(it.user)

            repository.delete(it)
            documentsApiResolver.deleteDocument(it.consentToTutorPdpId, throwable = false)
        }

        return "Tutor documents deleted successfully."
    }

    @Transactional
    override fun deleteAll(): String {
        getAll().forEach { deleteById(it.id) }
        return "All tutors documents deleted successfully"
    }
}