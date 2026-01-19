package org.careerseekers.userservice.repositories.spec

import jakarta.persistence.criteria.Path
import org.careerseekers.userservice.entities.Children
import org.springframework.data.jpa.domain.Specification

object ChildrenSpecifications {

    fun hasName(name: String?): Specification<Children>? = name?.takeIf { it.trim().isNotEmpty() }?.let { input ->
        Specification { root, _, criteriaBuilder ->
            val firstName: Path<String> = root["firstName"]
            val lastName: Path<String> = root["lastName"]
            val patronymic: Path<String> = root["patronymic"]

            criteriaBuilder.or(
                criteriaBuilder.like(firstName, input),
                criteriaBuilder.like(lastName, input),
                criteriaBuilder.like(patronymic, input)
            )
        }
    }
}