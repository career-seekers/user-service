package org.careerseekers.userservice.controllers.interfaces

import org.careerseekers.userservice.services.interfaces.BasicApiService
import org.springframework.data.jpa.repository.JpaRepository

interface BasicRestController {
    val service: BasicApiService<*, *>
    val repository: JpaRepository<*, *>
}