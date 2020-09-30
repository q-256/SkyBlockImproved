package com.q256.skyblockImproved.config;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.utils.TextUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigHandler {
    transient SkyblockImproved main;
    private File configFile;
    public ConfigValues configValues;

    public ConfigHandler(SkyblockImproved main, File configFile){
        this.main = main;
        this.configFile = configFile;
        if(!loadFile()) configValues = new ConfigValues();
    }

    /**
     * Loads the config file and saves it to the {@code configValues} field of this object
     * @return whether the load was successful
     */
    public boolean loadFile(){
        if(!configFile.exists()) return false;
        try {
            FileReader fileReader = new FileReader(configFile);
            configValues = new Gson().fromJson(fileReader, ConfigValues.class);
            fileReader.close();
            configValues.postDeserialization();
            return true;
        } catch (IOException | JsonParseException exception) {
            exception.printStackTrace();
            TextUtils.sendClientMessage("§cCould not load your config file, reason: §f"+exception.getMessage()+
                    "\nPlease use §e/sbi reloadConfig §fto try again", true);
            return false;
        }
    }

    /**
     * Saves {@link ConfigHandler#configValues} to {@link ConfigHandler#configFile}
     * @return whether the save was successful
     */
    public boolean saveFile(boolean printErrorToChat){
        try{
            configValues.preSerialization();
            FileWriter fileWriter = new FileWriter(configFile);
            new Gson().toJson(configValues, fileWriter);
            fileWriter.close();
            return true;
        } catch (IOException | JsonParseException exception){
            exception.printStackTrace();
            if(printErrorToChat) TextUtils.sendClientMessage("§cCould not save your config file, reason: §f" + exception.getMessage(), true);
            return false;
        }
    }
}
