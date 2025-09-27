package org.careerseekers.userservice.mocks.generators

import org.careerseekers.userservice.dto.docs.CreateExpertDocsDto
import org.careerseekers.userservice.mocks.generators.MocksGenerator.randomString
import org.careerseekers.userservice.dto.docs.CreateMentorDocsDto
import org.careerseekers.userservice.dto.docs.CreateTutorDocsDto
import org.careerseekers.userservice.entities.ExpertDocuments
import org.careerseekers.userservice.entities.MentorDocuments
import org.careerseekers.userservice.entities.TutorDocuments
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.mocks.generators.MultipartFileGenerator.createMultipartFile
import kotlin.random.Random

object DocumentsGenerator {

    fun createTutorDocuments(user: Users) = TutorDocuments(
        id = Random.nextLong(1, 10000),
        user = user,
        institution = randomString(12),
        post = randomString(12),
    )

    fun createTutorDocumentsDto(user: Users) = CreateTutorDocsDto(
        userId = user.id,
        institution = randomString(12),
        post = randomString(12),
    )

    fun createMentorDocuments(user: Users) = MentorDocuments(
        id = Random.nextLong(1, 10000),
        user = user,
        institution = randomString(12),
        post = randomString(12),
        consentToMentorPdpId = Random.nextLong(1, 1000)
    )

    fun createMentorDocumentsDto(user: Users) = CreateMentorDocsDto(
        userId = user.id,
        institution = randomString(12),
        post = randomString(12),
        consentToMentorPdp = createMultipartFile()
    )

    fun createExpertDocuments(user: Users) = ExpertDocuments(
        id = Random.nextLong(1, 10000),
        user = user,
        institution = randomString(12),
        post = randomString(12),
    )

    fun createExpertDocumentsDto(user: Users) = CreateExpertDocsDto(
        userId = user.id,
        institution = randomString(12),
        post = randomString(12),
    )
}