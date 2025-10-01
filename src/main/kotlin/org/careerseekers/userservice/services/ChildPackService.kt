package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.docs.CreateChildDocsDto
import org.careerseekers.userservice.dto.users.CreateChildDto
import org.careerseekers.userservice.dto.users.CreateChildPackDto
import org.careerseekers.userservice.entities.Children
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChildPackService(
    private val childService: ChildService,
    private val childDocumentsService: ChildDocumentsService,
) {

    @Transactional
    fun createChildPack(item: CreateChildPackDto): Children {
        val child = childService.create(CreateChildDto(
            lastName = item.lastName,
            firstName = item.firstName,
            patronymic = item.patronymic,
            dateOfBirth = item.dateOfBirth,
            createdAt = item.createdAt,
            userId = item.userId,
            mentorId = item.mentorId,
        ))

        childDocumentsService.create(CreateChildDocsDto(
            childId = child.id,
            child = child,
            snilsNumber = item.snilsNumber,
            snilsFile = item.snilsFile,
            studyingPlace = item.studyingPlace,
            studyingCertificateFile = item.studyingCertificateFile,
            learningClass = item.learningClass,
            trainingGround = item.trainingGround,
            additionalStudyingCertificateFile = item.additionalStudyingCertificateFile,
            parentRole = item.parentRole,
            consentToChildPdpFile = item.consentToChildPdpFile,
            birthCertificateFile = item.birthCertificateFile,
        ))

        return child
    }
}