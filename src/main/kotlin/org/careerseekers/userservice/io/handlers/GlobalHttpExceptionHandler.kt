package org.careerseekers.userservice.io.handlers

import io.jsonwebtoken.security.SignatureException
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.ConnectionRefusedException
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.InvalidNumberFormatException
import org.careerseekers.userservice.exceptions.JwtAuthenticationException
import org.careerseekers.userservice.exceptions.MobileNumberFormatException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.io.BasicErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalHttpExceptionHandler {

    /**
     * Basic exceptions handler
     * @return ResponseEntity with status 500 and exception message
     */
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception): ResponseEntity<BasicErrorResponse> {
        val errorResponse = BasicErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = "${ex::class}; ${ex.message}",
        )

        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }


    /**
     * Custom project exceptions handler
     */
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = BasicErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = ex.message
        )

        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(DoubleRecordException::class)
    fun handleDoubleRecordException(ex: DoubleRecordException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = BasicErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            message = ex.message
        )

        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(MobileNumberFormatException::class)
    fun handleMobileNumberFormatException(ex: MobileNumberFormatException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = BasicErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = BasicErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidNumberFormatException::class)
    fun handleInvalidNumberFormatException(ex: InvalidNumberFormatException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = BasicErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }


    /**
     * Jwt exceptions handler
     */
    @ExceptionHandler(JwtAuthenticationException::class)
    fun handleJwtAuthenticationException(ex: JwtAuthenticationException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = ex.message?.let {
            BasicErrorResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                message = it
            )
        }

        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(SignatureException::class)
    fun handleSignatureException(ex: SignatureException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = ex.message?.let {
            BasicErrorResponse(
                status = HttpStatus.LOCKED.value(),
                message = "Jwt token verification failed. JWT validity cannot be asserted and should not be trusted."
            )
        }

        return ResponseEntity(errorResponse, HttpStatus.LOCKED)
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(ex: AuthorizationDeniedException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = ex.message?.let {
            BasicErrorResponse(
                status = HttpStatus.FORBIDDEN.value(),
                message = "Access denied. You don't have permission to access this resource."
            )
        }

        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }


    /**
     * WebClient exceptions handler
     */
    @ExceptionHandler(ConnectionRefusedException::class)
    fun handleConnectionRefusedException(ex: ConnectionRefusedException): ResponseEntity<BasicErrorResponse> {
        val errorResponse = BasicErrorResponse(
            status = HttpStatus.SERVICE_UNAVAILABLE.value(),
            message = ex.message
        )

        return ResponseEntity(errorResponse, HttpStatus.SERVICE_UNAVAILABLE)
    }
}