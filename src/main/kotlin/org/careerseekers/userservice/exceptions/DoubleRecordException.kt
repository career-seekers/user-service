package org.careerseekers.userservice.exceptions

import org.springframework.http.HttpStatus

class DoubleRecordException(override val message: String = "Object with this params already exists.") :
    AbstractHttpException(HttpStatus.CONFLICT.value(), message)