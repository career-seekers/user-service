package org.careerseekers.userservice.io

import org.springframework.http.HttpStatus

data class BasicSuccessfulResponse<T>(
    override val message: T
) : AbstractResponse<T>(HttpStatus.OK.value(), message)