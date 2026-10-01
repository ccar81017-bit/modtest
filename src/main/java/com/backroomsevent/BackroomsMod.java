package com.backroomsevent;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BackroomsMod implements ModInitializer {
    public static final String MOD_ID = "backrooms";
    public static final Identifier EVENT_PACKET = Identifier.of(MOD_ID, "event_packet");
    public static boolean isGameActive = false;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("bacroms")
                .then(CommandManager.literal("game_ivent")
                    .executes(context -> {
                        isGameActive = true;
                        context.getSource().sendFeedback(() -> Text.literal("§a[Закулисье] Ивент АКТИВИРОВАН!"), true);
                        return 1;
                    })
                )
                .then(CommandManager.literal("stop")
                    .executes(context -> {
                        isGameActive = false;
                        context.getSource().sendFeedback(() -> Text.literal("§e[Закулисье] Ивент остановлен."), true);
                        return 1;
                    })
                )
            );
        });
    }
}
