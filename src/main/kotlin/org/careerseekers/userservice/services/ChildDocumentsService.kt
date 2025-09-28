package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.docs.CreateChildDocsDto
import org.careerseekers.userservice.dto.docs.UpdateChildDocsDto
import org.careerseekers.userservice.entities.ChildDocuments
import org.careerseekers.userservice.exceptions.BadRequestException
import org.careerseekers.userservice.exceptions.NotFoundException
import org.careerseekers.userservice.mappers.ChildDocsMapper
import org.careerseekers.userservice.repositories.ChildDocsRepository
import org.careerseekers.userservice.repositories.ChildrenRepository
import org.careerseekers.userservice.services.interfaces.crud.ICreateService
import org.careerseekers.userservice.services.interfaces.crud.IDeleteService
import org.careerseekers.userservice.services.interfaces.crud.IReadService
import org.careerseekers.userservice.services.interfaces.crud.IUpdateService
import org.careerseekers.userservice.utils.DocumentsApiResolver
import org.careerseekers.userservice.utils.SnilsValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ChildDocumentsService(
    override val repository: ChildDocsRepository,
    private val documentsApiResolver: DocumentsApiResolver,
    private val childrenRepository: ChildrenRepository,
    private val childDocsMapper: ChildDocsMapper,
    private val snilsValidator: SnilsValidator,
    private val childService: ChildService,
) : IReadService<ChildDocuments, Long>,
    ICreateService<ChildDocuments, Long, CreateChildDocsDto>,
    IUpdateService<ChildDocuments, Long, UpdateChildDocsDto>,
    IDeleteService<ChildDocuments, Long> {

    fun getByChildId(childId: Long, throwable: Boolean = true): ChildDocuments? {
        val docs = repository.findByChildId(childId)

        if (throwable && docs == null) {
            throw NotFoundException("Документы участника не найдены.")
        }
        return docs
    }

    fun getBySnilsNumber(snilsNumber: String, throwable: Boolean = true): ChildDocuments? {
        val docs = repository.findBySnilsNumber(snilsNumber)

        if (throwable && docs == null) {
            throw NotFoundException("Документы с номером СНИЛС $snilsNumber не найдены.")
        }
        return docs
    }

    @Transactional
    override fun create(item: CreateChildDocsDto): ChildDocuments {
        item.child = childService.getById(item.childId, message = "Ребёнок с ID ${item.childId} не найден.")!!

        snilsValidator.validateSnilsNumber(item.snilsNumber)
        snilsValidator.checkSnilsValid(item.snilsNumber)

        item.apply {
            snilsId = documentsApiResolver.loadDocId("uploadSnils", snilsFile)
            studyingCertificateId = documentsApiResolver.loadDocId("uploadStudyingCertificate", studyingCertificateFile)
            additionalStudyingCertificateId =
                documentsApiResolver.loadDocId("uploadAdditionalStudyingCertificate", additionalStudyingCertificateFile)
            consentToChildPdpId = documentsApiResolver.loadDocId("uploadConsentToChildPDP", consentToChildPdpFile)
            birthCertificateId = documentsApiResolver.loadDocId("uploadBirthCertificate", birthCertificateFile)
        }

        return repository.save(childDocsMapper.childDocsFromDto(item))
    }

    @Transactional
    override fun update(item: UpdateChildDocsDto): String {
        fun require(value: Boolean, lazyMessage: () -> Any = { "Exception." }) {
            if (!value) throw BadRequestException(lazyMessage().toString())
        }

        getById(item.id, message = "Документы с ID ${item.id} не найдены.")!!.apply {
            item.snilsNumber?.let {
                require(it.isNotBlank()) { "СНИЛС не может быть пустой строкой" }
                if (it != snilsNumber) {
                    snilsValidator.validateSnilsNumber(it)
                    snilsValidator.checkSnilsValid(it)

                    snilsNumber = it.trim()
                }
            }
            item.studyingPlace?.let {
                require(it.isNotBlank()) { "Место обучение не может быть пустой строкой" }
                studyingPlace = it.trim()
            }
            item.learningClass?.let { learningClass = it }
            item.trainingGround?.let {
                require(it.isNotBlank()) { "Площадка тренировки не может быть пустой строкой" }

                trainingGround = it.trim()
            }
            item.parentRole?.let {
                require(it.isNotBlank()) { "Роль родителя не может быть пустой строкой" }
                parentRole = it.trim()
            }

            item.snilsFile?.let { updateDoc(snilsId, it, "uploadSnils") }
            item.studyingCertificateFile?.let { updateDoc(studyingCertificateId, it, "uploadStudyingCertificate") }
            item.additionalStudyingCertificateFile?.let {
                updateDoc(
                    additionalStudyingCertificateId,
                    it,
                    "uploadAdditionalStudyingCertificate"
                )
            }
            item.consentToChildPdpFile?.let { updateDoc(consentToChildPdpId, it, "uploadConsentToChildPDP") }
            item.birthCertificate?.let { updateDoc(birthCertificateId, it, "uploadBirthCertificate") }
        }.also(repository::save)

        return "Информация о документах участника обновлена успешно."
    }

    private fun updateDoc(oldId: Long, newDoc: MultipartFile, url: String): Long {
        return documentsApiResolver.loadDocId(url, newDoc)?.let { id ->
            documentsApiResolver.deleteDocument(oldId, false)
            id
        } ?: throw BadRequestException("ID документа не может быть null.")

    }

    @Transactional
    override fun deleteById(id: Long): String {
        getById(id, message = "Документы с ID $id не найдены.")!!.let {docs ->
            docs.child.childDocuments = null
            childrenRepository.save(docs.child)

            repository.delete(docs)
        }

        return "Документы участника удалены успешно."
    }

    @Transactional
    override fun deleteAll(): String {
        getAll().forEach { doc ->
            doc.child.childDocuments = null
            childrenRepository.save(doc.child)
        }.also { repository.deleteAll() }

        return "Все документы участников удалены успешно."
    }
}