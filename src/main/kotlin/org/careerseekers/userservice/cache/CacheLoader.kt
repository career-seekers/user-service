package org.careerseekers.userservice.cache

interface CacheLoader {
    fun preloadCache(): Any
}