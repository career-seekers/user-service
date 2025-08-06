package org.careerseekers.userservice.controllers.interfaces.crud

import org.careerseekers.userservice.services.interfaces.crud.ICreateService

interface ICreateController<T, ID, CrDTO> {
    val service: ICreateService<T, ID, CrDTO>

    fun create(item: CrDTO): Any
}