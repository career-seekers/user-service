package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.MentorDocuments
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MentorDocsRepository : JpaRepository<MentorDocuments, Long>