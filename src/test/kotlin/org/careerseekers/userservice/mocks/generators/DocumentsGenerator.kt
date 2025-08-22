package org.careerseekers.userservice.mocks.generators

import MocksGenerator.randomString
import org.careerseekers.userservice.entities.UserDocuments
import org.careerseekers.userservice.entities.Users
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
        consentToChildPdpId = Random.nextLong(1, 1000)
    )
}