package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.users.links.CreateMentorLinkDto
import org.careerseekers.userservice.entities.MentorLinkBiscuits
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.MentorLinksBiscuitsMapper
import org.careerseekers.userservice.repositories.MentorLinkBiscuitsRepository
import org.careerseekers.userservice.services.interfaces.crud.ICreateService
import org.careerseekers.userservice.services.interfaces.crud.IDeleteService
import org.careerseekers.userservice.services.interfaces.crud.IReadService
import org.careerseekers.userservice.utils.BiscuitGenerator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MentorLinksBiscuitsService(
    override val repository: MentorLinkBiscuitsRepository,
    private val mentorLinksBiscuitsMapper: MentorLinksBiscuitsMapper,
    private val usersService: UsersService,
) : IReadService<MentorLinkBiscuits, Long>,
    ICreateService<MentorLinkBiscuits, Long, CreateMentorLinkDto>,
    IDeleteService<MentorLinkBiscuits, Long> {

    fun getByUserId(userId: Long, throwable: Boolean = true): MentorLinkBiscuits? {
        return repository.findByUserId(userId)
            ?: if (throwable) throw NotFoundException("У ментора с ID $userId ещё нет собственной ссылки.") else null
    }

    fun getByBiscuit(biscuit: String, throwable: Boolean = true): MentorLinkBiscuits? {
        return repository.findByBiscuit(biscuit)
            ?: if (throwable) throw NotFoundException("Ссылки с бискитом $biscuit не существует.") else null
    }

    @Transactional
    override fun create(item: CreateMentorLinkDto): MentorLinkBiscuits {
        getByUserId(item.userId, false)?.let {
            throw DoubleRecordException("У данного пользователя уже есть своя личная ссылка.")
        }

        var newBiscuit: String
        do {
            newBiscuit = BiscuitGenerator.generateLinkBiscuit()
        } while (getByBiscuit(newBiscuit, throwable = false) != null)

        item.apply {
            biscuit = newBiscuit
            user = usersService.getById(item.userId, message = "Пользователь с ID ${item.userId} не найден.")
        }

        return repository.save(mentorLinksBiscuitsMapper.objectFromDto(item))
    }

    @Transactional
    override fun deleteById(id: Long): String {
        getById(id, message = "Ссылка с ID $id не найдена.")!!.also(repository::delete)

        return "Ссылка удалена успешно."
    }

    @Transactional
    override fun deleteAll(): String {
        super.deleteAll()

        return "Все ссылки удалены успешно."
    }
}