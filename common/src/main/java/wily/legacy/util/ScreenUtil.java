package wily.legacy.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import wily.legacy.LegacyMinecraft;
import wily.legacy.LegacyMinecraftClient;
import wily.legacy.client.BufferSourceWrapper;
import wily.legacy.client.LegacyOptions;
import wily.legacy.client.LegacyTip;
import wily.legacy.client.LegacyTipOverride;
import wily.legacy.client.screen.LegacyIconHolder;

import java.util.function.Consumer;

public class ScreenUtil {
    private static final Minecraft mc = Minecraft.getInstance();
    protected static LogoRenderer logoRenderer = new LogoRenderer(false);
    public static LegacyIconHolder iconHolderRenderer = new LegacyIconHolder();
    public static final ResourceLocation LOADING_BLOCK_SPRITE = new ResourceLocation(LegacyMinecraft.MOD_ID,"widget/loading_block");
    public static final ResourceLocation POINTER_PANEL_SPRITE = new ResourceLocation(LegacyMinecraft.MOD_ID,"tiles/pointer_panel");
    public static final ResourceLocation PANEL_SPRITE = new ResourceLocation(LegacyMinecraft.MOD_ID,"tiles/panel");
    public static final ResourceLocation PANEL_RECESS_SPRITE = new ResourceLocation(LegacyMinecraft.MOD_ID,"tiles/panel_recess");
    public static final ResourceLocation ENTITY_PANEL_SPRITE = new ResourceLocation(LegacyMinecraft.MOD_ID,"tiles/entity_panel");
    public static final ResourceLocation SQUARE_RECESSED_PANEL = new ResourceLocation(LegacyMinecraft.MOD_ID,"tiles/square_recessed_panel");
    public static final ResourceLocation SQUARE_ENTITY_PANEL = new ResourceLocation(LegacyMinecraft.MOD_ID,"tiles/square_entity_panel");
    public static void renderPointerPanel(GuiGraphics graphics, int x, int y, int width, int height){
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        renderTiles(POINTER_PANEL_SPRITE,graphics,x,y,width,height,2);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
    public static void renderPanel(GuiGraphics graphics, int x, int y, int width, int height, float dp){
      renderTiles(PANEL_SPRITE,graphics,x,y,width,height,dp);
    }
    public static void renderPanelRecess(GuiGraphics graphics, int x, int y, int width, int height, float dp){
        renderTiles(PANEL_RECESS_SPRITE,graphics,x,y,width,height,dp);
    }
    public static void renderEntityPanel(GuiGraphics graphics, int x, int y, int width, int height, float dp){
        renderTiles(ENTITY_PANEL_SPRITE,graphics,x,y,width,height,dp);
    }
    public static void renderSquareEntityPanel(GuiGraphics graphics, int x, int y, int width, int height, float dp){
        renderTiles(SQUARE_ENTITY_PANEL,graphics,x,y,width,height,dp);
    }
    public static void renderSquareRecessedPanel(GuiGraphics graphics, int x, int y, int width, int height, float dp){
        renderTiles(SQUARE_RECESSED_PANEL,graphics,x,y,width,height,dp);
    }
    public static void renderTiles(ResourceLocation location,GuiGraphics graphics, int x, int y, int width, int height, float dp){
        mc.getTextureManager().getTexture(new ResourceLocation("textures/atlas/gui.png")).bind();
        GlStateManager._texParameter(3553, 10241, 9729);
        //GlStateManager._texParameter(3553, 10240, 9729);
        graphics.pose().pushPose();
        graphics.pose().translate(x,y,0);
        if (dp != 1.0)
            graphics.pose().scale(1/dp,1/dp,1/dp);
        graphics.blitSprite(location,0,0, (int) (width * dp), (int) (height * dp));
        graphics.pose().popPose();
        GlStateManager._texParameter(3553, 10241, 9728);
    }
    public static void drawAutoSavingIcon(GuiGraphics graphics,int x, int y) {
        graphics.pose().pushPose();
        graphics.pose().scale(0.5F,0.5F,1);
        graphics.blitSprite(new ResourceLocation(LegacyMinecraft.MOD_ID,"hud/save_chest"),x * 2,y * 2,48,48);
        graphics.pose().popPose();
        graphics.pose().pushPose();
        double heightAnim = (Util.getMillis() / 80D) % 11;
        graphics.pose().translate(x + 5.5,y - 8 - (heightAnim > 5 ? 10 - heightAnim : heightAnim),0);
        graphics.blitSprite(new ResourceLocation(LegacyMinecraft.MOD_ID,"hud/save_arrow"),0,0,13,16);
        graphics.pose().popPose();
    }
    public static void renderDefaultBackground(GuiGraphics guiGraphics){
        renderDefaultBackground(guiGraphics,false,true);
    }
    public static void renderDefaultBackground(GuiGraphics guiGraphics, boolean title){
        renderDefaultBackground(guiGraphics,false,title);
    }
    public static boolean getActualLevelNight(){
        return (mc.getSingleplayerServer() != null&& mc.getSingleplayerServer().overworld() != null && mc.getSingleplayerServer().overworld().isNight()) || (mc.level!= null && mc.level.isNight());
    }
    public static void renderDefaultBackground(GuiGraphics guiGraphics, boolean loading, boolean title){
        if (mc.level == null || loading)
            renderPanoramaBackground(guiGraphics, loading && getActualLevelNight());
        else mc.screen.renderTransparentBackground(guiGraphics);
        if (title)
            logoRenderer.renderLogo(guiGraphics,mc.screen == null ?  0: mc.screen.width,1.0F);
    }
    public static void renderPanoramaBackground(GuiGraphics guiGraphics, boolean isNight){
        RenderSystem.depthMask(false);
        ResourceLocation panorama = new ResourceLocation(LegacyMinecraft.MOD_ID, "textures/gui/title/panorama_" + (isNight ? "night" : "day") + ".png");
        Minecraft.getInstance().getTextureManager().getTexture(panorama).setFilter(true, false);
        guiGraphics.blit(panorama, 0, 0, mc.options.panoramaSpeed().get().floatValue() * Util.getMillis() / 66.32f, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), guiGraphics.guiHeight() * 820/144, guiGraphics.guiHeight());
        RenderSystem.depthMask(true);
    }
    public static void drawOutlinedString(GuiGraphics graphics, Font font, Component component, int x, int y, int color, int outlineColor, float outline) {
        drawStringOutline(graphics,font,component,x,y,outlineColor,outline);
        graphics.drawString(font,component, x, y, color,false);

    }
    public static void drawStringOutline(GuiGraphics graphics, Font font, Component component, int x, int y, int outlineColor, float outline) {
        outline/=2;
        float[] translations = new float[]{0,outline,-outline};
        for (float t : translations) {
            for (float t1 : translations) {
                if (t != 0 || t1 != 0) {
                    graphics.pose().pushPose();
                    graphics.pose().translate(t,t1,0F);
                    graphics.drawString(font, component, x, y, outlineColor, false);
                    graphics.pose().popPose();
                }
            }
        }
    }
    public static boolean isMouseOver(double mouseX, double mouseY, double x, double y, int width, int height){
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }
    public static void applyHUDScale(GuiGraphics graphics, Consumer<Integer> applyWidth, Consumer<Integer> applyHeight){
        graphics.pose().scale(3f / getHUDScale(), 3f / getHUDScale() ,3f / getHUDScale());
        applyHeight.accept((int) (mc.getWindow().getGuiScaledHeight() * getHUDScale()/3));
        applyWidth.accept((int) (mc.getWindow().getGuiScaledWidth() * getHUDScale()/3));
    }
    public static void resetHUDScale(GuiGraphics graphics, Consumer<Integer> applyWidth, Consumer<Integer> applyHeight){
        graphics.pose().scale(getHUDScale()/3f,getHUDScale()/3f,getHUDScale()/3f);
        applyHeight.accept(mc.getWindow().getGuiScaledHeight());
        applyWidth.accept(mc.getWindow().getGuiScaledWidth());
    }
    public static boolean hasClassicCrafting(){
        return getLegacyOptions().classicCrafting().get();
    }
    public static float getHUDScale(){
        return Math.max(1.5f,4 - getLegacyOptions().hudScale().get());
    }
    public static float getHUDSize(){
        return 3f / ScreenUtil.getHUDScale()* (mc.gameMode.canHurtPlayer() ? 68 : 41);
    }
    public static double getHUDDistance(){
        return -getLegacyOptions().hudDistance().value*(22.5D + (getLegacyOptions().inGameTooltips().get() ? 17.5D : 0));
    }
    public static float getHUDOpacity(){
        return Math.max(Math.min(255f,mc.gui.toolHighlightTimer * 38.4f)/ 255f, getInterfaceOpacity());
    }
    public static boolean hasTooltipBoxes(){
        return getLegacyOptions().tooltipBoxes().get();
    }
    public static float getInterfaceOpacity(){
        return getLegacyOptions().hudOpacity().get().floatValue();
    }
    public static int getDefaultTextColor(boolean forceWhite){
        return (getLegacyOptions().forceYellowText().get() || hasProgrammerArt()) && !forceWhite ? 0xFFFF00 : 0xFFFFFF;
    }
    public static int getDefaultTextColor(){
        return getDefaultTextColor(false);
    }
    public static boolean hasProgrammerArt(){
        return mc.getResourcePackRepository().getSelectedPacks().stream().anyMatch(p->p.getId().equals("programmer_art"));
    }
    public static void playSimpleUISound(SoundEvent sound, float grave, float volume){
        mc.getSoundManager().play(SimpleSoundInstance.forUI(sound, grave, volume));
    }
    public static void playSimpleUISound(SoundEvent sound, float grave){
        mc.getSoundManager().play(SimpleSoundInstance.forUI(sound, grave));
    }
    public static void addTip(Entity entity){
        if (hasTip(entity.getType())) mc.getToasts().addToast(new LegacyTip(entity.getType().getDescription(), ScreenUtil.getTip(entity.getType())));
        else if (entity.getPickResult() != null && !entity.getPickResult().isEmpty() && hasTip(entity.getPickResult())) addTip(entity.getPickResult());
    }
    public static void addTip(ItemStack stack){
        if (hasTip(stack)) mc.getToasts().addToast(new LegacyTip(stack));
    }
    public static Component getTip(ItemStack item){
        return hasValidTipOverride(item) ? LegacyTipOverride.getOverride(item) : Component.translatable(getTipId(item));
    }
    public static Component getTip(EntityType<?> type){
        return hasValidTipOverride(type) ? LegacyTipOverride.getOverride(type) : Component.translatable(getTipId(type));
    }
    public static boolean hasTip(ItemStack item){
        return hasTip(getTipId(item)) || hasValidTipOverride(item);
    }
    public static boolean hasValidTipOverride(ItemStack item){
        return !LegacyTipOverride.getOverride(item).getString().isEmpty() && hasTip(((TranslatableContents)LegacyTipOverride.getOverride(item).getContents()).getKey());
    }
    public static boolean hasValidTipOverride(EntityType<?> type){
        return !LegacyTipOverride.getOverride(type).getString().isEmpty() && hasTip(((TranslatableContents)LegacyTipOverride.getOverride(type).getContents()).getKey());
    }
    public static boolean hasTip(String s){
        return Language.getInstance().has(s);
    }
    public static boolean hasTip(EntityType<?> s){
        return hasTip(getTipId(s)) || hasValidTipOverride(s);
    }
    public static String getTipId(ItemStack item){
        return item.getDescriptionId() + ".tip";
    }
    public static String getTipId(EntityType<?> item){
        return item.getDescriptionId() + ".tip";
    }
    public static Component getTip(ResourceLocation location){
        return Component.translatable(location.toLanguageKey() +".tip");
    }
    public static LegacyOptions getLegacyOptions(){
        return (LegacyOptions) mc.options;
    }

