package com.q256.skyblockImproved.listeners;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WithermancerListener {
    private static final String WITHERMANCER_SKULL_TEXTURE_ID = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW" +
            "5lY3JhZnQubmV0L3RleHR1cmUvOTY0ZTFjM2UzMTVjOGQ4ZmZmYzM3OTg1YjY2ODFjNWJkMTZhNmY5N2ZmZDA3MTk5ZThhMDVlZmJlZjEwMzc5MyJ9fX0=";
    private static final Pattern cryptSkullHitPattern = Pattern.compile("A Crypt Wither Skull exploded, hitting you for (\\d+\\.\\d) damage.");
    private final SkyblockImproved main = SkyblockImproved.getInstance();

    int skullCount = 0;
    int totalDmg = 0;

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event){
        if(event.type == 2 || !main.getConfigValues().compactWithermancerMessages.getValue()) return;
        String unformattedMsg = event.message.getUnformattedText();
        Matcher matcher = cryptSkullHitPattern.matcher(unformattedMsg);

        if(matcher.matches()){
            try {
                totalDmg += (int)Double.parseDouble(matcher.group(1));
                skullCount++;
                event.setCanceled(true);
            } catch (NullPointerException exception){
                exception.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void tickStart(TickEvent.ClientTickEvent event){
        if(!main.getConfigValues().compactWithermancerMessages.getValue()) return;

        if(event.phase == TickEvent.Phase.END){
            skullCount = 0;
            totalDmg = 0;
        } else if(skullCount != 0) {
            String dmgString = TextUtils.formatNumberLong(totalDmg);
            if(skullCount == 1) TextUtils.sendClientMessage("§7A Crypt Wither Skull exploded, hitting you for §c" + dmgString + "§7 damage.");
            else TextUtils.sendClientMessage("§c" + skullCount + "§7 Crypt Wither Skulls exploded, hitting you for §c" + dmgString + "§7 damage.");
        }
    }

    //todo improve performance by using a HashMap
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if(!main.getConfigValues().compactWithermancerMessages.getValue()) return;

        Minecraft mc = Minecraft.getMinecraft();
        double partialTicks = event.partialTicks;

        Entity viewer = mc.getRenderViewEntity();
        double viewerX = viewer.prevPosX + (viewer.posX - viewer.prevPosX) * partialTicks;
        double viewerY = viewer.prevPosY + (viewer.posY - viewer.prevPosY) * partialTicks;
        double viewerZ = viewer.prevPosZ + (viewer.posZ - viewer.prevPosZ) * partialTicks;

        Vec3 viewerVec = viewer.getPositionVector();

        ArrayList<Entity> cryptSkulls = new ArrayList<>();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity != viewer && isWithermancerSkull(entity) && viewerVec.distanceTo(entity.getPositionVector()) < 32) cryptSkulls.add(entity);
        }

        for (int i = cryptSkulls.size()-1; i>=0; i--) {
            Entity entity = cryptSkulls.get(i);

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

            int counter = 1;
            for (int j = i-1; j >= 0; j--) {
                Entity entity2 = cryptSkulls.get(j);
                if(Math.abs(entity.posX - entity2.posX) < 1 && Math.abs(entity.posY - entity2.posY) < 1 && Math.abs(entity.posZ - entity2.posZ) < 1){
                    counter++;
                    cryptSkulls.remove(j);
                    i--;
                }
            }

            x -= viewerX;
            y -= viewerY;
            z -= viewerZ;

            y += 1.2;

            double rotation = entity.rotationYaw + 55;

            double sin = Math.sin(rotation*Math.PI/180);
            double cos = Math.cos(rotation*Math.PI/180);

            x -= sin*0.45;
            z += cos*0.45;

            double distanceScale = Math.max(1, viewer.getPositionVector().distanceTo(entity.getPositionVector()) / 5F);

            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.enableAlpha();
            GlStateManager.disableDepth();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.translate(x, y, z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-2F/75, -2F/75, 2F/75);
            GlStateManager.scale(distanceScale, distanceScale, distanceScale);

            String displayString = "§e" + counter;
            mc.fontRendererObj.drawString(displayString, -mc.fontRendererObj.getStringWidth(displayString) / 2F, -mc.fontRendererObj.FONT_HEIGHT/2F, -1, true);

            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    public static boolean isWithermancerSkull(Entity entity){
        if(entity instanceof EntityArmorStand){
            EntityArmorStand entityArmorStand = (EntityArmorStand) entity;
            if(!entityArmorStand.isInvisible() || !entityArmorStand.hasNoGravity() || !entityArmorStand.hasMarker() || !entityArmorStand.isSilent()) return false;

            try {
                String texture = entityArmorStand.getHeldItem().getTagCompound().getCompoundTag("SkullOwner")
                        .getCompoundTag("Properties").getTagList("textures", 10).getCompoundTagAt(0).getString("Value");
                return WITHERMANCER_SKULL_TEXTURE_ID.equals(texture);
            } catch (Exception ignored){}
        }
        return false;
    }
}
