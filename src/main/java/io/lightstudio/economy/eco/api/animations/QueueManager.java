package io.lightstudio.economy.eco.api.animations;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class QueueManager {
    private static final Map<Player, Queue<TitleCountUpAndDown>> playerQueues = new HashMap<>();

    public static void addToQueue(Player player, TitleCountUpAndDown titleCountUp) {
        Queue<TitleCountUpAndDown> queue = playerQueues.computeIfAbsent(player, k -> new LinkedList<>());
        queue.add(titleCountUp);
        if (queue.size() == 1) {
            titleCountUp.processQueue();
        }
    }

    public static void removeQueue(Player player) {
        playerQueues.remove(player);
    }

    public static Queue<TitleCountUpAndDown> getQueue(Player player) {
        return playerQueues.get(player);
    }
}