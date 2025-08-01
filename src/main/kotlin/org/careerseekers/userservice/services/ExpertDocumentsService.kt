package org.careerseekers.userservice.services

import jakarta.transaction.Transactional
import org.careerseekers.userservice.dto.docs.CreateExpertDocsDto
import org.careerseekers.userservice.dto.docs.CreateExpertDocsTransferDto
import org.careerseekers.userservice.dto.docs.UpdateExpertDocsDto
import org.careerseekers.userservice.entities.ExpertDocuments
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.ExpertDocumentsMapper
import org.careerseekers.userservice.repositories.ExpertDocsRepository
import org.careerseekers.userservice.services.interfaces.crud.IDeleteService
import org.careerseekers.userservice.services.interfaces.crud.IReadService
import org.careerseekers.userservice.utils.DocumentsApiResolver
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class ExpertDocumentsService(
    override val repository: ExpertDocsRepository,
    private val usersService: UsersService,
    private val documentsApiResolver: DocumentsApiResolver,
    private val expertDocumentsMapper: ExpertDocumentsMapper,
) : IReadService<ExpertDocuments, Long>, IDeleteService<ExpertDocuments, Long> {
    private val basicNotFoundMessage: String = "Expert documents not found."

    fun getDocsByUserId(userId: Long, throwable: Boolean = true): ExpertDocuments? {
        return usersService.getById(userId, message = "User with id $userId not found").let {
            repository.findByUserId(it!!.id)
                ?: if (throwable) throw NotFoundException("Documents for user with if $userId not found") else null
        }
    }

    private fun createExpertDocument(item: CreateExpertDocsDto, user: Users): ExpertDocuments {
        val transferDto = CreateExpertDocsTransferDto(
            userId = user.id,
            institution = item.institution,
            post = item.post,
            consentToExpertPdpId = documentsApiResolver.loadDocId(
                "uploadConsentToExpertPDP",
                item.consentToExpertPdp
            )
        )

        return expertDocumentsMapper.expertDocsFromDto(transferDto)
    }

    @Transactional
    fun create(item: CreateExpertDocsDto): ExpertDocuments {
        val user = usersService.getById(item.userId, message = "User with id ${item.userId} not found.")!!
        getDocsByUserId(
            user.id,
            throwable = false
        )?.let { throw DoubleRecordException("This user already has documents. If you want to change it, use update method.") }

        return repository.save(createExpertDocument(item, user))
    }

    @Transactional
    fun update(item: UpdateExpertDocsDto): String {
        getById(item.id, message = basicNotFoundMessage)!!.let { docs ->
            item.institution?.let { docs.institution = it }
            item.post?.let { docs.post = it }

            item.consentToExpertPdp.let {
                val oldId = docs.consentToExpertPdpId

                documentsApiResolver.loadDocId("uploadConsentToExpertPDP", item.consentToExpertPdp)
                documentsApiResolver.deleteDocument(oldId, throwable = false)
            }
        }

        return "Expert documents updated successfully."
    }

    @Transactional
    override fun deleteById(id: Long): String {
        getById(id, message = basicNotFoundMessage)?.let {
            documentsApiResolver.deleteDocument(it.consentToExpertPdpId, throwable = false)
            repository.delete(it)
        }

        return "Expert documents deleted successfully."
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    override fun deleteAll(): String {
        super.deleteAll()

        return "All expert documents deleted successfully"
    }
}