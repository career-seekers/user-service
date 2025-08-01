package org.careerseekers.userservice.services.interfaces.crud

import org.careerseekers.userservice.services.interfaces.BasicApiService
import org.springframework.transaction.annotation.Transactional

interface ICreateService<T, ID, CrDTO> : BasicApiService<T, ID> {
    @Transactional
    fun create(item: CrDTO): Any
}