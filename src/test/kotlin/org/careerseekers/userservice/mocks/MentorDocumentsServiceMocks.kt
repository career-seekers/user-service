package org.careerseekers.userservice.mocks

import io.mockk.mockk
import io.mockk.spyk
import org.careerseekers.userservice.mappers.MentorsDocumentsMapper
import org.careerseekers.userservice.repositories.MentorDocsRepository
import org.careerseekers.userservice.repositories.UsersRepository
import org.careerseekers.userservice.services.MentorDocumentsService
import org.careerseekers.userservice.services.UsersService
import org.careerseekers.userservice.utils.DocumentsApiResolver

open class MentorDocumentsServiceMocks {

    protected val repository = mockk<MentorDocsRepository>()
    protected val usersRepository = mockk<UsersRepository>()
    protected val usersService = mockk<UsersService>()
    protected val documentsApiResolver = mockk<DocumentsApiResolver>()
    protected val mentorsDocumentsMapper = mockk<MentorsDocumentsMapper>()

    protected val serviceUnderTest = spyk(MentorDocumentsService(
        repository = repository,
        usersRepository = usersRepository,
        usersService = usersService,
        documentsApiResolver = documentsApiResolver,
        mentorsDocumentsMapper = mentorsDocumentsMapper
    ))
}