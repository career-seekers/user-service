package org.careerseekers.userservice.mocks

import io.mockk.mockk
import io.mockk.spyk
import org.careerseekers.userservice.mappers.ExpertDocumentsMapper
import org.careerseekers.userservice.repositories.ExpertDocsRepository
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.ExpertDocumentsService
import org.careerseekers.userservice.services.UsersService

open class ExpertDocumentsServiceMocks {

    protected val repository = mockk<ExpertDocsRepository>()
    protected val usersRepository = mockk<UsersRepository>()
    protected val usersService = mockk<UsersService>()
    protected val expertDocumentsMapper = mockk<ExpertDocumentsMapper>()

    protected val serviceUnderTest = spyk(
        ExpertDocumentsService(
            repository = repository,
            usersRepository = usersRepository,
            usersService = usersService,
            expertDocumentsMapper = expertDocumentsMapper
        )
    )
}