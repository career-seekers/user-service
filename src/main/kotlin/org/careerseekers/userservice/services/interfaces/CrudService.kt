package org.careerseekers.userservice.services.interfaces

import org.careerseekers.userservice.services.interfaces.crud.ICreateService
import org.careerseekers.userservice.services.interfaces.crud.IDeleteService
import org.careerseekers.userservice.services.interfaces.crud.IReadService
import org.careerseekers.userservice.services.interfaces.crud.IUpdateService
import org.springframework.transaction.annotation.Transactional

interface CrudService<T, ID, CrDTO, UpDTO> :
    IReadService<T, ID>,
    ICreateService<T, ID, CrDTO>,
    IUpdateService<T, ID, UpDTO>,
    IDeleteService<T, ID>
{
    @Transactional
    fun createAll(items: List<CrDTO>): Any
}