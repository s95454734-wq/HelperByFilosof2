package com.byfilosof.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

public enum HelperItem {
	FIRE_CHARGE(Items.FIRE_CHARGE, "item.minecraft.fire_charge",
			"key.itemhelper.use.fire_charge", GLFW.GLFW_KEY_KP_1),
	ENDER_EYE(Items.ENDER_EYE, "item.minecraft.ender_eye",
			"key.itemhelper.use.ender_eye", GLFW.GLFW_KEY_KP_2),
	NETHERITE_SCRAP(Items.NETHERITE_SCRAP, "item.minecraft.netherite_scrap",
			"key.itemhelper.use.netherite_scrap", GLFW.GLFW_KEY_KP_3),
	DRIED_KELP(Items.DRIED_KELP, "item.minecraft.dried_kelp",
			"key.itemhelper.use.dried_kelp", GLFW.GLFW_KEY_KP_4),
	SUGAR(Items.SUGAR, "item.minecraft.sugar",
			"key.itemhelper.use.sugar", GLFW.GLFW_KEY_KP_5),
	PHANTOM_MEMBRANE(Items.PHANTOM_MEMBRANE, "item.minecraft.phantom_membrane",
			"key.itemhelper.use.phantom_membrane", GLFW.GLFW_KEY_KP_6);

	private final Item item;
	private final String displayKey;
	private final String translationKey;
	private final int defaultKeyCode;
	private KeyMapping keyMapping;

	HelperItem(Item item, String displayKey, String translationKey, int defaultKeyCode) {
		this.item = item;
		this.displayKey = displayKey;
		this.translationKey = translationKey;
		this.defaultKeyCode = defaultKeyCode;
	}

	public void register(KeyMapping.Category category) {
		this.keyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				translationKey,
				InputConstants.Type.KEYSYM,
				defaultKeyCode,
				category));
	}

	public Item item() {
		return item;
	}

	public String displayKey() {
		return displayKey;
	}

	public boolean consumeClick() {
		return keyMapping != null && keyMapping.consumeClick();
	}
}
