package fuzs.deathfinder.handler;

import fuzs.deathfinder.DeathFinder;
import fuzs.deathfinder.config.ConfigBuildHandler;
import fuzs.deathfinder.util.DeathMessageBuilder;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.scores.Team;

public class DeathMessageHelper {

    public void handleTamed(ServerPlayer owner, DeathMessageBuilder message) {

        if (ConfigBuildHandler.MESSAGES_TAMED.get()) {
            owner.sendMessage(message.getMessage(owner), Util.NIL_UUID);
        }
    }

    public void handlePlayer(ServerPlayer player, DeathMessageBuilder message, MessageSender sender) {

        if (!ConfigBuildHandler.MESSAGES_PLAYERS.get()) {

            return;
        }

        // message for death screen is different from chat message
        Component component = player.getCombatTracker().getDeathMessage();
        player.connection.send(new SCombatPacket(player.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, component), future -> {

            if (!future.isSuccess()) {

                String s = component.getStringTruncated(256);
                Component itextcomponent1 = new TranslationTextComponent("death.attack.message_too_long",
                        new TextComponent(s).mergeStyle(TextFormatting.YELLOW));
                Component itextcomponent2 = (new TranslatableComponent("death.attack.even_more_magic", player.getDisplayName()))
                        .withStyle((p_212357_1_) -> p_212357_1_.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent1)));
                player.connection.send(new SCombatPacket(player.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent2));
            }
        });

        Team team = player.getTeam();
        if (team != null && team.getDeathMessageVisibility() != Team.Visibility.ALWAYS) {

            if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {

                sender.sendMessageToAllTeamMembers(player, message);
            } else if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {

                sender.sendMessageToTeamOrAllPlayers(player, message);
            }
        } else {

            sender.sendMessage(message);
        }
    }

    public boolean isAllowed(EntityType<?> type) {
        if (DeathFinder.CONFIG.server().messages.deathMessageWhitelist.contains(type)) return true;
        return !DeathFinder.CONFIG.server().messages.deathMessageBlacklist.contains(type);
    }
}
