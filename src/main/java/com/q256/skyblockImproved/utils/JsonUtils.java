package com.q256.skyblockImproved.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {
    public static JsonElement get(JsonObject jsonObject, String path){
        String[] splitString = path.split("\\.");

        JsonObject currentObj = jsonObject;
        try {
            for (int i = 0; i < splitString.length; i++) {
                if (!currentObj.has(splitString[i])) return null;
                if (i == splitString.length - 1) {
                    return currentObj.get(splitString[i]);
                }
                currentObj = currentObj.getAsJsonObject(splitString[i]);
            }
        } catch (ClassCastException exception){
            exception.printStackTrace();
        }
        return null;
    }

    public static JsonObject getJsonObject(JsonObject jsonObject, String path){
        JsonElement jsonElement = get(jsonObject, path);
        return jsonElement instanceof JsonObject ? (JsonObject) jsonElement : null;
    }

    public static JsonArray getJsonArray(JsonObject jsonObject, String path){
        JsonElement jsonElement = get(jsonObject, path);
        return jsonElement instanceof JsonArray ? (JsonArray)jsonElement : null;
    }
}
