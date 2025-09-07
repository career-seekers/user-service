package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.users.links.CreateTelegramLinksDto
import org.careerseekers.userservice.dto.users.links.UpdateTelegramLinkDto
import org.careerseekers.userservice.entities.TelegramLinks
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.mappers.TelegramLinksMapper
import org.careerseekers.userservice.repositories.TelegramLinksRepository
import org.careerseekers.userservice.services.interfaces.CrudService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TelegramLinksService(
    override val repository: TelegramLinksRepository,
    private val telegramLinksMapper: TelegramLinksMapper,
    private val usersService: UsersService,
) : CrudService<TelegramLinks, Long, CreateTelegramLinksDto, UpdateTelegramLinkDto> {

    @Transactional
    override fun create(item: CreateTelegramLinksDto): TelegramLinks {
        repository.getByTgLink(item.tgLink)
            ?.let { throw DoubleRecordException("This telegram link is already in use.") }

        return usersService.getById(item.userId, message = "User with id ${item.userId} not found.").let { user ->
            repository.save(telegramLinksMapper.linkFromDto(item.copy(user = user)))
        }
    }

    @Transactional
    override fun createAll(items: List<CreateTelegramLinksDto>): String {
        for (item in items) {
            create(item)
        }
        return "Telegram links created successfully."
    }

    @Transactional
    override fun update(item: UpdateTelegramLinkDto): String {
        return getById(item.id, message = "Telegram link with id ${item.id} not found").let { link ->
            link!!.tgLink = item.tgLink
            repository.save(link)

            "Telegram link updated successfully."
        }
    }

    @Transactional
    override fun deleteById(id: Long): String {
        return getById(id, message = "Telegram link with id $id not found").let { link ->
            link!!.user.telegramLink = null
            repository.delete(link)

            "Telegram link deleted successfully."
        }
    }

    override fun deleteAll(): String {
        getAll().forEach { link ->
            link.user.telegramLink = null
            repository.delete(link)
        }

        return "All telegram links deleted successfully."
    }
}