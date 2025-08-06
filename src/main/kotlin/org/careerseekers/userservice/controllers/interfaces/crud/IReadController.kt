package org.careerseekers.userservice.controllers.interfaces.crud

import org.careerseekers.userservice.controllers.interfaces.BasicRestController
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.services.interfaces.crud.IReadService

interface IReadController<T, ID> : BasicRestController {
    override val service: IReadService<T, ID>

    fun getAll(): BasicSuccessfulResponse<List<T>>
    fun getById(id: ID): BasicSuccessfulResponse<T>
}