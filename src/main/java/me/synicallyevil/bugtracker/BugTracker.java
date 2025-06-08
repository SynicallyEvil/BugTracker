/*
 *
 *     This file is part of BugTracker.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.synicallyevil.bugtracker;

import me.synicallyevil.bugtracker.commands.BugCommand;
import me.synicallyevil.bugtracker.data.Database;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BugTracker extends JavaPlugin {

    private static BugTracker instance;
    private static Database db;

    public HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public void onEnable(){
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        getCommand("bugs").setExecutor(new BugCommand());

        if(getConfig().isSet("database.enabled") && getConfig().getBoolean("database.enabled") && getConfig().isSet("database.host") && getConfig().isSet("database.port")
                && getConfig().isSet("database.username") && getConfig().isSet("database.password") && getConfig().isSet("database.database_name")){


            db = new Database(getConfig().getString("database.table_name", "bugtracker_Bugs"), getConfig().getString("database.host"), getConfig().getInt("database.port"), getConfig().getString("database.username"),
                    getConfig().getString("database.password"), getConfig().getString("database.database_name"));

            db.enableConnection();
        }
    }

    @Override
    public void onDisable(){
        if(!(db == null))
            getDB().disableConnection();
    }

    public static BugTracker getInstance(){
        return instance;
    }

    public static Database getDB(){
        return db;
    }

    public void addToCooldown(UUID uuid){
        if(cooldown.containsKey(uuid))
            cooldown.replace(uuid, System.currentTimeMillis());
        else
            cooldown.put(uuid, System.currentTimeMillis());
    }

    public void removeFromCooldown(UUID uuid){
        cooldown.remove(uuid);
    }

    public boolean isCooldownEnabled(Player player){
        int c = getConfig().getInt("global.cooldown_in_seconds", 5);

        return c > 0 && !(player.hasPermission("bugtracker.bypass"));
    }

    public List<String> getReportInfo(){
        return getConfig().getStringList("messages.reportinfo");
    }

    public String getString(String path, String def){
        return getConfig().getString(path, def);
    }

    public int getInt(String path, int def){
        return getConfig().getInt(path, def);
    }

    public boolean isDBEnabled(){
        return getConfig().getBoolean("database.enabled") && db != null && db.isConnected();
    }
}