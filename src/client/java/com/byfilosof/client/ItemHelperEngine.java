package com.byfilosof.client;

import java.util.ArrayDeque;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemHelperEngine {
	private static final Queue<HelperItem> QUEUE = new ArrayDeque<>();
	private static ActiveUse active;

	private ItemHelperEngine() {
	}

	public static void tick(Minecraft client) {
		pollBindings();

		if (!canWork(client)) {
			abort(client);
			return;
		}

		if (active == null) {
			HelperItem requested = QUEUE.poll();
			if (requested == null) {
				return;
			}
			active = begin(client, requested);
			if (active == null) {
				return;
			}
		}

		int guard = 4;
		while (active != null && guard-- > 0) {
			if (active.delayTicks > 0) {
				active.delayTicks--;
				return;
			}

			switch (active.stage) {
				case WAIT_USE -> {
					// Главная проверка: не жмём ПКМ, пока сервер/клиент не подтвердили,
					// что нужный предмет действительно лежит в активном слоте.
					if (!holding(client, active.requested)) {
						active.handCheckTicks++;
						if (active.handCheckTicks >= ItemHelperSettings.HAND_CHECK_TIMEOUT_TICKS) {
							notifyNotReady(client, active.requested);
							restore(client, active);
							active = null;
							return;
						}
						active.delayTicks = 1;
						return;
					}

					use(client);
					active.stage = Stage.WAIT_RESTORE;
					active.delayTicks = ItemHelperSettings.RESTORE_DELAY_TICKS;
				}
				case WAIT_RESTORE -> {
					restore(client, active);
					active = null;
				}
			}
		}
	}

	private static void pollBindings() {
		for (HelperItem item : HelperItem.values()) {
			while (item.consumeClick()) {
				if (QUEUE.size() < ItemHelperSettings.MAX_QUEUED_USES) {
					QUEUE.add(item);
				}
			}
		}
	}

	private static boolean canWork(Minecraft client) {
		LocalPlayer player = client.player;
		return player != null
				&& client.level != null
				&& client.gameMode != null
				&& client.screen == null
				&& player.containerMenu == player.inventoryMenu;
	}

	private static void abort(Minecraft client) {
		if (active != null) {
			restore(client, active);
			active = null;
		}
		QUEUE.clear();
	}

	private static ActiveUse begin(Minecraft client, HelperItem helperItem) {
		LocalPlayer player = client.player;
		Inventory inventory = player.getInventory();
		int originalSelected = inventory.getSelectedSlot();
		int sourceSlot = findItem(inventory, helperItem.item(), originalSelected);

		if (sourceSlot < 0) {
			notifyMissing(client, helperItem);
			return null;
		}

		// Предмет уже в активном слоте: просто ПКМ без лишних перестановок.
		if (sourceSlot == originalSelected) {
			client.gameMode.useItem(player, InteractionHand.MAIN_HAND);
			return null;
		}

		ActiveUse use = new ActiveUse(
				helperItem,
				originalSelected,
				ItemHelperSettings.USE_DELAY_TICKS);

		if (sourceSlot < ItemHelperSettings.HOTBAR_SIZE) {
			// Предмет в другом слоте хотбара: переключаем выбранный слот.
			inventory.setSelectedSlot(sourceSlot);
			if (player.connection != null) {
				player.connection.send(new ServerboundSetCarriedItemPacket(sourceSlot));
			}
			use.switchedHotbar = true;
		} else {
			// Предмет в основном инвентаре: обычный SWAP-клик с активным слотом.
			// В InventoryMenu слоты 9..35 совпадают с номерами слотов инвентаря.
			use.swapped = true;
			use.menuSlot = sourceSlot;
			client.gameMode.handleInventoryMouseClick(
					player.inventoryMenu.containerId,
					sourceSlot,
					originalSelected,
					ClickType.SWAP,
					player);
		}

		return use;
	}

	private static boolean holding(Minecraft client, HelperItem item) {
		LocalPlayer player = client.player;
		return player != null
				&& player.getInventory().getSelectedItem().is(item.item());
	}

	private static void use(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null || client.gameMode == null) {
			return;
		}
		client.gameMode.useItem(player, InteractionHand.MAIN_HAND);
	}

	private static void restore(Minecraft client, ActiveUse use) {
		LocalPlayer player = client.player;
		if (player == null || client.gameMode == null) {
			return;
		}

		if (use.swapped) {
			client.gameMode.handleInventoryMouseClick(
					player.inventoryMenu.containerId,
					use.menuSlot,
					use.originalSelected,
					ClickType.SWAP,
					player);
		}

		if (use.switchedHotbar) {
			player.getInventory().setSelectedSlot(use.originalSelected);
			if (player.connection != null) {
				player.connection.send(new ServerboundSetCarriedItemPacket(use.originalSelected));
			}
		}
	}

	private static int findItem(Inventory inventory, Item item, int selectedSlot) {
		ItemStack held = inventory.getItem(selectedSlot);
		if (!held.isEmpty() && held.is(item)) {
			return selectedSlot;
		}

		for (int slot = 0; slot < ItemHelperSettings.MAIN_INVENTORY_SIZE; slot++) {
			ItemStack stack = inventory.getItem(slot);
			if (!stack.isEmpty() && stack.is(item)) {
				return slot;
			}
		}
		return -1;
	}

	private static void notifyMissing(Minecraft client, HelperItem item) {
		if (client.player == null) {
			return;
		}
		client.player.displayClientMessage(
				Component.translatable("text.itemhelper.not_found",
						Component.translatable(item.displayKey())),
				true);
	}

	private static void notifyNotReady(Minecraft client, HelperItem item) {
		if (client.player == null) {
			return;
		}
		client.player.displayClientMessage(
				Component.translatable("text.itemhelper.not_ready",
						Component.translatable(item.displayKey())),
				true);
	}

	private enum Stage {
		WAIT_USE,
		WAIT_RESTORE
	}

	private static final class ActiveUse {
		private final HelperItem requested;
		private final int originalSelected;
		private int menuSlot = -1;
		private boolean swapped;
		private boolean switchedHotbar;
		private int handCheckTicks;
		private int delayTicks;
		private Stage stage = Stage.WAIT_USE;

		private ActiveUse(HelperItem requested, int originalSelected, int delayTicks) {
			this.requested = requested;
			this.originalSelected = originalSelected;
			this.delayTicks = delayTicks;
		}
	}
}
