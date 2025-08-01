package org.careerseekers.userservice.exceptions

import org.springframework.http.HttpStatus

class InvalidNumberFormatException(override val message: String) :
    AbstractHttpException(HttpStatus.BAD_REQUEST.value(), message)