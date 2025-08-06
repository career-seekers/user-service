package org.careerseekers.userservice.controllers.interfaces.crud

import org.careerseekers.userservice.controllers.interfaces.BasicRestController
import org.careerseekers.userservice.services.interfaces.crud.ICreateService

interface ICreateController<T, ID, CrDTO> : BasicRestController {
    override val service: ICreateService<T, ID, CrDTO>

    fun create(item: CrDTO): Any
}