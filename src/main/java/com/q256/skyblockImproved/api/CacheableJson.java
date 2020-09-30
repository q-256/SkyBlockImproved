package com.q256.skyblockImproved.api;

import com.google.gson.JsonObject;

class CacheableJson {
    /**
     * Determines how long this object will be cached for measured in milliseconds.
     * */
    static long Time_TO_LIVE = 40000;

    String name;
    int type;
    private long removalTime;
    JsonObject value;

    public static int TYPE_PLAYER = 0;
    public static int TYPE_SKYBLOCK_PROFILE = 1;

    public CacheableJson(String name, int type, JsonObject value){
        this.name = name;
        this.type = type;
        this.value = value;
        updateRemovalTime();
    }

    long getRemovalTime(){return removalTime;}
    void updateRemovalTime(){removalTime = System.currentTimeMillis() + Time_TO_LIVE;}
}