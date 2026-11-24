package com.byfilosof.client;

import com.byfilosof.ItemHelperMod;
import net.minecraft.client.KeyMapping;

public final class ItemHelperBindings {
	public static final KeyMapping.Category CATEGORY =
			KeyMapping.Category.register(ItemHelperMod.id("main"));

	private ItemHelperBindings() {
	}

	public static void register() {
		for (HelperItem item : HelperItem.values()) {
			item.register(CATEGORY);
		}
	}
}
