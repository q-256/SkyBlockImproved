package com.q256.skyblockImproved.utils;

import com.q256.skyblockImproved.constants.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    private static final String MESSAGE_PREFIX = EnumChatFormatting.WHITE+ "[" + EnumChatFormatting.AQUA + "SBI" + EnumChatFormatting.WHITE + "] ";
    private static final String[] RAINBOW_COLORS = {"§c","§6","§e","§a","§b","§9","§d"};
    private static final String[] NUMBER_MAGNITUDE_SUFFIXES = {"k","mil","bil","tril"};

    public static void sendClientMessage(String text){
        sendClientMessage(text, false);
    }

    public static synchronized void sendClientMessage(String text, boolean prefix) {
        sendClientMessage(new ChatComponentText(prefix ? MESSAGE_PREFIX + text : text));
    }

    public static synchronized void sendClientMessage(IChatComponent chatComponent){
        EntityPlayerSP thePlayer;
        try {
            thePlayer = Minecraft.getMinecraft().thePlayer;
        } catch (NullPointerException exception){
            System.out.println("[CHAT] "+chatComponent.getFormattedText());
            return;
        }
        if(thePlayer==null){
            System.out.println("[CHAT] "+chatComponent.getFormattedText());
            return;
        }

        ClientChatReceivedEvent event = new ClientChatReceivedEvent((byte) 1, chatComponent);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            thePlayer.addChatMessage(chatComponent);
        }
    }

    public static synchronized void sendClientMessage(IChatComponent chatComponent, boolean prefix){
        if(prefix) {
            ChatComponentText newCct = new ChatComponentText(MESSAGE_PREFIX);
            newCct.appendSibling(chatComponent);
            sendClientMessage(newCct);
        } else {
            sendClientMessage(chatComponent);
        }
    }

    public static synchronized void sendClientMessages(List<IChatComponent> texts){
        for(IChatComponent chatComp:texts){
            sendClientMessage(chatComp);
        }
    }

    public static ChatComponentText mergeChatTexts(String text1, ChatComponentText text2){
        ChatComponentText cct = new ChatComponentText(text1 + text2.getChatComponentText_TextValue());
        cct.setChatStyle(text2.getChatStyle());
        for(IChatComponent sibling:text2.getSiblings()){
            cct.appendSibling(sibling);
        }
        return cct;
    }

    /**
     * Converts an amount of time, measured in milliseconds, into an easier to read String.
     * This includes turning it into a larger unit (e.g minutes, hours, weeks...), rounding it down, and appending the unit to the end of the String.
     * Examples:
     * <blockquote><pre>
     * millisToString(45986) returns "45 seconds"
     * millisToString(10800123) returns "3 hours"
     * millisToString(90000000) returns "1 day"
     * </pre></blockquote>
     * @return A human-readable String representing the specified time
     */
    public static String millisToString(long millis){
        long value = millis;
        String unit = "millisecond";

        if(millis> 1000L){
            value = millis/1000;
            unit = "second";
        }
        if(millis> 1000L *60){
            value = millis/1000/60;
            unit = "minute";
        }
        if(millis> 1000L *60*60){
            value = millis/1000/60/60;
            unit = "hour";
        }
        if(millis> 1000L *60*60*24){
            value = millis/1000/60/60/24;
            unit = "day";
        }
        if(millis> 1000L *60*60*24*7){
            value = millis/1000/60/60/24/7;
            unit = "week";
        }
        if(millis> 1000L *60*60*24*30){
            value = millis/1000/60/60/24/30;
            unit = "month";
        }
        if(millis> 1000L *60*60*24*365){
            value = millis/1000/60/60/24/365;
            unit = "year";
        }

        if(value==1) return value + " " + unit;
        return value + " " + unit + "s";
    }

    /** Formats the given number with commas: #,### */
    public static String formatNumberLong(long l){
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(l);
    }
    /** Formats the given number with commas and 2 decimal places: #,###.## */
    public static String formatNumberLong(double d){
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        return formatter.format(d);
    }

    /**
     * Formats the given number with suffixes, keeps a total of 3 digits and a decimal point.
     * Examples:
     * <blockquote><pre>
     * 3458 -> 3.46k
     * 98765432 -> 988mil
     * 12635630000 -> 12.6bil
     * 1000000000000 -> 1tril
     * </pre></blockquote>
     */
    public static String formatNumberShort(long number){
        StringBuilder stringBuilder = new StringBuilder();
        if(number<0){
            stringBuilder.append("-");
            number *= -1;
        }

        DecimalFormat decimalFormat;
        if(number<1000) return String.valueOf(number);
        for(String suffix:NUMBER_MAGNITUDE_SUFFIXES){
            if(number<1000000){
                if(number<10000) decimalFormat = new DecimalFormat("#.##");
                else if(number<100000) decimalFormat = new DecimalFormat("##.#");
                else decimalFormat = new DecimalFormat("###");
                return stringBuilder.append(decimalFormat.format((double) number / 1000)).append(suffix).toString();
            }
            number /= 1000;
        }

        decimalFormat = new DecimalFormat("###");
        stringBuilder.append(decimalFormat.format(number)).append("tril");
        return stringBuilder.toString();
    }
    /**
     * Formats the given number with suffixes, keeps a total of 3 digits and a decimal point.
     * Examples:
     * <blockquote><pre>
     * 3458.25 -> 3.46k
     * 98765432.1 -> 988mil
     * 12635630000 -> 12.6bil
     * 1000000000000 -> 1tril
     * </pre></blockquote>
     */
    public static String formatNumberShort(double number){
        return formatNumberShort((long)number);
    }

    /**
     * Returns {@code number} of whiteSpaces
     */
    public static String getWhiteSpaces(int number){
        StringBuilder ss = new StringBuilder();
        for(int i=0; i<number; i++){
            ss.append(" ");
        }
        return ss.toString();
    }

    /**
     * Calls colorNumber method with the default color scheme of dark red, light red, orange, yellow, light green, dark green, bold cyan
     */
    public static String colorNumber(double number, double[] thresholds){
        return colorNumber(number, thresholds, new String[]{"§4","§c","§6","§e","§a","§2","§b§l"});
    }

    /**
     * Returns a color code based on the value of the number.
     * Example: <p>
     * {@code colorNumber(number, new double[]{50,75,100}), new char[]{'§c','§6','§e','§a'}} will return:
     * <blockquote><pre>
     * §c if number < 50
     * §6 if 50 <= number < 75
     * §e if 75 <= number < 100
     * §a if 100 <= number
     * </pre></blockquote>
     * @param number number to be colored
     * @param thresholds determines when the colors switch, must be in ascending order
     * @param colors color codes, must be one longer than thresholds
     * @return One of the inputted color codes
     */
    public static String colorNumber(double number, double[] thresholds, String[] colors){
        for(int i=0; i<thresholds.length; i++){
            if(number < thresholds[i]) return colors[i];
        }
        return colors[thresholds.length];
    }

    /**
     * Turns §w into a rainbow color code
     */
    public static String addRainbowFormat(String string){
        StringBuilder stringBuilder = new StringBuilder();
        int currentIndex = 0;
        while (currentIndex<string.length()){
            //find the first §w rainbow color code
            int firstRainbowColorCode = string.indexOf("§w", currentIndex);
            if(firstRainbowColorCode==-1){
                stringBuilder.append(string, currentIndex, string.length());
                break;
            }

            String currentExtraFormat = "";

            int currentColor = 0;

            currentIndex = firstRainbowColorCode;
            stringBuilder.append(string, 0, currentIndex);
            currentIndex+=2;

            while(currentIndex<string.length()){
                char nextChar = string.charAt(currentIndex);
                if(nextChar==' '){
                    stringBuilder.append(" ");
                }
                else if(nextChar!='§'){
                    stringBuilder.append(RAINBOW_COLORS[currentColor]);
                    stringBuilder.append(currentExtraFormat);
                    stringBuilder.append(nextChar);
                    currentColor = (currentColor+1)% RAINBOW_COLORS.length;
                } else {
                    currentIndex++;
                    nextChar = string.charAt(currentIndex);
                    if(nextChar=='l' || nextChar=='m' || nextChar=='n' || nextChar=='o' || nextChar=='k'){
                        currentExtraFormat = "§" + nextChar;
                    } else {
                        stringBuilder.append(string.charAt(currentIndex-1));
                        stringBuilder.append(nextChar);
                        currentIndex++;
                        break;
                    }
                }
                currentIndex++;
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Will return a list of Strings, each corresponding to a different line of the next.
     * A single word will not be split up.
     * Each line will be at most {@code maxPixelsPerLine} pixels wide (based on the fontRenderer).
     * The only exception to this is if a single word is wider than {@code maxPixelsPerLine}, in which case that line will be one word long.
     * Keeps color codes across lines
     */
    public static ArrayList<String> splitIntoLines(String string, FontRenderer fontRenderer, int maxPixelsPerLine){

        //todo Differentiate between color and formatting codes.

        ArrayList<String> result = new ArrayList<>();

        String remainingString = string;
        String lastColorCode = "";

        while(remainingString.length()>0){
            int firstSpace = remainingString.indexOf(' ');
            if(firstSpace==-1) firstSpace = remainingString.length();

            StringBuilder stringThisLine = new StringBuilder(lastColorCode);

            String nextWord = remainingString.substring(0, firstSpace);

            if(fontRenderer.getStringWidth(nextWord.replaceAll("§.",""))>maxPixelsPerLine){
                stringThisLine.append(nextWord);
                result.add(stringThisLine.toString());
                remainingString = remainingString.substring(firstSpace+1);
                String tempColorCode = getLastColorCode(nextWord);
                if(tempColorCode!=null) lastColorCode = tempColorCode;
            }
            else {
                int pixelsThisLine = fontRenderer.getStringWidth(nextWord.replaceAll("§.",""));
                stringThisLine.append(nextWord);
                if(firstSpace<remainingString.length()) remainingString = remainingString.substring(firstSpace+1);
                else remainingString = "";
                String tempColorCode = getLastColorCode(nextWord);
                if(tempColorCode!=null) lastColorCode = tempColorCode;

                while(true){
                    firstSpace = remainingString.indexOf(' ');
                    if(firstSpace==-1) firstSpace=remainingString.length();

                    nextWord = remainingString.substring(0, firstSpace);
                    pixelsThisLine += fontRenderer.getCharWidth(' ');
                    pixelsThisLine += fontRenderer.getStringWidth(nextWord.replaceAll("§.",""));
                    if(pixelsThisLine>maxPixelsPerLine){
                        result.add(stringThisLine.toString());
                        break;
                    }

                    tempColorCode = getLastColorCode(nextWord);
                    if(tempColorCode!=null) lastColorCode = tempColorCode;
                    stringThisLine.append(' ').append(nextWord);
                    if(firstSpace<remainingString.length()) remainingString = remainingString.substring(firstSpace+1);
                    else{
                        remainingString = "";
                        result.add(stringThisLine.toString());
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static String getLastColorCode(String inString){
        if(inString.length()<2) return null;

        int lastIndex = inString.lastIndexOf('§');

        if(lastIndex==inString.length()-1){
            lastIndex = inString.substring(0, inString.length()-1).lastIndexOf('§');
        }

        if(lastIndex==-1) return null;
        else return inString.substring(lastIndex, lastIndex+2);
    }

    /**
     * Adds §7 before the first decimal point in the input string
     */
    public static String grayOutAfterDecimal(String stringIn){
        int decimalIndex = stringIn.indexOf('.');
        if(decimalIndex==-1) return stringIn;
        return stringIn.substring(0, decimalIndex) + "§7" + stringIn.substring(decimalIndex);
    }

    /**
     * Returns the name of the given color code
     */
    public static String getColorCodeName(char colorCode){
        if(colorCode>='0' && colorCode<='9') return Constants.COLOR_NAMES[colorCode-'0'];
        if(colorCode>='a' && colorCode<='f') return Constants.COLOR_NAMES[colorCode-'a'+10];
        return "Unknown Color";
    }
}
