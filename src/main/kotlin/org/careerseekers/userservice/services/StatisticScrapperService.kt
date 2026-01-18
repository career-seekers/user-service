package org.careerseekers.userservice.services

import org.careerseekers.userservice.dto.statistics.UsersStatisticPairDto
import org.careerseekers.userservice.entities.Children
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.enums.VerificationStatuses
import org.careerseekers.userservice.utils.storages.StatisticsStorage
import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.stereotype.Service

@Service
class StatisticScrapperService(
    private val usersService: UsersService,
    private val childService: ChildService
) : SmartInitializingSingleton {

    override fun afterSingletonsInstantiated() {
        val allUsers = usersService.getAll()
        val allChildren = childService.getAll()

        setTutorsInfo(allUsers.filter { it.role == UsersRoles.TUTOR })
        setExpertsInfo(allUsers.filter { it.role == UsersRoles.EXPERT })
        setMentorInfo(allUsers.filter { it.role == UsersRoles.MENTOR })
        setUsersInfo(allUsers.filter { it.role == UsersRoles.USER })
        setChildrenCount(allChildren)
    }

    fun setTutorsInfo(users: List<Users>) =
        StatisticsStorage.setTutorsInfo(UsersStatisticPairDto(users.size, users.filter { it.verified == VerificationStatuses.ACCEPTED }.size))

    fun setExpertsInfo(users: List<Users>) =
        StatisticsStorage.setExpertsInfo(UsersStatisticPairDto(users.size, users.filter { it.verified == VerificationStatuses.ACCEPTED }.size))

    fun setMentorInfo(users: List<Users>) =
        StatisticsStorage.setMentorsInfo(UsersStatisticPairDto(users.size, users.filter { it.verified == VerificationStatuses.ACCEPTED }.size))

    fun setUsersInfo(users: List<Users>) =
        StatisticsStorage.setUsersInfo(UsersStatisticPairDto(users.size, users.filter { it.verified == VerificationStatuses.ACCEPTED }.size))

    fun setChildrenCount(children: List<Children>) =
        StatisticsStorage.setChildrenCount(children.size)
}