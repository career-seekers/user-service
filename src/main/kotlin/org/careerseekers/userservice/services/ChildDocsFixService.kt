package org.careerseekers.userservice.services

import jakarta.transaction.Transactional
import org.careerseekers.userservice.enums.DirectionAgeCategory
import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.io.converters.convertDateToLocalDate
import org.careerseekers.userservice.repositories.ChildDocsRepository
import org.careerseekers.userservice.utils.AgeCalculator.calculateAge
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ChildDocsFixService(
    private val childDocumentsService: ChildDocumentsService,
    private val childDocsRepository: ChildDocsRepository
) {

    @Transactional
    fun fixChildDocs(): BasicSuccessfulResponse<MutableList<Pair<String, String>>> {
        val resolvedProblems = mutableListOf<Pair<String, String>>()

        childDocumentsService.getAll()
            .filter { it.learningClass == 0.toShort() }
            .forEach { docs ->
                val child = docs.child
                val normalizedAgeCategory = DirectionAgeCategory.getAgeCategory(
                    calculateAge(
                        convertDateToLocalDate(child.dateOfBirth),
                        LocalDate.of(2025, 12, 7)
                    ), docs.learningClass
                )

                if (docs.ageCategory != normalizedAgeCategory) {
                    docs.ageCategory = normalizedAgeCategory
                    childDocsRepository.save(docs)

                    resolvedProblems.add(Pair("Child: ${docs.child.id}", "Docs: ${docs.id}"))
                }
            }

        return BasicSuccessfulResponse(resolvedProblems)
    }
}