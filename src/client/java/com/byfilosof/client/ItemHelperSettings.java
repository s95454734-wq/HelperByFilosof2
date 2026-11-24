package com.byfilosof.client;

public final class ItemHelperSettings {
	/** Количество слотов хотбара. */
	public static final int HOTBAR_SIZE = 9;

	/** Основной инвентарь: хотбар 0..8 + 27 слотов 9..35. */
	public static final int MAIN_INVENTORY_SIZE = 36;

	/**
	 * Тиков ожидания после подмены/смены слота перед ПКМ.
	 * 1 тик = 50 мс при 20 TPS. Ноль делает всё в один тик.
	 */
	public static final int USE_DELAY_TICKS = 1;

	/**
	 * Тиков ожидания после ПКМ перед возвратом предмета.
	 * Ноль возвращает в тот же тик, 1 даёт ещё ~50 мс.
	 */
	public static final int RESTORE_DELAY_TICKS = 0;

	/** Максимум нажатий в очереди, чтобы удержание клавиши не спамило пакетами. */
	public static final int MAX_QUEUED_USES = 12;

	private ItemHelperSettings() {
	}
}
