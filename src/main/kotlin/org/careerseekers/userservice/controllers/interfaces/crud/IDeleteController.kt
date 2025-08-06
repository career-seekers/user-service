package org.careerseekers.userservice.controllers.interfaces.crud

import org.careerseekers.userservice.services.interfaces.crud.IDeleteService

interface IDeleteController<T, ID> {
    val service : IDeleteService<T, ID>

    fun deleteAll(): Any
    fun deleteById(id : ID): Any
}