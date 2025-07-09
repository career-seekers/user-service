package org.careerseekers.userservice.services.interfaces

import org.careerseekers.userservice.exceptions.NotFoundException
import org.springframework.transaction.annotation.Transactional

interface CrudService<T, ID, CrDTO, UpDTO> : BasicApiService<T, ID> {
    fun getAll(): List<T> = repository.findAll()
    fun getById(id: ID, throwable: Boolean = true, message: String = "Object with id $id not found."): T? {
        val o = id!!.let { repository.findById(it) }

        if (throwable && !o.isPresent) {
            throw NotFoundException(message)
        }
        return if (!o.isPresent) null else o.get()
    }

    @Transactional
    fun create(item: CrDTO): Any

    @Transactional
    fun createAll(items: List<CrDTO>): Any

    @Transactional
    fun update(item: UpDTO): Any

    @Transactional
    fun deleteById(id: ID): Any?

    @Transactional
    fun deleteAll(): Any? = repository.deleteAll()
}