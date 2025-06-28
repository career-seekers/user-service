package org.careerseekers.userservice.io

abstract class AbstractResponse<T> (
    open val status: Int,
    open val message: T?
)