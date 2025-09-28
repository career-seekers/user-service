package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.dto.auth.CodeVerificationDto
import org.careerseekers.userservice.dto.auth.ForgotPasswordDto
import org.careerseekers.userservice.dto.auth.LoginUserDto
import org.careerseekers.userservice.dto.auth.PreRegisterUserDto
import org.careerseekers.userservice.dto.auth.ResetPasswordDto
import org.careerseekers.userservice.dto.auth.UpdateUserTokensDto
import org.careerseekers.userservice.dto.auth.UserRegistrationDto
import org.careerseekers.userservice.io.converters.extensions.toHttpResponse
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
    @PostMapping("/preRegister")
    fun preRegister(@RequestBody data: PreRegisterUserDto) = authService.preRegister(data)

    @PostMapping("/register")
    fun register(@RequestBody data: UserRegistrationDto) = authService.register(data).toHttpResponse()

    @PostMapping("/login")
    fun login(@RequestBody data: LoginUserDto) = authService.login(data).toHttpResponse()

    @PostMapping("/updateTokens")
    fun updateTokens(@RequestBody data: UpdateUserTokensDto) = authService.updateTokens(data).toHttpResponse()

    @PostMapping("/forgotPassword")
    fun forgotPassword(@RequestBody data: ForgotPasswordDto) = authService.forgotPassword(data).toHttpResponse()

    @PostMapping("/verifyCode")
    fun verifyCode(@RequestBody data: CodeVerificationDto) = authService.verifyCode(data).toHttpResponse()

    @PostMapping("/resetPassword")
    fun resetPassword(@RequestBody data: ResetPasswordDto) = authService.resetPassword(data).toHttpResponse()
}