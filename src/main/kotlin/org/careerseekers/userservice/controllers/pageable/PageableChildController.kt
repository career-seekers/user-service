package org.careerseekers.userservice.controllers.pageable

import org.careerseekers.userservice.dto.filters.ChildrenFilterDto
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.services.ChildService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users-service/v2/children")
class PageableChildController(private val service: ChildService) {

    @GetMapping
    fun getAll(
        @ModelAttribute filters: ChildrenFilterDto,
        pageable: Pageable
    ) = service.getAll(filters, pageable).toHttpResponse()
}