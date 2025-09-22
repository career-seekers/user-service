package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.TgLinkNotificationDto
import org.careerseekers.userservice.dto.users.links.CreateTelegramLinksDto
import org.careerseekers.userservice.dto.users.links.UpdateTelegramLinkDto
import org.careerseekers.userservice.entities.TelegramLinks
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.io.converters.extensions.toCache
import org.careerseekers.userservice.mappers.TelegramLinksMapper
import org.careerseekers.userservice.repositories.TelegramLinksRepository
import org.careerseekers.userservice.services.interfaces.CrudService
import org.careerseekers.userservice.services.kafka.producers.KafkaTgLinksNotificationProducer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TelegramLinksService(
    override val repository: TelegramLinksRepository,
    private val telegramLinksMapper: TelegramLinksMapper,
    private val usersService: UsersService,
    private val notificationProducer: KafkaTgLinksNotificationProducer
) : CrudService<TelegramLinks, Long, CreateTelegramLinksDto, UpdateTelegramLinkDto> {

    @Transactional
    override fun create(item: CreateTelegramLinksDto): TelegramLinks {
        repository.getByTgLink(item.tgLink)
            ?.let { throw DoubleRecordException("Эта telegram-ссылка уже используется.") }

        return usersService.getById(item.userId, message = "Пользователь с ID ${item.userId} не найден.").let { user ->
            repository.save(telegramLinksMapper.linkFromDto(item.copy(user = user)))
        }.also {
            notificationProducer.sendMessage(TgLinkNotificationDto(user = it.user.toCache()))
        }
    }

    @Transactional
    override fun createAll(items: List<CreateTelegramLinksDto>): String {
        for (item in items) {
            create(item)
        }
        return "Telegram-ссылка успешно создана."
    }

    @Transactional
    override fun update(item: UpdateTelegramLinkDto): String {
        return getById(item.id, message = "Ссылка на Telegram с ID ${item.id} не найдена").let { link ->
            link!!.tgLink = item.tgLink
            repository.save(link)

            "Telegram-ссылка обновлена успешно."
        }
    }

    @Transactional
    override fun deleteById(id: Long): String {
        return getById(id, message = "Telegram link with id $id not found").let { link ->
            link!!.user.telegramLink = null
            repository.delete(link)

            "Telegram-ссылка удалена успешно."
        }
    }

    override fun deleteAll(): String {
        getAll().forEach { link ->
            link.user.telegramLink = null
            repository.delete(link)
        }

        return "Все Telegram-ссылки удалены успешно."
    }
}