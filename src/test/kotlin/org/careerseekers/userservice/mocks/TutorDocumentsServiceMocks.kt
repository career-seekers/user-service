package org.careerseekers.userservice.mocks

import io.mockk.mockk
import io.mockk.spyk
import org.careerseekers.userservice.mappers.TutorDocumentsMapper
import org.careerseekers.userservice.repositories.TutorDocsRepository
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.TutorDocumentsService
import org.careerseekers.userservice.services.UsersService

open class TutorDocumentsServiceMocks {
    protected val repository = mockk<TutorDocsRepository>()
    protected val usersRepository = mockk<UsersRepository>()
    protected val usersService = mockk<UsersService>()
    protected val tutorDocumentsMapper = mockk<TutorDocumentsMapper>()

    val serviceUnderTest = spyk(TutorDocumentsService(
        repository = repository,
        usersRepository = usersRepository,
        usersService = usersService,
        tutorDocumentsMapper = tutorDocumentsMapper
    ), recordPrivateCalls = true)
}