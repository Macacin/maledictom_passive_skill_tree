package daripher.skilltree.skill.bonus.event.intelligence;

import net.minecraft.world.entity.player.Player;

import java.lang.ThreadLocal;

public class ContextHelper {
    public static final ThreadLocal<Player> CURRENT_ENCHANTER = new ThreadLocal<>();
}