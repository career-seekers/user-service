package org.careerseekers.userservice.services.rpc

import com.careerseekers.grpc.children.*
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.careerseekers.userservice.services.ChildService
import org.careerseekers.userservice.utils.builders.RpcChildBuilder

@GrpcService
class RpcChildService(
    private val childService: ChildService,
    private val rpcChildBuilder: RpcChildBuilder
) : ChildrenServiceGrpc.ChildrenServiceImplBase() {

    override fun getById(request: Id, responseObserver: StreamObserver<ShortChild>) {
        val response = rpcChildBuilder.buildRpcChild(request.id)

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getByIdFull(request: Id, responseObserver: StreamObserver<FullChild>) {
        val response = rpcChildBuilder.buildRpcFullChild(request.id)

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun getAll(request: Empty, responseObserver: StreamObserver<ChildrenList>) {
        val childrenList = ChildrenList.newBuilder()

        childService.getAll()
            .map { child -> rpcChildBuilder.buildRpcChild(child.id) }
            .run { childrenList.addAllChildren(this) }

        responseObserver.onNext(childrenList.build())
        responseObserver.onCompleted()
    }

    override fun getAllFull(request: Empty, responseObserver: StreamObserver<FullChildrenList>) {
        val childrenList = FullChildrenList.newBuilder()

        childService.getAll()
            .map { child -> rpcChildBuilder.buildRpcFullChild(child.id) }
            .run { childrenList.addAllChildren(this) }

        responseObserver.onNext(childrenList.build())
        responseObserver.onCompleted()
    }
}