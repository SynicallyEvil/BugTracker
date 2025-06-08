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

package me.synicallyevil.bugtracker.data;

import java.util.UUID;

public class BugUtils {

    private int id;
    private String playername;
    private UUID uuid;
    private String report;
    private String created;
    private String world;
    private int x;
    private int y;
    private int z;

    public BugUtils(int id, String playername, UUID uuid, String report, String created, String world, int x, int y, int z){
        this.id = id;
        this.playername = playername;
        this.uuid = uuid;
        this.report = report;
        this.created = created;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getID() {
        return id;
    }

    public String getPlayername() {
        return playername;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getReport() {
        return report;
    }

    public String getCreated() {
        return created;
    }

    public String getWorld(){
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
