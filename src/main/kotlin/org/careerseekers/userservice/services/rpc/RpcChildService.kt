package org.careerseekers.userservice.services.rpc

import com.careerseekers.grpc.children.*
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
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

        runBlocking {
            val deferredChildren = childService.getAll()
                .sortedBy { it.id }
                .map { child ->
                    async(Dispatchers.Default) {
                        rpcChildBuilder.buildRpcChild(child)
                    }
                }

            val resultChildren = deferredChildren.awaitAll()
            childrenList.addAllChildren(resultChildren)
        }

        responseObserver.onNext(childrenList.build())
        responseObserver.onCompleted()
    }

    override fun getAllFull(request: Empty, responseObserver: StreamObserver<FullChildrenList>) {
        val childrenList = FullChildrenList.newBuilder()

        runBlocking {
            val deferredChildren = childService.getAll()
                .sortedBy { it.id }
                .map { child ->
                    async(Dispatchers.Default) {
                        rpcChildBuilder.buildRpcFullChild(child)
                    }
                }

            val resultChildren = deferredChildren.awaitAll()
            childrenList.addAllChildren(resultChildren)
        }

        responseObserver.onNext(childrenList.build())
        responseObserver.onCompleted()
    }
}