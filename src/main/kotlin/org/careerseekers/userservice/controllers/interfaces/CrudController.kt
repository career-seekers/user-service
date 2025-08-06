package org.careerseekers.userservice.controllers.interfaces

import org.careerseekers.userservice.controllers.interfaces.crud.ICreateController
import org.careerseekers.userservice.controllers.interfaces.crud.IDeleteController
import org.careerseekers.userservice.controllers.interfaces.crud.IReadController
import org.careerseekers.userservice.controllers.interfaces.crud.IUpdateController
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.services.interfaces.CrudService

interface CrudController<T, ID, CrDTO, UpDTO>
    : IReadController<T, ID>,
    ICreateController<T, ID, CrDTO>,
    IUpdateController<T, ID, UpDTO>,
    IDeleteController<T, ID> {
    override val service: CrudService<T, ID, CrDTO, UpDTO>

    fun createAll(items: List<CrDTO>): BasicSuccessfulResponse<*>
}