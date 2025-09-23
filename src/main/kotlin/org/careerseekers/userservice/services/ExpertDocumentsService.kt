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
    private val basicNotFoundMessage: String = "Документы эксперта не найдены."

    fun getDocsByUserId(userId: Long, throwable: Boolean = true): ExpertDocuments? {
        return usersService.getById(userId, message = "Пользователь с ID $userId не найден.").let { user ->
            if (user!!.role == UsersRoles.EXPERT) {
                repository.findByUserId(user.id)
                    ?: if (throwable) throw NotFoundException("Пользовательские документы с ID пользователя $userId не найдены.") else null
            } else {
                throw BadRequestException(
                    "У этого пользователя есть роль ${user.role}, а не ${UsersRoles.EXPERT}. Пожалуйста, используйте другой контроллер для проверки его документов."
                )
            }
        }
    }

    @Transactional
    override fun create(item: CreateExpertDocsDto): ExpertDocuments {
        val user = usersService.getById(item.userId, message = "Пользователь с ID ${item.userId} не найден.")!!

        if (user.role != UsersRoles.EXPERT) {
            throw BadRequestException(
                "У этого пользователя есть роль ${user.role}, а не ${UsersRoles.EXPERT}. Пожалуйста, используйте другой контроллер для создания его документов."
            )
        } else {
            item.user = user
        }

        getDocsByUserId(
            user.id,
            throwable = false
        )?.let { throw DoubleRecordException("У этого пользователя уже есть документы. Если вы хотите изменить его, используйте метод обновления.") }

        return repository.save(expertDocumentsMapper.expertDocsFromDto(item))
    }

    @Transactional
    override fun update(item: UpdateExpertDocsDto): String {
        getById(item.id, message = basicNotFoundMessage)!!.let { docs ->
            item.institution?.let { docs.institution = it }
            item.post?.let { docs.post = it }
        }

        return "Документы эксперта обновлены успешно."
    }

    @Transactional
    override fun deleteById(id: Long): String {
        getById(id, message = basicNotFoundMessage)?.let {
            it.user.expertDocuments = null
            usersRepository.save(it.user)

            repository.delete(it)
        }

        return "Документы эксперта удалены успешно."
    }

    @Transactional
    override fun deleteAll(): String {
        getAll().forEach { deleteById(it.id) }
        return "Все документы эксперта удалены успешно."
    }
}