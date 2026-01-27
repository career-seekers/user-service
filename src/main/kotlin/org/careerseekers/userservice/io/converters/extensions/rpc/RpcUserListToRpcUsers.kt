package org.careerseekers.userservice.io.converters.extensions.rpc

import com.careerseekers.grpc.users.User
import com.careerseekers.grpc.users.Users

fun List<User>.toRpcList(): Users {
    return Users.newBuilder()
        .addAllUsers(this)
        .build()
}