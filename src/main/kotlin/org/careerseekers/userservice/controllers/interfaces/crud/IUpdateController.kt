package org.careerseekers.userservice.controllers.interfaces.crud

import org.careerseekers.userservice.services.interfaces.crud.IUpdateService

interface IUpdateController<T, ID, UpDTO> {
    val service: IUpdateService<T, ID, UpDTO>

    fun update(item: UpDTO): Any
}