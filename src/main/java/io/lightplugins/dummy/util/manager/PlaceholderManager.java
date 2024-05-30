package io.lightplugins.dummy.util.manager;

import io.lightplugins.dummy.util.SubPlaceholder;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlaceholderManager extends PlaceholderExpansion {
    private final ArrayList<SubPlaceholder> subPlaceholder;
    private ArrayList<SubPlaceholder> getSubPlaceholder() {
        return subPlaceholder;
    }

    public PlaceholderManager(ArrayList<SubPlaceholder> subPlaceholder) {
        this.subPlaceholder = subPlaceholder;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lightStudio";
    }

    @Override
    public @NotNull String getAuthor() {
        return "lightStudio";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        for(SubPlaceholder subPlaceholder : getSubPlaceholder()) {
            return subPlaceholder.onRequest(player, params);
        }

        return null;
    }
}
