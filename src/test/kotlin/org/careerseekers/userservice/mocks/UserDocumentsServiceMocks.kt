package org.careerseekers.userservice.mocks

import io.mockk.mockk
import org.careerseekers.userservice.mappers.UserDocumentsMapper
import org.careerseekers.userservice.repositories.UserDocsRepository
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.UserDocumentsService
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.utils.DocumentsApiResolver
import org.careerseekers.userservice.utils.SnilsValidator

open class UserDocumentsServiceMocks {
    protected val repository = mockk<UserDocsRepository>()
    protected val usersRepository = mockk<UsersRepository>()
    protected val usersService = mockk<UsersService>()
    protected val documentsApiResolver = mockk<DocumentsApiResolver>()
    protected val userDocumentsMapper = mockk<UserDocumentsMapper>()
    protected val snilsValidator = mockk<SnilsValidator>()

    protected val userDocumentsServiceMock = mockk<UserDocumentsService>(relaxed = true)
    protected val serviceUnderTest = UserDocumentsService(
        repository = repository,
        usersRepository = usersRepository,
        usersService = usersService,
        documentsApiResolver = documentsApiResolver,
        userDocumentsMapper = userDocumentsMapper,
        snilsValidator = snilsValidator
    )
}