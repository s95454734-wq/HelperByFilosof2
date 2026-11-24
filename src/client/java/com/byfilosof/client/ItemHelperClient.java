package com.byfilosof.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class ItemHelperClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemHelperBindings.register();
		ClientTickEvents.END_CLIENT_TICK.register(ItemHelperEngine::tick);
	}
}
