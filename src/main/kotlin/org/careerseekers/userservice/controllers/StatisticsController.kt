package org.careerseekers.userservice.controllers

import org.careerseekers.userservice.io.BasicSuccessfulResponse
import org.careerseekers.userservice.utils.storages.StatisticsStorage
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users-service/v1/statistics")
class StatisticsController {

    @GetMapping("/getTutorsInfo")
    fun getTutorsInfo() = StatisticsStorage.tutorsInfo.toHttpResponse()

    @GetMapping("/getExpertsInfo")
    fun getExpertsInfo() = StatisticsStorage.expertsInfo.toHttpResponse()

    @GetMapping("/getMentorsInfo")
    fun getMentorsInfo() = StatisticsStorage.mentorsInfo.toHttpResponse()

    @GetMapping("/getUsersInfo")
    fun getUsersInfo() = StatisticsStorage.usersInfo.toHttpResponse()

    @GetMapping("/getChildrenCount")
    fun getChildrenCount() = BasicSuccessfulResponse(StatisticsStorage.childrenCount)
}