package org.careerseekers.userservice.exceptions

import org.springframework.http.HttpStatus

class MobileNumberFormatException(override val message: String) :
    AbstractHttpException(HttpStatus.BAD_REQUEST.value(), message)