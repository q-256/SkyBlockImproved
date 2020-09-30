package com.q256.skyblockImproved.api;

import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Used for temporarily storing and accessing data from the Hypixel API
 */
public class ApiCache {
    /**
     * Maximum number of items that can be held in the cache before the garbage collector starts removing objects earlier than their {@code removalTime}
     */
    int cacheLimit = 50;

    /**
     * This is where all the cached objects are stored
     */
    private ArrayList<CacheableJson> cachedObjects = new ArrayList<CacheableJson>();

    /**
     * Makes sure that 2 different threads don't modify the cache at the same time
     */
    private ReadWriteLock cachedObjectsLock = new ReentrantReadWriteLock();

    /**
     * Creates a new ApiCache and starts the garbage collector with a {@code period} of 1 second
     */
    public ApiCache(){
        startGarbageCollector(1000);
    }

    /**
     * Starts a loop that removes unused items from {@code cachedObjects}.
     * An object is removed if: its {@code removalTime} has been passed, or the cache is full and this object has the the earliest {@code removalTime}
     * @param period Determines how often {@code cachedObjects} is checked for unused objects. Measured in milliseconds.
     */
    private void startGarbageCollector(long period){
        final Timer cacheGC_timer = new Timer();
        TimerTask cleanCache = new TimerTask() {
            @Override
            public void run() {

                long currentTime = System.currentTimeMillis();
                cachedObjectsLock.writeLock().lock();

                //remove any objects whose timer has expired
                for(int i = 0; i< cachedObjects.size(); i++){
                    if(cachedObjects.get(i).getRemovalTime() <= currentTime){
                        cachedObjects.remove(i);
                        i--;
                    }
                }

                while(cachedObjects.size()>cacheLimit){
                    //remove the object with the least time remaining
                    long firstRemovalTime = Long.MAX_VALUE;
                    CacheableJson firstObjToBeRemoved = null;
                    for(CacheableJson cachedObj: cachedObjects){
                        if(cachedObj.getRemovalTime() < firstRemovalTime){
                            firstRemovalTime = cachedObj.getRemovalTime();
                            firstObjToBeRemoved = cachedObj;
                        }
                    }
                    cachedObjects.remove(firstObjToBeRemoved);
                }

                cachedObjectsLock.writeLock().unlock();

            }
        };
        cacheGC_timer.schedule(cleanCache, period, period);
    }

    /**
     * Adds the specified item to cachedObjects.
     * If an item with the given {@code name} and {@code type} already exists, its {@code removalTime} will be updated.
     * Note: this does not override the previous value of the item.
     * @return True if a new item was successfully added, false if the the item already existed
     */
    boolean addItem(String name, int type, JsonObject value){
        cachedObjectsLock.readLock().lock();

        for(CacheableJson cachedObj:cachedObjects){
            if(cachedObj.name.equalsIgnoreCase(name) && cachedObj.type == type){
                cachedObj.updateRemovalTime();
                cachedObjectsLock.readLock().unlock();
                return false;
            }
        }

        cachedObjectsLock.readLock().unlock();

        cachedObjectsLock.writeLock().lock();

        cachedObjects.add(new CacheableJson(name, type, value));

        cachedObjectsLock.writeLock().unlock();
        return true;
    }

    /**
     * Finds the requested item and returns its {@code value}
     * @return Returns the item's {@code value} if the item was found, returns null otherwise
     */
    JsonObject getItem(String name, int type){
        cachedObjectsLock.readLock().lock();

        JsonObject outJson = null;

        for(CacheableJson cachedObj:cachedObjects){
            if(cachedObj.name.equalsIgnoreCase(name) && cachedObj.type == type){
                outJson = cachedObj.value;
                cachedObj.updateRemovalTime();
            }
        }

        cachedObjectsLock.readLock().unlock();
        return outJson;
    }
}

/**
 * Used for caching UUIDs from the Mojang API
 */
class UuidCache {
    private static final int cacheLimit = 10000;
    private HashMap<String, String> cache = new HashMap<>();

    public synchronized String getUuid(String name){
        if(cache.containsKey(name)) return cache.get(name);
        return null;
    }

    public synchronized void addItem(String name, String uuid){
        if(cache.size()>cacheLimit){
            int i = 0;
            for(String ss:cache.keySet()){
                cache.remove(ss);
                i++;
                if(i>cacheLimit/10)break;
            }
        }
        cache.put(name, uuid);
    }
}
