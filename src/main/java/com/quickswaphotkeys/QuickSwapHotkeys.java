package com.quickswaphotkeys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = QuickSwapHotkeys.MOD_ID, dist = Dist.CLIENT)
public class QuickSwapHotkeys {

    public static final String MOD_ID = "quick_swap_hotkeys";

    private static final int CHEST_SLOT = 6;
    private static final int INV_START = 9;
    private static final int HOTBAR_END = 44;
    private static final int OFFHAND_SLOT = 45;

    private static KeyMapping totemKey;
    private static KeyMapping elytraKey;

    public QuickSwapHotkeys(IEventBus modBus) {
        modBus.addListener(this::registerKeys);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void registerKeys(RegisterKeyMappingsEvent event) {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main"));

        totemKey = new KeyMapping("key.quick_swap_hotkeys.totem", InputConstants.KEY_X, category);
        elytraKey = new KeyMapping("key.quick_swap_hotkeys.elytra", InputConstants.KEY_G, category);

        event.register(totemKey);
        event.register(elytraKey);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        while (totemKey.consumeClick()) {
            if (ready(mc)) equipTotem(mc, mc.player);
        }
        while (elytraKey.consumeClick()) {
            if (ready(mc)) swapChest(mc, mc.player);
        }
    }

    private static boolean ready(Minecraft mc) {
        return mc.player != null && mc.gameMode != null && !mc.player.isSpectator();
    }

    private static void equipTotem(Minecraft mc, LocalPlayer player) {
        InventoryMenu menu = player.inventoryMenu;
        if (menu.getSlot(OFFHAND_SLOT).getItem().is(Items.TOTEM_OF_UNDYING)) return;

        for (int i = INV_START; i <= HOTBAR_END; i++) {
            if (menu.getSlot(i).getItem().is(Items.TOTEM_OF_UNDYING)) {
                mc.gameMode.handleContainerInput(menu.containerId, i, 40, ContainerInput.SWAP, player);
                return;
            }
        }
    }

    private static void swapChest(Minecraft mc, LocalPlayer player) {
        InventoryMenu menu = player.inventoryMenu;
        boolean wantChestplate = menu.getSlot(CHEST_SLOT).getItem().is(Items.ELYTRA);

        int best = -1;
        boolean bestOff = false;
        int bestDur = -1;
        int bestEnch = -1;

        for (int i = INV_START; i <= OFFHAND_SLOT; i++) {
            ItemStack s = menu.getSlot(i).getItem();
            if (!matches(s, wantChestplate)) continue;

            boolean off = i == OFFHAND_SLOT;
            int dur = s.getMaxDamage() - s.getDamageValue();
            int ench = enchantLevels(s);

            if (best == -1 || isBetter(off, dur, ench, bestOff, bestDur, bestEnch)) {
                best = i;
                bestOff = off;
                bestDur = dur;
                bestEnch = ench;
            }
        }
        if (best == -1) return;

        int id = menu.containerId;
        mc.gameMode.handleContainerInput(id, best, 0, ContainerInput.PICKUP, player);
        mc.gameMode.handleContainerInput(id, CHEST_SLOT, 0, ContainerInput.PICKUP, player);
        if (!menu.getCarried().isEmpty()) {
            mc.gameMode.handleContainerInput(id, best, 0, ContainerInput.PICKUP, player);
        }
    }

    private static boolean matches(ItemStack s, boolean wantChestplate) {
        if (s.isEmpty()) return false;
        if (wantChestplate) return s.is(ItemTags.CHEST_ARMOR) && !s.is(Items.ELYTRA);
        return s.is(Items.ELYTRA) && s.getDamageValue() < s.getMaxDamage() - 1;
    }

    private static boolean isBetter(boolean off, int dur, int ench, boolean bOff, int bDur, int bEnch) {
        if (off != bOff) return off;
        if (dur != bDur) return dur > bDur;
        return ench > bEnch;
    }

    private static int enchantLevels(ItemStack s) {
        ItemEnchantments e = s.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        int sum = 0;
        for (Holder<Enchantment> h : e.keySet()) {
            sum += e.getLevel(h);
        }
        return sum;
    }
}
