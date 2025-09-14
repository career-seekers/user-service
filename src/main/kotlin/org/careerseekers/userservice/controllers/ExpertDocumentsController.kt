package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.controllers.interfaces.crud.IDeleteController
import org.careerseekers.userservice.controllers.interfaces.crud.IReadController
import org.careerseekers.userservice.dto.docs.CreateExpertDocsDto
import org.careerseekers.userservice.dto.docs.UpdateExpertDocsDto
import org.careerseekers.userservice.entities.ExpertDocuments
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.services.ExpertDocumentsService
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
@RequestMapping("/users-service/v1/expert-docs")
class ExpertDocumentsController(
    override val service: ExpertDocumentsService
) : IReadController<ExpertDocuments, Long>,
    IDeleteController<ExpertDocuments, Long> {
    @GetMapping("/{id}")
    override fun getById(@PathVariable id: Long) = service.getById(id)!!.toHttpResponse()

    @GetMapping("/")
    override fun getAll() = service.getAll().toHttpResponse()

    @GetMapping("getByUserId/{id}")
    fun getDocsByUserId(@PathVariable id: Long) = service.getDocsByUserId(id)

    @PostMapping("/")
    fun create(@RequestBody item: CreateExpertDocsDto): BasicSuccessfulResponse<ExpertDocuments> =
        service.create(item).toHttpResponse()


    @PatchMapping("/")
    fun update(@RequestBody item: UpdateExpertDocsDto): BasicSuccessfulResponse<String> =
        service.update(item).toHttpResponse()

    @DeleteMapping("/{id}")
    override fun deleteById(@PathVariable id: Long): BasicSuccessfulResponse<String> {
        val res = service.deleteById(id).toHttpResponse()
        return res
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/")
    override fun deleteAll() = service.deleteAll().toHttpResponse()
}