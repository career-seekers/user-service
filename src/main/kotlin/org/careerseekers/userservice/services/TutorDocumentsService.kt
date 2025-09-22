package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.docs.CreateTutorDocsDto
import org.careerseekers.userservice.dto.docs.UpdateTutorDocsDto
import org.careerseekers.userservice.entities.TutorDocuments
import org.careerseekers.userservice.enums.ReviewStatus
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
import org.careerseekers.userservice.utils.Tested
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Tested(testedBy = "scobca", createdOn = "23.08.2025", reviewStatus = ReviewStatus.APPROVED)
class TutorDocumentsService(
    override val repository: TutorDocsRepository,
    private val usersRepository: UsersRepository,
    private val usersService: UsersService,
    private val tutorDocumentsMapper: TutorDocumentsMapper,
) : IReadService<TutorDocuments, Long>,
    ICreateService<TutorDocuments, Long, CreateTutorDocsDto>,
    IUpdateService<TutorDocuments, Long, UpdateTutorDocsDto>,
    IDeleteService<TutorDocuments, Long> {
    private val basicNotFoundMessage: String = "Документы куратора не найдены."

    fun getDocsByUserId(userId: Long, throwable: Boolean = true): TutorDocuments? {
        return usersService.getById(userId, message = "Пользователь с ID $userId не найден.").let { user ->
            if (user!!.role == UsersRoles.TUTOR) {
                repository.findByUserId(user.id)
                    ?: if (throwable) throw NotFoundException("Пользовательские документы с ID пользователя $userId не найдены.") else null
            } else {
                throw BadRequestException(
                    "У этого пользователя есть роль ${user.role}, а не ${UsersRoles.TUTOR}. Пожалуйста, используйте другого администратора для проверки его документов."
                )
            }
        }
    }

    @Transactional
    override fun create(item: CreateTutorDocsDto): TutorDocuments {
        val user = usersService.getById(item.userId, message = "Пользователь с ID ${item.userId} не найден.")!!

        if (user.role != UsersRoles.TUTOR) {
            throw BadRequestException(
                "У этого пользователя есть роль ${user.role}, а не ${UsersRoles.TUTOR}. Пожалуйста, используйте другой контроллер для создания его документов."
            )
        }

        item.user = user
        getDocsByUserId(user.id, throwable = false)?.let {
            throw DoubleRecordException("У этого пользователя уже есть документы. Если вы хотите изменить его, используйте метод обновления.")
        }

        return repository.save(tutorDocumentsMapper.tutorDocsFromDto(item))
    }

    @Transactional
    override fun update(item: UpdateTutorDocsDto): String {
        getById(item.id, message = basicNotFoundMessage)!!.let { docs ->
            item.institution?.let { docs.institution = it }
            item.post?.let { docs.post = it }
        }

        return "Документы куратора успешно обновлены."
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun deleteById(id: Long): String {
        getById(id, message = basicNotFoundMessage)?.let {
            it.user.tutorDocuments = null
            usersRepository.save(it.user)

            repository.delete(it)
        }

        return "Документы куратора успешно удалены."
    }

    @Transactional
    override fun deleteAll(): String {
        getAll().forEach { deleteById(it.id) }
        return "Все документы куратора успешно удалены."
    }
}