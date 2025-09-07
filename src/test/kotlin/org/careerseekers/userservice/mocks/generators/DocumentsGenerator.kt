package org.careerseekers.userservice.mocks.generators

import org.careerseekers.userservice.dto.docs.CreateExpertDocsDto
import org.careerseekers.userservice.mocks.generators.MocksGenerator.randomString
import org.careerseekers.userservice.dto.docs.CreateMentorDocsDto
import org.careerseekers.userservice.dto.docs.CreateTutorDocsDto
import org.careerseekers.userservice.dto.docs.CreateUserDocsDto
import org.careerseekers.userservice.dto.docs.SnilsDto
import org.careerseekers.userservice.entities.ExpertDocuments
import org.careerseekers.userservice.entities.MentorDocuments
import org.careerseekers.userservice.entities.TutorDocuments
import org.careerseekers.userservice.entities.UserDocuments
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.mocks.generators.MultipartFileGenerator.createMultipartFile
import kotlin.random.Random

object DocumentsGenerator {

    fun createUserDocs(user: Users) = UserDocuments(
        id = Random.nextLong(1, 10000),
        user = user,
        snilsNumber = randomString(12),
        snilsId = Random.nextLong(1, 1000),
        studyingPlace = randomString(20),
        studyingCertificateId = Random.nextLong(1, 1000),
        learningClass = Random.nextLong(1, 11).toShort(),
        trainingGround = randomString(20),
        additionalStudyingCertificateId = Random.nextLong(1, 1000),
        parentRole = randomString(20),
        consentToChildPdpId = Random.nextLong(1, 1000),
        birthCertificateId = Random.nextLong(1, 1000),
    )

    fun createUserDocsDto(user: Users) = CreateUserDocsDto(
        userId = user.id,
        snilsDto = SnilsDto(
            snilsNumber = randomString(12),
            snilsFile = createMultipartFile(),
        ),
        studyingPlace = randomString(20),
        studyingCertificateFile = createMultipartFile(),
        learningClass = Random.nextLong(1, 11).toShort(),
        trainingGround = randomString(20),
        additionalStudyingCertificateFile = createMultipartFile(),
        parentRole = randomString(20),
        consentToChildPdpFile = createMultipartFile(),
        birthCertificateFile = createMultipartFile(),
    )

    fun createTutorDocuments(user: Users) = TutorDocuments(
        id = Random.nextLong(1, 10000),
        user = user,
        institution = randomString(12),
        post = randomString(12),
        consentToTutorPdpId = Random.nextLong(1, 1000)
    )

    fun createTutorDocumentsDto(user: Users) = CreateTutorDocsDto(
        userId = user.id,
        institution = randomString(12),
        post = randomString(12),
        consentToTutorPdp = createMultipartFile()
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
        consentToExpertPdpId = Random.nextLong(1, 1000)
    )

    fun createExpertDocumentsDto(user: Users) = CreateExpertDocsDto(
        userId = user.id,
        institution = randomString(12),
        post = randomString(12),
        consentToExpertPdp = createMultipartFile()
    )
}