package org.careerseekers.userservice.cache

interface CacheLoader<T> : CacheClient<T> {
    fun loadItemToCache(item: T): Any
}