    public static void drawGenericLoading(GuiGraphics graphics,int x, int y) {
        RenderSystem.enableBlend();
        for (int i = 0; i < 8; i++) {
            int v = (i + 1) * 100;
            int n = (i + 3) * 100;
            float l = (Util.getMillis() / 4f) % 1000;
            float alpha = l >= v - 100  ? (l <= v ? l / v: (n - l) / 200f) : 0;
            if (alpha > 0) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
                graphics.blitSprite(LOADING_BLOCK_SPRITE, x+ (i <= 2 ? i : i >= 4 ? i == 7 ? 0 : 6 - i : 2) * 27, y + (i <= 2 ? 0 : i == 3 || i == 7 ? 1 : 2)* 27, 21, 21);
            }
        }
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
    }

    public static void renderScrollingString(GuiGraphics guiGraphics, Font font, Component component, int j, int k, int l, int m, int n, boolean shadow) {
        int o = font.width(component);
        int p = (k + m - font.lineHeight) / 2 + 1;
        int q = l - j;
        if (o > q) {
            int r = o - q;
            double d = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double)r * 0.5, 3.0);
            double f = Math.sin(1.5707963267948966 * Math.cos(Math.PI * 2 * d / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, r);
            guiGraphics.enableScissor(j, k, l, m);
            guiGraphics.drawString(font, component, j - (int)g, p, n,shadow);
            guiGraphics.disableScissor();
        } else {
            guiGraphics.drawString(font, component, j, p, n,shadow);
        }
    }
    public static void secureTranslucentRender(GuiGraphics graphics, boolean translucent, float alpha, Runnable render){
        if (translucent){
            LegacyMinecraftClient.guiBufferSourceOverride = BufferSourceWrapper.translucent(graphics.bufferSource());
            graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
            RenderSystem.enableBlend();
        }
        render.run();
        if (translucent){
            RenderSystem.disableBlend();
            graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            LegacyMinecraftClient.guiBufferSourceOverride = null;
        }
    }
    public static boolean isHovering(Slot slot,int leftPos, int topPos,  double d, double e) {
        LegacyIconHolder holder = ScreenUtil.iconHolderRenderer.slotBounds(slot);
        int width = holder.getWidth();
        int height = holder.getHeight();
        double xCorner = holder.getXCorner() + (holder.offset != null ? holder.offset.x() : 0);
        double yCorner = holder.getYCorner() + (holder.offset != null ? holder.offset.y() : 0);
        return (d -= leftPos) >= xCorner && d < (xCorner + width) && (e -= topPos) >= yCorner && e < (yCorner + height);
    }
}
