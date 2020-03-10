package com.fuzs.deathfinder.common;

import com.fuzs.deathfinder.config.ConfigBuildHandler;
import com.fuzs.deathfinder.config.StringListBuilder;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public class DeathMessageHelper {

    private final StringListBuilder<EntityType<?>> parser = new StringListBuilder<>(ForgeRegistries.ENTITIES);
    private Set<EntityType<?>> blacklist;

    private boolean resetGamerule;

    public void handleTamed(TameableEntity tameable, DeathMessage message, ServerPlayerEntity owner) {

        if (tameable.getEntityWorld() instanceof ServerWorld) {

            // disable gamerule to prevent vanilla logic from running
            tameable.getEntityWorld().getGameRules().get(GameRules.SHOW_DEATH_MESSAGES).set(false, null);
            this.resetGamerule = true;

            owner.sendMessage(message.getMessage(owner));
        }
    }

    public void handlePlayer(ServerPlayerEntity player, DeathMessage message, MessageSender sender) {

        // disable gamerule to prevent vanilla logic from running
        player.getEntityWorld().getGameRules().get(GameRules.SHOW_DEATH_MESSAGES).set(false, null);
        this.resetGamerule = true;

        ITextComponent itextcomponent = message.getMessage(player);
        player.connection.sendPacket(new SCombatPacket(player.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent), (p_212356_2_) -> {

            if (!p_212356_2_.isSuccess()) {

                String s = itextcomponent.getStringTruncated(256);
                ITextComponent itextcomponent1 = new TranslationTextComponent("death.attack.message_too_long",
                        new StringTextComponent(s).applyTextStyle(TextFormatting.YELLOW));
                ITextComponent itextcomponent2 = (new TranslationTextComponent("death.attack.even_more_magic", player.getDisplayName()))
                        .applyTextStyle((p_212357_1_) -> p_212357_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent1)));
                player.connection.sendPacket(new SCombatPacket(player.getCombatTracker(), SCombatPacket.Event.ENTITY_DIED, itextcomponent2));
            }
        });

        Team team = player.getTeam();
        if (team != null && team.getDeathMessageVisibility() != Team.Visible.ALWAYS) {

            if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OTHER_TEAMS) {

                sender.sendMessageToAllTeamMembers(player, message);
            } else if (team.getDeathMessageVisibility() == Team.Visible.HIDE_FOR_OWN_TEAM) {

                sender.sendMessageToTeamOrAllPlayers(player, message);
            }
        } else {

            sender.sendMessage(message);
        }
    }

    public boolean getReset() {

        return this.resetGamerule;
    }

    public boolean isAllowed(EntityType<?> type) {

        return !this.getBlacklist().contains(type);
    }

    private Set<EntityType<?>> getBlacklist() {

        if (this.blacklist == null) {
            this.syncBlacklist();
        }

        return this.blacklist;
    }

    public void syncBlacklist() {

        this.blacklist = this.parser.buildEntrySetWithCondition(ConfigBuildHandler.GENERAL_CONFIG.entityBlacklist.get(), type -> type.getClassification() != EntityClassification.MISC, "No instance of LivingEntity");
    }

}
