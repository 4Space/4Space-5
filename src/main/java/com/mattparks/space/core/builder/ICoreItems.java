package com.mattparks.space.core.builder;

/**
 * A interface for creating and registering items.
 */
public interface ICoreItems {
	/**
	 * Used to create the item objects.
	 */
	public void initItems();

	/**
	 * Used to register the items to minecraft, using {@link com.mattparls.space.core.registerItem(Item)}.
	 */
	public void registerItems();
	
	/**
	 * Used to set item harvest levels.
	 */
	public void registerHarvestLevels();
}
