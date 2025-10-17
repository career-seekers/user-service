package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.annotations.AccessUntil
import org.careerseekers.userservice.controllers.interfaces.CrudController
import org.careerseekers.userservice.dto.users.CreateChildDto
import org.careerseekers.userservice.dto.users.UpdateChildDto
import org.careerseekers.userservice.entities.Children
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.services.ChildService
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
@RequestMapping("/users-service/v1/children/")
class ChildController(
    override val service: ChildService
) : CrudController<Children, Long, CreateChildDto, UpdateChildDto> {

    @GetMapping("/")
    override fun getAll() = service.getAll().toHttpResponse()

    @GetMapping("/{id}")
    override fun getById(@PathVariable id: Long) =
        service.getById(id, throwable = true, message = "Ребёнок с ID $id не найден.")!!.toHttpResponse()

    @GetMapping("/getByUserId/{userId}")
    fun getByUserId(@PathVariable userId: Long) = service.getByUserId(userId).toHttpResponse()

    @PostMapping("/")
    @AccessUntil(
        until = "2025-10-15T23:59:59+03:00",
        errorMessage = "Срок регистрации закончился 15.10.2025 в 23:59.",
        allowedRoles = [UsersRoles.ADMIN]
    )
    override fun create(@RequestBody item: CreateChildDto) = service.create(item).toHttpResponse()

    @PostMapping("/createAll")
    @AccessUntil(
        until = "2025-10-15T23:59:59+03:00",
        errorMessage = "Срок регистрации закончился 15.10.2025 в 23:59.",
        allowedRoles = [UsersRoles.ADMIN]
    )
    override fun createAll(@RequestBody items: List<CreateChildDto>) = service.createAll(items).toHttpResponse()

    @PatchMapping("/")
    @AccessUntil(
        until = "2025-10-15T23:59:59+03:00",
        errorMessage = "Срок изменения данных о ребенке закончился 15.10.2025 в 23:59.",
        allowedRoles = [UsersRoles.ADMIN]
    )
    override fun update(@RequestBody item: UpdateChildDto) = service.update(item).toHttpResponse()

    @DeleteMapping("/{id}")
    @AccessUntil(
        until = "2025-10-15T23:59:59+03:00",
        errorMessage = "Срок изменения данных о ребенке закончился 15.10.2025 в 23:59.",
        allowedRoles = [UsersRoles.ADMIN]
    )
    override fun deleteById(@PathVariable id: Long) = service.deleteById(id).toHttpResponse()

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/")
    @AccessUntil(
        until = "2025-10-15T23:59:59+03:00",
        errorMessage = "Срок изменения данных о ребенке закончился 15.10.2025 в 23:59.",
        allowedRoles = [UsersRoles.ADMIN]
    )
    override fun deleteAll() = service.deleteAll().toHttpResponse()
}