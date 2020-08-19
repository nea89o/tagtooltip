package com.romangraef.tagtooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static com.romangraef.tagtooltip.TagTooltip.MODID;

@Mod(MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TagTooltip {
    public static final String MODID = "tagtooltip";
    public static final Logger log = LogManager.getLogger(MODID);
    public KeyBinding showTagsBinding = new KeyBinding("key.showtags", KeyConflictContext.UNIVERSAL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.categories.inventory");
    public boolean isShowTagsDown = false;

    public TagTooltip() {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            log.warn("Tag Tooltips is a client side only mod.");
        } else {
            IEventBus eventBus = MinecraftForge.EVENT_BUS;
            eventBus.register(this);
            ClientRegistry.registerKeyBinding(showTagsBinding);
        }
    }

    private boolean isKeyBindDown(KeyBinding keyBinding) {
        if (keyBinding.isKeyDown()) return true;
        InputMappings.Input key = keyBinding.getKey();
        int keyCode = key.getKeyCode();
        // GL window handle
        long handle = Minecraft.getInstance().getMainWindow().getHandle();
        return InputMappings.isKeyDown(handle, keyCode) && keyBinding.getKeyModifier().isActive(keyBinding.getKeyConflictContext());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onTooltip(ItemTooltipEvent event) {
        if (event == null) return;
        List<ITextComponent> toolTip = event.getToolTip();
        if (!isKeyBindDown(showTagsBinding)) {
            toolTip.add(new TranslationTextComponent("tooltip.showTags", showTagsBinding.func_238171_j_()));
            return;
        }
        for (ResourceLocation tag : event.getItemStack().getItem().getTags()) {
            toolTip.add(new TranslationTextComponent("tooltip.tag.prefix", new StringTextComponent(tag.toString())));
        }
    }

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }
}
