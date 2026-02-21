package pl.npcclick;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NpcClickMod implements ClientModInitializer {

    public static final String MOD_ID = "npcclick";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static KeyBinding toggleKey;
    private static boolean enabled = true;

    public static boolean isEnabled() { return enabled; }

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.npcclick.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.npcclick.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (client.player != null) {
                    String msg = enabled
                            ? "§a[NpcClick] Klikanie NPC przez graczy: WŁĄCZONE"
                            : "§c[NpcClick] Klikanie NPC przez graczy: WYŁĄCZONE";
                    client.player.sendMessage(Text.literal(msg), true);
                }
            }
        });
    }
}
