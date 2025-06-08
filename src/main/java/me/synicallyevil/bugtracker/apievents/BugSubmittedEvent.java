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

package me.synicallyevil.bugtracker.apievents;

import me.synicallyevil.bugtracker.BugTracker;
import me.synicallyevil.bugtracker.data.Bugs;
import me.synicallyevil.bugtracker.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BugSubmittedEvent extends Event implements Cancellable {

    private static final HandlerList handlers  = new HandlerList();

    private boolean cancelled;
    private Player player;
    private String report;

    public BugSubmittedEvent(Player player, String report){
        this.player = player;
        this.report = report;

        if(!(cancelled)){
            new Bugs(player, report).addReport().build();

            player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.submitted", "&aReport submitted!")));
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer(){
        return player;
    }

    public String getReport(){
        return report;
    }
}
