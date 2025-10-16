package org.careerseekers.userservice.exceptions

import org.springframework.http.HttpStatus

class AccessBlockedException(override val message: String) :
    AbstractHttpException(HttpStatus.FORBIDDEN.value(), message)