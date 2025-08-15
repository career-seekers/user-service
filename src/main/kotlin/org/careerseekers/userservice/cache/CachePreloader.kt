package org.careerseekers.userservice.cache

interface CachePreloader {
    fun preloadCache(): Any
}