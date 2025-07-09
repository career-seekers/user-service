package org.careerseekers.userservice.controllers.interfaces

import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.services.interfaces.CrudService
import org.springframework.data.jpa.repository.JpaRepository

interface CrudController<T, ID, CrDTO, UpDTO> : BasicRestController {
    override val service: CrudService<T, ID, CrDTO, UpDTO>
    override val repository: JpaRepository<T, ID>

    fun getAll(): BasicSuccessfulResponse<List<T>>
    fun getById(id: ID): BasicSuccessfulResponse<T>

    fun create(item: CrDTO) : BasicSuccessfulResponse<*>
    fun createAll(items: List<CrDTO>) : BasicSuccessfulResponse<*>

    fun update(item: UpDTO) : BasicSuccessfulResponse<*>

    fun deleteById(id: ID) : BasicSuccessfulResponse<*>
    fun deleteAll() : BasicSuccessfulResponse<*>
}