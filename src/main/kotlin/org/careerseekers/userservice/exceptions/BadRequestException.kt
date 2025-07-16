package org.careerseekers.userservice.exceptions

import org.springframework.http.HttpStatus

class BadRequestException(override val message: String) :
    AbstractHttpException(HttpStatus.BAD_REQUEST.value(), message)