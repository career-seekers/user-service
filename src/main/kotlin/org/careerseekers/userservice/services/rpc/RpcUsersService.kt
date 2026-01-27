package org.careerseekers.userservice.services.rpc

import com.careerseekers.grpc.users.*
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.careerseekers.userservice.io.converters.extensions.rpc.toList
import org.careerseekers.userservice.io.converters.extensions.rpc.toRpcList
import org.careerseekers.userservice.io.converters.extensions.rpc.toRpcUser
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.utils.builders.RpcUserBuilder

@GrpcService
class RpcUsersService(
    private val rpcUserBuilder: RpcUserBuilder,
    private val usersRepository: UsersRepository,
) : UsersServiceGrpc.UsersServiceImplBase() {

    override fun getById(request: UserId, responseObserver: StreamObserver<User>) {
        val response = rpcUserBuilder.buildRpcUser(request.id)

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getUsersByIds(request: UserIds, responseObserver: StreamObserver<Users>) {
        val response = usersRepository
            .findAllById(request.toList())
            .map { it.toRpcUser() }
            .toRpcList()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}