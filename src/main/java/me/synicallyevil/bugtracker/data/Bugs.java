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

import me.synicallyevil.bugtracker.BugTracker;
import me.synicallyevil.bugtracker.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Bugs {

    private File file = new File(BugTracker.getInstance().getDataFolder(), File.separator + "reports.yml");
    private FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

    private Player initializer;
    private String report;

    private DateFormat dateformat;
    private Date date;

    private int id;

    public Bugs() {
        if(!(BugTracker.getInstance().isDBEnabled()))
            loadFile();


    }

    public Bugs(Player initializer, String report){
        if(!(BugTracker.getInstance().isDBEnabled())){
            loadFile();
            this.id = (listBugs().size()+1);
        }

        this.initializer = initializer;
        this.report = report.substring(0, (report.length()-1));

        this.dateformat = new SimpleDateFormat(BugTracker.getInstance().getString("global.date_format", "dd/MM/yy HH:mm:ss"));
        this.date = new Date();


    }

    public Bugs(int id){
        if(!(BugTracker.getInstance().isDBEnabled()))
            loadFile();

        this.id = id;
    }

    public Bugs(Player initializer){
        if(!(BugTracker.getInstance().isDBEnabled()))
            loadFile();

        this.initializer = initializer;
    }

    private void loadFile(){
        if(!(BugTracker.getInstance().getDataFolder().exists()))
            BugTracker.getInstance().getDataFolder().mkdir();

        if(!(file.exists())){
            try {
                file.createNewFile();
                fc.addDefault("Global.TotalReports", 0);
                fc.createSection("Reports");
                build();
            }catch (IOException e){
                System.out.println("[BugTracker] ERROR CREATING FILE! Check the StackTrace below!");
                e.printStackTrace();
            }
        }

        if(fc.isSet("Global.TotalReports") && Utils.isNumber(fc.getString("Global.TotalReports")))
            this.id = (fc.getInt("Global.TotalReports")+1);
        else
            this.id = 0;
    }

    public Bugs removeReport(){
        if(BugTracker.getInstance().isDBEnabled()){
            BugTracker.getDB().removeReport(this.id);
        }else {
            if (fc.isSet("Reports." + this.id))
                fc.set("Reports." + this.id, null);
        }
        return this;
    }

    public String getInitializer(){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().getPlayer(this.id);

        if(!(fc.isSet("Reports." + this.id + ".Initializer")))
            return "Undefined";

        return fc.getString("Reports." + this.id + ".Initializer");
    }

    public String getInitializer(int id){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().getPlayer(id);

        if(!(fc.isSet("Reports." + id + ".Initializer")))
            return "Undefined";

        return fc.getString("Reports." + id + ".Initializer");
    }

    public String getUUID(){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().getUUID(this.id);

        if(!(fc.isSet("Reports." + this.id + ".UUID")))
            return "Undefined";

        return fc.getString("Reports." + this.id + ".UUID");
    }

    public String getUUID(int id){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().getUUID(id);

        if(!(fc.isSet("Reports." + id + ".UUID")))
            return "Undefined";

        return fc.getString("Reports." + id + ".UUID");
    }

    public String getCreation(){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().getCreation(this.id);

        if(!(fc.isSet("Reports." + this.id + ".Created")))
            return "Undefined";

        return fc.getString("Reports." + this.id + ".Created");
    }

    public String getCreation(int id){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().getCreation(id);

        if(!(fc.isSet("Reports." + id + ".Created")))
            return "Undefined";

        return fc.getString("Reports." + id + ".Created");
    }

    public String getReport(){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().getReport(this.id);

        if(!(fc.isSet("Reports." + this.id + ".Report")))
            return "Undefined";

        return fc.getString("Reports." + this.id + ".Report");
    }

    public String getReport(int id){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().getReport(id);

        if(!(fc.isSet("Reports." + id + ".Report")))
            return "Undefined";

        return fc.getString("Reports." + id + ".Report");
    }

    public String getPosition(){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().getPosition(this.id);

        String world = (fc.isSet("Reports." + this.id + ".Position.World") ? fc.getString("Reports." + this.id + ".Position.World") : "Undefined");
        String x = (fc.isSet("Reports." + this.id + ".Position.X") ? fc.getString("Reports." + this.id + ".Position.X") : "Undefined");
        String y = (fc.isSet("Reports." + this.id + ".Position.Y") ? fc.getString("Reports." + this.id + ".Position.Y") : "Undefined");
        String z = (fc.isSet("Reports." + this.id + ".Position.Z") ? fc.getString("Reports." + this.id + ".Position.Z") : "Undefined");

        return world + " " + x + " " + y + " " + z;
    }

    public Bugs addReport(){
        if(BugTracker.getInstance().isDBEnabled()){
            BugTracker.getDB().addReport(initializer, report, String.valueOf(dateformat.format(date)));
        }else {
            fc.set("Reports." + this.id + ".Initializer", initializer.getName());
            fc.set("Reports." + this.id + ".UUID", initializer.getUniqueId().toString());
            fc.set("Reports." + this.id + ".Created", dateformat.format(date));
            fc.set("Reports." + this.id + ".Report", report);
            fc.set("Reports." + this.id + ".Position.World", initializer.getWorld().getName());
            fc.set("Reports." + this.id + ".Position.X", initializer.getLocation().getBlockX());
            fc.set("Reports." + this.id + ".Position.Y", initializer.getLocation().getBlockY());
            fc.set("Reports." + this.id + ".Position.Z", initializer.getLocation().getBlockZ());
        }

        return this;
    }

    public ArrayList<String> listBugs(){
        ArrayList<String> bugs = new ArrayList<>();

        if(BugTracker.getInstance().isDBEnabled()){
            bugs.addAll(BugTracker.getDB().listBugs());

            return bugs;
        }

        bugs.addAll(fc.getConfigurationSection("Reports").getKeys(false));

        return bugs;
    }

    public ArrayList<String> reportsFromPlayer(String name){
        ArrayList<String> bugs = new ArrayList<>();

        if(BugTracker.getInstance().isDBEnabled()){
            bugs.addAll(BugTracker.getDB().listBugsFromPlayer(name));

            return bugs;
        }

        for(String s : fc.getConfigurationSection("Reports").getKeys(false)){
            if(fc.getString("Reports." + s + ".Initializer").equalsIgnoreCase(name))
                bugs.add(s);
        }

        return bugs;
    }

    public boolean containsBugs(){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().containsBugs();

        return listBugs().size() > 0;
    }

    public boolean isIDValid(){
        if(BugTracker.getInstance().isDBEnabled())
            return BugTracker.getDB().isIDValid(this.id);

        return listBugs().contains(String.valueOf(this.id));
    }

    public Bugs build(){
        if(!(BugTracker.getInstance().isDBEnabled())){
            try {
                fc.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this;
    }
}