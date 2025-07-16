package org.careerseekers.userservice.io

interface AbstractResponse<T> {
    val status: Int
    val message: T?
}