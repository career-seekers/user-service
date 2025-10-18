package org.careerseekers.userservice.utils

import org.careerseekers.userservice.entities.Children
import org.careerseekers.userservice.entities.Users
import org.careerseekers.userservice.enums.UsersRoles
import org.careerseekers.userservice.services.ChildService
import org.careerseekers.userservice.services.UsersService
import org.springframework.beans.factory.SmartInitializingSingleton

@Utility
class StatisticScrapperService(
    private val usersService: UsersService,
    private val childService: ChildService
) : SmartInitializingSingleton {

    override fun afterSingletonsInstantiated() {
        val allUsers = usersService.getAll()
        val allChildren = childService.getAll()

        setTutorsInfo(allUsers.filter { it.role == UsersRoles.TUTOR })
        setExpertsInfo(allUsers.filter { it.role == UsersRoles.EXPERT })
        setMentorInfo(allUsers.filter { it.role == UsersRoles.TUTOR })
        setUsersInfo(allUsers.filter { it.role == UsersRoles.TUTOR })
        setChildrenCount(allChildren)
    }

    fun setTutorsInfo(users: List<Users>) =
        StatisticsStorage.setTutorsInfo(Pair(users.size, users.filter { it.verified }.size))

    fun setExpertsInfo(users: List<Users>) =
        StatisticsStorage.setExpertsInfo(Pair(users.size, users.filter { it.verified }.size))

    fun setMentorInfo(users: List<Users>) =
        StatisticsStorage.setMentorsInfo(Pair(users.size, users.filter { it.verified }.size))

    fun setUsersInfo(users: List<Users>) =
        StatisticsStorage.setUsersInfo(Pair(users.size, users.filter { it.verified }.size))

    fun setChildrenCount(children: List<Children>) =
        StatisticsStorage.setChildrenCount(children.size)
}