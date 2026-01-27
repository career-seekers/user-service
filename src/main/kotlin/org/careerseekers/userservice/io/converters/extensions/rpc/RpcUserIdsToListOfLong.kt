package org.careerseekers.userservice.io.converters.extensions.rpc

import com.careerseekers.grpc.users.UserIds

fun UserIds.toList(): List<Long> = this.idsList.map { it.id }