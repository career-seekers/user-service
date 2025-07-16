package org.careerseekers.userservice.io

import kotlinx.serialization.Serializable

@Serializable
data class BasicErrorResponse(override val status: Int, override val message: String) :
    AbstractResponse<String>
