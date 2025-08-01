package org.careerseekers.userservice.services.interfaces.crud

import org.careerseekers.userservice.services.interfaces.BasicApiService
import org.springframework.transaction.annotation.Transactional

interface IDeleteService<T, ID> : BasicApiService<T, ID> {
    @Transactional
    fun deleteById(id: ID): Any?

    @Transactional
    fun deleteAll(): Any? = repository.deleteAll()
}