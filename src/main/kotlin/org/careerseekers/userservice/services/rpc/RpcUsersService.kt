package org.careerseekers.userservice.services.rpc

import com.careerseekers.grpc.users.*
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.careerseekers.userservice.utils.builders.RpcUserBuilder

@GrpcService
class RpcUsersService(
    private val rpcUserBuilder: RpcUserBuilder
) : UsersServiceGrpc.UsersServiceImplBase() {

    override fun getById(request: UserId, responseObserver: StreamObserver<User>) {
        val response = rpcUserBuilder.buildRpcUser(request.id)

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}