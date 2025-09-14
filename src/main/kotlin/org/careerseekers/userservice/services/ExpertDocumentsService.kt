package org.careerseekers.userservice.services

import jakarta.transaction.Transactional
import org.careerseekers.userservice.dto.docs.CreateExpertDocsDto
import org.careerseekers.userservice.dto.docs.UpdateExpertDocsDto
import org.careerseekers.userservice.entities.ExpertDocuments
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.ExpertDocumentsMapper
import org.careerseekers.userservice.repositories.ExpertDocsRepository
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.interfaces.crud.ICreateService
import org.careerseekers.userservice.services.interfaces.crud.IDeleteService
import org.careerseekers.userservice.services.interfaces.crud.IReadService
import org.careerseekers.userservice.services.interfaces.crud.IUpdateService
import org.springframework.stereotype.Service

@Service
class ExpertDocumentsService(
    override val repository: ExpertDocsRepository,
    private val usersRepository: UsersRepository,
    private val usersService: UsersService,
    private val expertDocumentsMapper: ExpertDocumentsMapper,
) : IReadService<ExpertDocuments, Long>,
    ICreateService<ExpertDocuments, Long, CreateExpertDocsDto>,
    IUpdateService<ExpertDocuments, Long, UpdateExpertDocsDto>,
    IDeleteService<ExpertDocuments, Long> {
    private val basicNotFoundMessage: String = "Expert documents not found."

    fun getDocsByUserId(userId: Long, throwable: Boolean = true): ExpertDocuments? {
        return usersService.getById(userId, message = "User with id $userId not found").let { user ->
            if (user!!.role == UsersRoles.EXPERT) {
                repository.findByUserId(user.id)
                    ?: if (throwable) throw NotFoundException("Documents for user with id $userId not found") else null
            } else {
                throw BadRequestException(
                    "This user has role ${user.role}, not ${UsersRoles.EXPERT}. Please use another controller to check his documents."
                )
            }
        }
    }

    @Transactional
    override fun create(item: CreateExpertDocsDto): ExpertDocuments {
        val user = usersService.getById(item.userId, message = "User with id ${item.userId} not found.")!!

        if (user.role != UsersRoles.EXPERT) {
            throw BadRequestException(
                "This user has role ${user.role}, not ${UsersRoles.EXPERT}. Please use another controller to create his documents."
            )
        } else {
            item.user = user
        }

        getDocsByUserId(
            user.id,
            throwable = false
        )?.let { throw DoubleRecordException("This user already has documents. If you want to change it, use update method.") }

        return repository.save(expertDocumentsMapper.expertDocsFromDto(item))
    }

    @Transactional
    override fun update(item: UpdateExpertDocsDto): String {
        getById(item.id, message = basicNotFoundMessage)!!.let { docs ->
            item.institution?.let { docs.institution = it }
            item.post?.let { docs.post = it }
        }

        return "Expert documents updated successfully."
    }

    @Transactional
    override fun deleteById(id: Long): String {
        getById(id, message = basicNotFoundMessage)?.let {
            it.user.expertDocuments = null
            usersRepository.save(it.user)

            repository.delete(it)
        }

        return "Expert documents deleted successfully."
    }

    @Transactional
    override fun deleteAll(): String {
        getAll().forEach { deleteById(it.id) }
        return "All expert documents deleted successfully"
    }
}