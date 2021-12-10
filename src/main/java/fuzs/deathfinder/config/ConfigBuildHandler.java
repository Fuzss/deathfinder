package fuzs.deathfinder.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ConfigBuildHandler {

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	// death items
	public static final ForgeConfigSpec.BooleanValue IGNORE_KEEP_INVENTORY;
	public static final ForgeConfigSpec.BooleanValue ITEMS_ON_LOST;

	static {

		BUILDER.push("death_items");
		IGNORE_KEEP_INVENTORY = ConfigBuildHandler.BUILDER.comment("Give map and compass items even when the \"keepInventory\" gamerule is enabled.").define("Ignore Keep Inventory", false);
		ITEMS_ON_LOST = ConfigBuildHandler.BUILDER.comment("Give death items only to players that dropped items from their inventory upon dying.").define("No Empty Inventory ", false);
		BUILDER.pop();
	}

	public static final ForgeConfigSpec SPEC = BUILDER.build();
	
}
