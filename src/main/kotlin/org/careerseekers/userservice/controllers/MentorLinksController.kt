package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.controllers.interfaces.crud.ICreateController
import org.careerseekers.userservice.controllers.interfaces.crud.IDeleteController
import org.careerseekers.userservice.controllers.interfaces.crud.IReadController
import org.careerseekers.userservice.dto.users.links.CreateMentorLinkDto
import org.careerseekers.userservice.entities.MentorLinkBiscuits
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.services.MentorLinksBiscuitsService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users-service/v1/mentor-links/")
class MentorLinksController(
    override val service: MentorLinksBiscuitsService,
) : IReadController<MentorLinkBiscuits, Long>,
    ICreateController<MentorLinkBiscuits, Long, CreateMentorLinkDto>,
    IDeleteController<MentorLinkBiscuits, Long> {

    @GetMapping("/")
    override fun getAll() = service.getAll().toHttpResponse()

    @GetMapping("/{id}")
    override fun getById(@PathVariable id: Long) =
        service.getById(id, message = "Ссылка с ID $id не найдена.")!!.toHttpResponse()

    @GetMapping("/getByUserId/{id}")
    fun getByUserId(@PathVariable id: Long) = service.getByUserId(id)!!.toHttpResponse()

    @GetMapping("/getByBiscuit/{biscuit}")
    fun getByBiscuit(@PathVariable biscuit: String) = service.getByBiscuit(biscuit)!!.toHttpResponse()

    @PostMapping("/")
    override fun create(@RequestBody item: CreateMentorLinkDto) = service.create(item).toHttpResponse()

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/")
    override fun deleteAll() = service.deleteAll().toHttpResponse()

    @DeleteMapping("/{id}")
    override fun deleteById(@PathVariable id: Long) = service.deleteById(id).toHttpResponse()
}