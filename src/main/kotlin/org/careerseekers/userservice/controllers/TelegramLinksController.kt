package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.controllers.interfaces.CrudController
import org.careerseekers.userservice.dto.users.links.CreateTelegramLinksDto
import org.careerseekers.userservice.dto.users.links.UpdateTelegramLinkDto
import org.careerseekers.userservice.entities.TelegramLinks
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.services.TelegramLinksService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users-service/v1/telegram-links")
class TelegramLinksController(
    override val service: TelegramLinksService
) : CrudController<TelegramLinks, Long, CreateTelegramLinksDto, UpdateTelegramLinkDto> {

    @GetMapping("/")
    override fun getAll() = service.getAll().toHttpResponse()

    @GetMapping("/{id}")
    override fun getById(@PathVariable id: Long) =
        service.getById(id, message = "Ссылка в Telegram с ID $id не найдена.")!!.toHttpResponse()

    @PostMapping("/")
    override fun create(@RequestBody item: CreateTelegramLinksDto) = service.create(item).toHttpResponse()

    @PostMapping("/createAll")
    override fun createAll(@RequestBody items: List<CreateTelegramLinksDto>) = service.createAll(items).toHttpResponse()

    @PatchMapping("/")
    override fun update(@RequestBody item: UpdateTelegramLinkDto) = service.update(item).toHttpResponse()

    @DeleteMapping("/{id}")
    override fun deleteById(@PathVariable id: Long) = service.deleteById(id).toHttpResponse()

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/")
    override fun deleteAll() = service.deleteAll().toHttpResponse()
}