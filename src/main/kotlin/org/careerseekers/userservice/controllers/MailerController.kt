package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.dto.UniversalEmailMessageDto
import org.careerseekers.userservice.dto.mail.MailerService
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users-service/v1/mailer")
class MailerController(private val service: MailerService) {

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/send")
    fun send(@RequestBody message: UniversalEmailMessageDto): BasicSuccessfulResponse<String> {
        return service.sendEmail(message).toHttpResponse()
    }
}