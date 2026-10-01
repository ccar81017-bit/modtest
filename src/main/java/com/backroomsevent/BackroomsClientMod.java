package com.backroomsevent;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class BackroomsClientMod implements ClientModInitializer {
    private static String role = "NONE"; // "SUBJECT", "LEADER", "NONE"

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(BackroomsMod.EVENT_PACKET, (client, handler, buf, responseSender) -> {
            String type = buf.readString();
            client.execute(() -> {
                if ("START_SUBJECT".equals(type)) {
                    role = "SUBJECT";
                } else if ("START_LEADER".equals(type)) {
                    role = "LEADER";
                } else if ("STOP".equals(type)) {
                    role = "NONE";
                }
            });
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if ("NONE".equals(role)) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();
            TextRenderer textRenderer = client.textRenderer;

            if ("SUBJECT".equals(role)) {
                // Actionbar text: ПОДОПЫТНЫЙ
                String status = "ПОДОПЫТНЫЙ";
                drawContext.drawCenteredTextWithShadow(textRenderer, Text.literal("§e§l" + status), width / 2, height - 68, 0xFFFF55);
            } else if ("LEADER".equals(role)) {
                // Actionbar text: РУКОВОДИТЕЛЬ ИВЕНТА
                String status = "РУКОВОДИТЕЛЬ ИВЕНТА";
                drawContext.drawCenteredTextWithShadow(textRenderer, Text.literal("§c§l" + status), width / 2, height - 68, 0xFF5555);
            }
        });
    }
}
