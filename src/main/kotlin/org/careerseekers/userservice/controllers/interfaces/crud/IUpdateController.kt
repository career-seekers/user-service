package org.careerseekers.userservice.controllers.interfaces.crud

import org.careerseekers.userservice.controllers.interfaces.BasicRestController
import org.careerseekers.userservice.services.interfaces.crud.IUpdateService

interface IUpdateController<T, ID, UpDTO> : BasicRestController {
    override val service: IUpdateService<T, ID, UpDTO>

    fun update(item: UpDTO): Any
}