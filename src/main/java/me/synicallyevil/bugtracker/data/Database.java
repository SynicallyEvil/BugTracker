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

import org.bukkit.entity.Player;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class Database {

    private static java.sql.Connection connection;

    private String table;

    private String host;
    private int port;
    private String username;
    private String password;
    private String database;

    public Database(String table, String host, int port, String username, String password, String database){
        this.table = table;

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public boolean isConnected(){
        try{
            return ((connection != null) && !(connection.isClosed()));
        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public void enableConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            return;
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
            createDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableConnection(){
        try {
            if((connection != null) && !(connection.isClosed()))
                connection.close();

            System.out.println("Connection disabled!");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void createDatabase(){
        String createTable = "CREATE TABLE IF NOT EXISTS " + this.table + "(report_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT, " +
                "report varchar(255), created varchar(255), " +
                "player varchar(64), " +
                "UUID varchar(64), " +
                "world varchar(64), " +
                "loc_x double, " +
                "loc_y double, " +
                "loc_z double);";
        try {
            PreparedStatement table = connection.prepareStatement(createTable, Statement.RETURN_GENERATED_KEYS);

            table.executeUpdate();
            System.out.println("Database connected!");

            table.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addReport(Player player, String report, String created){
        String sql = "INSERT INTO " + this.table + "(report, created, player, UUID, world, loc_x, loc_y, loc_z) VALUES(?,?,?,?,?,?,?,?);";

        try{
            PreparedStatement prepare = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepare.setString(1, report);
            prepare.setString(2, created);
            prepare.setString(3, player.getName());
            prepare.setString(4, player.getUniqueId().toString());
            prepare.setString(5, player.getWorld().getName());
            prepare.setDouble(6, player.getLocation().getX());
            prepare.setDouble(7, player.getLocation().getY());
            prepare.setDouble(8, player.getLocation().getZ());
            prepare.executeUpdate();

            prepare.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void removeReport(int id){
        String sql = "SELECT * FROM " + this.table + " WHERE report_id=?;";

        try{
            PreparedStatement prepare = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepare.setInt(1, id);

            ResultSet results = prepare.executeQuery();

            if(results.next())
                prepare.executeUpdate("DELETE FROM " + this.table + " WHERE report_id='" + id + "';");

            prepare.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean isIDValid(int id){
        return getValue(id);
    }

    public boolean containsBugs(){
        return getValue(-1);
    }

    public ArrayList<String> listBugs(){
        ArrayList<String> bugs = new ArrayList<>();

        String sql = "SELECT * FROM " + this.table + " WHERE 1;";

        try{
            PreparedStatement prepare = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ResultSet results = prepare.executeQuery();

            while(results.next())
                bugs.add(String.valueOf(results.getInt("report_id")));

            prepare.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return bugs;
    }

    public ArrayList<String> listBugsFromPlayer(String name){
        ArrayList<String> bugs = new ArrayList<>();

        String sql = "SELECT * FROM " + this.table + " WHERE player=?;";

        try{
            PreparedStatement prepare = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepare.setString(1, name);

            ResultSet results = prepare.executeQuery();

            while(results.next())
                bugs.add(String.valueOf(results.getInt("report_id")));

            prepare.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return bugs;
    }

    public BugUtils getItemsFromRow(int id){
        String sql = "SELECT * FROM " + this.table + " WHERE report_id=?;";

        try{
            PreparedStatement prepare = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepare.setInt(1, id);

            ResultSet results = prepare.executeQuery();

            if(results.next()){
                String playername = results.getString("player");
                UUID uuid = UUID.fromString(results.getString("UUID"));
                String report = results.getString("report");
                String created = results.getString("created");
                String world = results.getString("world");
                int x = results.getInt("loc_x");
                int y = results.getInt("loc_y");
                int z = results.getInt("loc_z");

                return new BugUtils(id, playername, uuid, report, created, world, x, y, z);
                /*bugs.add(results.getString("player"));
                bugs.add(results.getString("UUID"));
                bugs.add(results.getString("report"));
                bugs.add(results.getString("created"));
                bugs.add(results.getString("world") + " " + String.valueOf(results.getDouble("loc_x")) + " " + String.valueOf(results.getDouble("loc_y")) + " " + String.valueOf(results.getDouble("loc_z")));
            */}

            prepare.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public String getPlayer(int id){
        return getValue("player", id);
    }

    public String getUUID(int id){
        return getValue("UUID", id);
    }

    public String getReport(int id){
        return getValue("report", id);
    }

    public String getCreation(int id){
        return getValue("created", id);
    }

    public String getPosition(int id){
        String sql = "SELECT * FROM " + this.table + " WHERE report_id=?;";

        try{
            PreparedStatement prepare = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepare.setInt(1, id);

            ResultSet results = prepare.executeQuery();

            if(results.next()) {
                String world = results.getString("world");
                String x = String.valueOf(results.getString("loc_x"));
                String y = String.valueOf(results.getString("loc_y"));
                String z = String.valueOf(results.getString("loc_z"));

                return world + " " + x + " " + y + " " + z;
            }

            prepare.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return "Undefined";
    }

    private String getValue(String value, int id){
        String sql = "SELECT * FROM " + this.table + " WHERE report_id=?;";

        try{
            PreparedStatement prepare = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepare.setInt(1, id);

            ResultSet results = prepare.executeQuery();

            if(results.next())
                return results.getString(value);

            prepare.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return "Undefined";
    }

    private boolean getValue(int id){
        try{
            PreparedStatement prepare;

            if(id == -1){
                String sql = "SELECT * FROM " + this.table + " WHERE 1;";
                prepare = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            }else{
                String sql = "SELECT * FROM " + this.table + " WHERE report_id=?;";
                prepare = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                prepare.setInt(1, id);
            }

            ResultSet results = prepare.executeQuery();

            if(results.next())
                return true;

            prepare.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
}