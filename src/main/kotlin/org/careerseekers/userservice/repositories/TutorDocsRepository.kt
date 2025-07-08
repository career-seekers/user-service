package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.TutorDocuments
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TutorDocsRepository : JpaRepository<TutorDocuments, Long>