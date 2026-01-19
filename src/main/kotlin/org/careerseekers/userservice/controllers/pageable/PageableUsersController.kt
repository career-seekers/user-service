package org.careerseekers.userservice.controllers.pageable

import org.careerseekers.userservice.dto.filters.UsersFilterDto
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.careerseekers.userservice.services.UsersService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users-service/v2/users")
class PageableUsersController(private val service: UsersService) {

    @GetMapping
    fun getAll(
        @ModelAttribute filters: UsersFilterDto,
        pageable: Pageable
    ) = service.getAll(filters, pageable).toHttpResponse()
}