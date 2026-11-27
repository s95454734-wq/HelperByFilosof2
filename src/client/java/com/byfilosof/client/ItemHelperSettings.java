package com.byfilosof.client;

public final class ItemHelperSettings {
	/** Количество слотов хотбара. */
	public static final int HOTBAR_SIZE = 9;

	/** Основной инвентарь: хотбар 0..8 + 27 слотов 9..35. */
	public static final int MAIN_INVENTORY_SIZE = 36;

	/**
	 * Тиков ожидания после подмены/смены слота перед первой проверкой руки.
	 * 1 тик = 50 мс при 20 TPS.
	 */
	public static final int USE_DELAY_TICKS = 1;

	/**
	 * Тиков ожидания после ПКМ перед возвратом предмета.
	 * 1 тик даёт серверу время обработать использование до обратного SWAP.
	 */
	public static final int RESTORE_DELAY_TICKS = 1;

	/**
	 * Сколько тиков ждать, пока нужный предмет подтвердится в активном слоте.
	 * Если за это время предмет не появился — ПКМ не выполняется.
	 */
	public static final int HAND_CHECK_TIMEOUT_TICKS = 6;

	/** Максимум нажатий в очереди, чтобы удержание клавиши не спамило пакетами. */
	public static final int MAX_QUEUED_USES = 12;

	private ItemHelperSettings() {
	}
}
