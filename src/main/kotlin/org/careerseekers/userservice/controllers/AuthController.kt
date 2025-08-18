package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.dto.auth.LoginUserDto
import org.careerseekers.userservice.dto.auth.RegistrationDto
import org.careerseekers.userservice.dto.auth.UpdateUserTokensDto
import org.careerseekers.userservice.services.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users-service/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/register")
    fun register(@RequestBody data: RegistrationDto) = authService.register(data).toHttpResponse()

    @PostMapping("/login")
    fun login(@RequestBody data: LoginUserDto) = authService.login(data).toHttpResponse()

    @PostMapping("/updateTokens")
    fun updateTokens(@RequestBody data: UpdateUserTokensDto) = authService.updateTokens(data).toHttpResponse()
}