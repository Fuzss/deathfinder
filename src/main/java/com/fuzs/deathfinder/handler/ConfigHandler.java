package com.fuzs.deathfinder.handler;

import com.fuzs.deathfinder.DeathFinder;
import net.minecraftforge.common.config.Config;

@SuppressWarnings("WeakerAccess")
@Config(modid = DeathFinder.MODID)
public class ConfigHandler {

	@Config.Name("Coordinates In Death Message")
	@Config.Comment("Add coordinates to the end of ever death message.")
	public static boolean deathMessage = true;

	@Config.Name("Coordinates On Death Screen")
	@Config.Comment("Show current player coordinates on the death screen.")
	public static boolean deathScreen = true;

	@Config.Name("Register Command")
	@Config.Comment("Register a custom teleport command for teleporting across dimensions.")
	@Config.RequiresMcRestart
	public static boolean tpxEnable = true;

	@Config.Name("Command Name")
	@Config.Comment("Name for the custom teleport command added. Leave empty for \"tpx\" to be used.")
	@Config.RequiresMcRestart
	public static String tpxName = "";

	@Config.Name("Death Message Command")
	@Config.Comment("Command entered into chat when clicking the death message coordinates. %d is replaced by x-coordinate, y-coordinate, z-coordinate and dimension. This has to be adjusted when something about the custom teleport command has been altered.")
	public static String deathMessageCommand = "/tpx @s %d %d %d %d";

	@Config.Name("Named Entity Deaths")
	@Config.Comment("Show death messages for named entities.")
	@Config.RequiresMcRestart
	public static boolean namedEntities = true;
	
}
