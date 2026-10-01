package com.backroomsevent;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class BackroomsMod implements ModInitializer {
    public static final String MOD_ID = "backroomsevent";
    public static final Identifier EVENT_PACKET = new Identifier(MOD_ID, "event_packet");

    public static boolean isGameActive = false;
    public static int defaultTimer = 600;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            // /function or /bacroms game_ivent
            dispatcher.register(CommandManager.literal("bacroms")
                .then(CommandManager.literal("game_ivent")
                    .executes(context -> {
                        isGameActive = true;
                        context.getSource().sendFeedback(() -> Text.literal("§a[Закулисье] Ивент АКТИВИРОВАН!"), true);
                        return 1;
                    })
                )
                .then(CommandManager.literal("ivent")
                    .then(CommandManager.literal("start")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .executes(context -> {
                                if (!isGameActive) {
                                    context.getSource().sendError(Text.literal("§cОШИБКА: Ивент ещё не активирован! Сначала введите /bacroms game_ivent"));
                                    return 0;
                                }
                                ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                                sendEventPacket(target, "START_SUBJECT");
                                context.getSource().sendFeedback(() -> Text.literal("§a[Закулисье] Ивент запущен для " + target.getName().getString()), true);
                                return 1;
                            })
                        )
                    )
                )
                .then(CommandManager.literal("start_ruk")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(context -> {
                            if (!isGameActive) {
                                context.getSource().sendError(Text.literal("§cОШИБКА: Ивент ещё не активирован! Сначала введите /bacroms game_ivent"));
                                return 0;
                            }
                            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");
                            sendEventPacket(target, "START_LEADER");
                            context.getSource().sendFeedback(() -> Text.literal("§a[Закулисье] Назначен руководитель: " + target.getName().getString()), true);
                            return 1;
                        })
                    )
                )
                .then(CommandManager.literal("stop")
                    .executes(context -> {
                        isGameActive = false;
                        for (ServerPlayerEntity p : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                            sendEventPacket(p, "STOP");
                        }
                        context.getSource().sendFeedback(() -> Text.literal("§e[Закулисье] Ивент остановлен."), true);
                        return 1;
                    })
                )
            );
        });
    }

    private void sendEventPacket(ServerPlayerEntity player, String type) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(type);
        ServerPlayNetworking.send(player, EVENT_PACKET, buf);
    }
}
