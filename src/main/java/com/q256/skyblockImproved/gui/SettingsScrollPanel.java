package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.config.Setting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsScrollPanel extends ScrollablePanel{
    ArrayList<Setting<?>> settings;
    protected GuiButton interactedButton;

    public SettingsScrollPanel(FontRenderer fontRendererObj, int x, int y, int width, int height) {
        super(fontRendererObj, x, y, width, height, 10000);
        SkyblockImproved main = SkyblockImproved.getInstance();

        LinkedHashMap<String, ArrayList<Setting<?>>> settingHashMap = new LinkedHashMap<>();
        settings = new ArrayList<>();

        for(String settingName:main.getConfigValues().getSettingNames()){
            Setting<?> setting = main.getConfigValues().getSetting(settingName);
            String settingCategory = setting.getCategory();
            if(!settingHashMap.containsKey(settingCategory)){
                settingHashMap.put(settingCategory, new ArrayList<>());
            }
            settingHashMap.get(settingCategory).add(setting);

            settings.add(setting);
        }

        int currentHeight = 0;
        int settingNumber = 0;
        for(Map.Entry<String, ArrayList<Setting<?>>> entry:settingHashMap.entrySet()){
            currentHeight += 4;
            addLabel(2, currentHeight, entry.getKey(), "");
            currentHeight += 5 + fontRendererObj.FONT_HEIGHT;

            for(Setting<?> setting: entry.getValue()){
                addLabel(smallWidth/15, currentHeight, setting.getDisplayName(), setting.getDescription());

                if(setting.getType() == Boolean.class){
                   addButton(settingNumber, (int)(smallWidth*0.7), currentHeight-1, (int)(smallWidth*0.25),
                           fontRendererObj.FONT_HEIGHT+3, ((Setting<Boolean>)setting).getValue() ? "§aTrue" : "§cFalse");
                }
                if(setting.getType() == Double.class){
                    addSlider(settingNumber, (int)(smallWidth*0.7), currentHeight-1, (int)(smallWidth*0.25), fontRendererObj.FONT_HEIGHT+3,
                            "","%", 0, 100, (Double)(setting.getValue())*100, false);
                }

                currentHeight += 3 + fontRendererObj.FONT_HEIGHT;
                settingNumber++;
            }
        }
        totalHeight = currentHeight + 30;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        interactedButton = button;
        int id = button.id;
        if(id>=0 && id<settings.size()){
            Setting<?> setting = settings.get(id);
            if(button instanceof MovableGuiButton){
                MovableGuiButton movableButton = (MovableGuiButton) button;
                if(setting.getType() == Boolean.class){
                    Setting<Boolean> booleanSetting = (Setting<Boolean>) setting;
                    booleanSetting.setValue(!booleanSetting.getValue());
                    button.displayString = booleanSetting.getValue() ? "§aTrue" : "§cFalse";
                }
            }
        }

        super.actionPerformed(button);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if(interactedButton != null){
            int id = interactedButton.id;
            if(id>=0 && id<settings.size()){
                Setting<?> setting = settings.get(id);
                if(interactedButton instanceof MovableGuiSlider){
                    MovableGuiSlider movableSlider = (MovableGuiSlider) interactedButton;
                    if(setting.getType() == Double.class){
                        Setting<Double> doubleSetting = (Setting<Double>) setting;
                        doubleSetting.setValue(movableSlider.getValue()/100);
                    }
                }
            }
        }
        interactedButton = null;
        super.mouseReleased(mouseX, mouseY, state);
    }
}
