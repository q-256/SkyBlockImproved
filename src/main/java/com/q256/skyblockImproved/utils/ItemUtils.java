package com.q256.skyblockImproved.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ItemUtils {
    public static ItemStack getFillerGlassPane (){
        ItemStack itemStack = new ItemStack(Item.getItemById(160));
        itemStack.setItemDamage(15);
        itemStack.setStackDisplayName(" ");
        return itemStack;
    }

    public static ItemStack getCloseBarrier(){
        ItemStack itemStack = new ItemStack(Item.getItemById(166));
        itemStack.setStackDisplayName("§cClose");
        return itemStack;
    }

    public static ArrayList<NBTTagCompound> parseNbt(byte[] gzippedBytes) throws IOException {
        NBTTagCompound compoundTag = CompressedStreamTools.readCompressed(new ByteArrayInputStream(gzippedBytes));

        ArrayList<NBTTagCompound> outAl = new ArrayList<>();
        NBTTagList listTag = compoundTag.getTagList("i", 10);
        for(int i=0; i<listTag.tagCount(); i++){
            outAl.add(listTag.getCompoundTagAt(i));
        }
        return outAl;
    }
}
