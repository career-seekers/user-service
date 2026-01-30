package org.careerseekers.userservice.cache

fun interface CachePreloader {
    fun preloadCache(): Any
}