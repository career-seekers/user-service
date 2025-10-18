package org.careerseekers.userservice.annotations

import org.careerseekers.userservice.enums.ReviewStatus

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Tested(
    val testedBy: String = "",
    val createdOn: String = "",
    val reviewStatus: ReviewStatus = ReviewStatus.NOT_REVIEWED,
    val comments: String = ""
)

