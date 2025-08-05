package org.careerseekers.userservice.controllers.interfaces.crud

import org.careerseekers.userservice.services.interfaces.crud.IReadService

interface IReadController<T, ID> {
    val service: IReadService<T, ID>

    fun getAll(): List<T>
    fun getById(id: Long): T?
}