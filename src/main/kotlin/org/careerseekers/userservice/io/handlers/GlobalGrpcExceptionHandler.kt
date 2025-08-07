package org.careerseekers.userservice.io.handlers

import io.grpc.Status
import io.grpc.StatusRuntimeException
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import org.careerseekers.userservice.exceptions.DoubleRecordException
import org.careerseekers.userservice.exceptions.NotFoundException

@GrpcAdvice
class GlobalGrpcExceptionHandler {

    @GrpcExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): StatusRuntimeException {
        return Status.NOT_FOUND
            .withDescription(ex.message)
            .asRuntimeException()
    }

    @GrpcExceptionHandler(DoubleRecordException::class)
    fun handleDoubleRecordException(ex: DoubleRecordException): StatusRuntimeException {
        return Status.ALREADY_EXISTS
            .withDescription(ex.message)
            .asRuntimeException()
    }

    @GrpcExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): StatusRuntimeException {
        return Status.INTERNAL
            .withDescription("Internal server error: ${ex.message}")
            .asRuntimeException()
    }
}