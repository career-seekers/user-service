package org.careerseekers.userservice.services.rpc

import com.careerseekers.grpc.jwt.Empty
import com.careerseekers.grpc.jwt.JwtServiceGrpc
import com.careerseekers.grpc.jwt.JwtToken
import com.careerseekers.grpc.jwt.JwtTokensList
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.careerseekers.userservice.repositories.JwtTokensRepository

@GrpcService
class JwtService(
    private val jwtTokensRepository: JwtTokensRepository
) : JwtServiceGrpc.JwtServiceImplBase() {
    override fun getAllJwtTokens(
        request: Empty, responseObserver: StreamObserver<JwtTokensList>
    ) {
        var grpcTokens = mutableListOf<JwtToken>()
        jwtTokensRepository.findAll().map {
            val token = JwtToken.newBuilder()
                .setId(it.id)
                .setEmail(it.user.email)
                .setRole(it.user.role.toString())
                .setUuid(it.uuid.toString())
                .setToken(it.token)
                .build()

            grpcTokens.add(token)
        }

        val response = JwtTokensList.newBuilder()
            .addAllTokens(grpcTokens)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}