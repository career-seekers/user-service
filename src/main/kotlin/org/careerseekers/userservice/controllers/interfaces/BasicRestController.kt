package org.careerseekers.userservice.controllers.interfaces

import org.careerseekers.userservice.services.interfaces.BasicApiService

interface BasicRestController {
    val service: BasicApiService<*, *>
}