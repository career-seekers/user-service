package org.careerseekers.userservice.exceptions

import org.springframework.http.HttpStatus

class ConnectionRefusedException(override val message: String) :
    AbstractHttpException(HttpStatus.SERVICE_UNAVAILABLE.value(), message)