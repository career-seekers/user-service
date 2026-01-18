package org.careerseekers.userservice.repositories.spec

import jakarta.persistence.criteria.Path
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.enums.VerificationStatuses
import org.springframework.data.jpa.domain.Specification

object UsersSpecifications {

    fun hasAnyRole(roles: List<UsersRoles>?): Specification<Users>? = roles?.let { targetRoles ->
        Specification<Users> { root, _, criteriaBuilder ->
            val rolePath: Path<UsersRoles> = root["role"]

            criteriaBuilder.or(*targetRoles.map {
                criteriaBuilder.equal(rolePath, it)
            }.toTypedArray())
        }
    }

    fun hasVerified(verified: VerificationStatuses?): Specification<Users>? = verified?.let {
        Specification { root, _, criteriaBuilder ->
            val verifiedPath: Path<VerificationStatuses> = root["verified"]
            criteriaBuilder.equal(verifiedPath, it)
        }
    }
}