package org.careerseekers.userservice.repositories.spec

import jakarta.persistence.criteria.Path
import org.careerseekers.userservice.entities.ChildDocuments
import org.careerseekers.userservice.entities.Children
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.DirectionAgeCategory
import org.springframework.data.jpa.domain.Specification

object ChildDocumentsSpecifications {

    fun hasChildId(id: Long?): Specification<ChildDocuments>? = id?.let {
        Specification { root, _, criteriaBuilder ->
            val child: Path<Children> = root["child"]
            val childId: Path<Long> = child["id"]

            criteriaBuilder.equal(childId, id)
        }
    }

    fun hasUserId(id: Long?): Specification<ChildDocuments>? = id?.let {
        Specification { root, _, criteriaBuilder ->
            val child: Path<Children> = root["child"]
            val user: Path<Users> = child["user"]
            val userId: Path<Long> = user["id"]

            criteriaBuilder.equal(userId, id)
        }
    }

    fun hasAgeCategory(category: DirectionAgeCategory?): Specification<ChildDocuments>? = category?.let {
        Specification { root, _, criteriaBuilder ->
            val ageCategory: Path<DirectionAgeCategory> = root["ageCategory"]

            criteriaBuilder.equal(ageCategory, category)
        }
    }
}