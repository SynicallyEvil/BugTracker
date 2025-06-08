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

package me.synicallyevil.bugtracker.commands;

import me.synicallyevil.bugtracker.BugTracker;
import me.synicallyevil.bugtracker.apievents.BugSubmittedEvent;
import me.synicallyevil.bugtracker.data.Bugs;
import me.synicallyevil.bugtracker.data.BugUtils;
import me.synicallyevil.bugtracker.utils.Pagination;
import me.synicallyevil.bugtracker.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(Utils.getColor("&c[BugTracker] You must be a player to use this command."));
            return true;
        }

        Player player = (Player)sender;

        if(args.length > 0){
            switch(args[0]){
                case "create":
                case "add":
                    if(!(player.hasPermission("bugtracker.use"))){
                        player.sendMessage(noPerms());
                        break;
                    }

                    if(args.length > 4)
                        addBug(player, args);
                    else {
                        int words = BugTracker.getInstance().getInt("global.minimum_words_to_submit", 4);

                        sender.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.needs_more_words", "&4&lError &cPlease specify the issue with at least %count% words.")
                                                                                    .replace("%count%", String.valueOf(words))));
                    }
                    break;

                case "remove":
                case "delete":
                    if(!(player.hasPermission("bugtracker.modify"))){
                        player.sendMessage(noPerms());
                        break;
                    }

                    if(args.length > 1){
                        if(Utils.isNumber(args[1])){
                            removeBug(player, Integer.parseInt(args[1]));
                        }else{
                            player.sendMessage(notANumber(args[1]));
                        }
                    }
                    break;

                case "list":
                    if(!(player.hasPermission("bugtracker.show"))){
                        player.sendMessage(noPerms());
                        break;
                    }

                    listBugs(player, args);
                    break;

                case "player":
                    if(!(player.hasPermission("bugtracker.show"))){
                        player.sendMessage(noPerms());
                        break;
                    }

                    listBugsForPlayer(player, args);
                    break;

                case "help":
                case "?":
                    help(player);
                    break;


                case "show":
                case "info":
                case "detail":
                    if(!(player.hasPermission("bugtracker.show"))){
                        player.sendMessage(noPerms());
                        break;
                    }

                    if(args.length > 1){
                        if(Utils.isNumber(args[1])){
                            showBug(player, Integer.parseInt(args[1]));
                        }else{
                            player.sendMessage(notANumber(args[1]));
                        }
                    }
                    break;

                default:
                    if(!(player.hasPermission("bugtracker.use"))){
                        player.sendMessage(noPerms());
                        break;
                    }

                    help(player);
                    break;
            }
        }else{
            help(player);
        }

        return false;
    }

    private void help(Player player){
        player.sendMessage(Utils.getColor("&7[&c/bug add|create <message>&7] &fAdds a bug report."));
        player.sendMessage(Utils.getColor("&7[&c/bug help|?&7] &fShows the help messages. (This)"));

        if(player.hasPermission("bugtracker.modify")){
            player.sendMessage(Utils.getColor("&7[&c/bug remove|delete <bug_id>&7] &fRemoves a bug report."));
        }

        if(player.hasPermission("bugtracker.show")){
            player.sendMessage(Utils.getColor("&7[&c/bug list&7] &fLists bug reports."));
            player.sendMessage(Utils.getColor("&7[&c/bug player <playername>&7] &fTo view all of a players reports. (Same as /bug list)"));
            player.sendMessage(Utils.getColor("&7[&c/bug show|info|detail <bug_id>&7] &fShows the info of a reported bug."));
        }
    }

    private void addBug(Player player, String[] args){
        if(BugTracker.getInstance().cooldown.containsKey(player.getUniqueId())){
            long timer = (BugTracker.getInstance().cooldown.get(player.getUniqueId())/1000 + BugTracker.getInstance().getInt("global.cooldown_in_seconds", 5)) - System.currentTimeMillis()/1000;

            player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.errors.cooldown_activated", "&4Please way %seconds% seconds before submitting another report!")
                                                                        .replace("%seconds%", String.valueOf(timer))));
            return;
        }

        if(BugTracker.getInstance().isCooldownEnabled(player)){
            BugTracker.getInstance().addToCooldown(player.getUniqueId());

            new BukkitRunnable(){

                @Override
                public void run() {
                    BugTracker.getInstance().removeFromCooldown(player.getUniqueId());
                }

            }.runTaskLater(BugTracker.getInstance(), (BugTracker.getInstance().getInt("global.cooldown_in_seconds", 5)*20));
        }

        StringBuilder reason = new StringBuilder();

        for(int i = 1; i < args.length; i++)
            reason.append(args[i]).append(" ");

        BugTracker.getInstance().getServer().getPluginManager().callEvent(new BugSubmittedEvent(player, reason.toString()));
    }

    private void removeBug(Player player, int id){
        new Bugs(id).removeReport().build();

        player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.report_deleted", "&aReport &l%id% &ahas been deleted!")
                                                                    .replace("%id%", String.valueOf(id))));
    }

    private void listBugsForPlayer(Player player, String[] args){
        if(!(args.length > 1)){
            player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.specifiy_a_player", "&4Please specify a player.")));
            return;
        }

        Bugs b = new Bugs();
        int page = 1;

        if(!(b.reportsFromPlayer(args[1]).size() > 0)){
            player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.no_reports_for_player", "&aNo reports from &l%player%&a!")
                                                                        .replace("%player%", args[1])));
            return;
        }

        if(args.length > 2){
            if(Utils.isNumber(args[2]))
                page = (Integer.parseInt(args[2]) > 0 ? Integer.parseInt(args[2]) : 1);
            else{
                player.sendMessage(notANumber(args[2]));
                return;
            }
        }

        Pagination p = new Pagination(b.reportsFromPlayer(args[1]));

        totalReports(player, b.reportsFromPlayer(args[1]));

        boolean isDBEnabled = BugTracker.getInstance().isDBEnabled();

        for(String s : p.getListFromPage(page)){
            BugUtils db = null;

            if(isDBEnabled)
                db = BugTracker.getDB().getItemsFromRow(Integer.parseInt(s));

            player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.list", "&7[&c%id%&7] &f%report% &7- &c%creator%")
                    .replace("%id%", s)
                    .replace("%report%", isDBEnabled ? db.getReport() : b.getReport(Integer.parseInt(s)))
                    .replace("%creator%", isDBEnabled ? db.getPlayername() : b.getInitializer(Integer.parseInt(s)))));

        }



        pages(player, page, p.getTotalPages());
    }

    private void listBugs(Player player, String[] args){
        Bugs b = new Bugs();
        int page = 1;

        if(!(b.containsBugs())){
            player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.no_reported_bugs", "&aNo bugs reported!")));
            return;
        }

        Pagination p = new Pagination(b.listBugs());

        if(args.length > 1){
            if(Utils.isNumber(args[1]))
                page = (Integer.parseInt(args[1]) > 0 ? Integer.parseInt(args[1]) : 1);
            else{
                player.sendMessage(notANumber(args[1]));
                return;
            }
        }

        totalReports(player, b.listBugs());


        boolean isDBEnabled = BugTracker.getInstance().isDBEnabled();

        for(String s : p.getListFromPage(page)){
            BugUtils db = null;

            if(isDBEnabled)
                db = BugTracker.getDB().getItemsFromRow(Integer.parseInt(s));

            player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.list", "&7[&c%id%&7] &f%report% &7- &c%creator%")
                    .replace("%id%", s)
                    .replace("%report%", isDBEnabled ? db.getReport() : b.getReport(Integer.parseInt(s)))
                    .replace("%creator%", isDBEnabled ? db.getPlayername() : b.getInitializer(Integer.parseInt(s)))));
        }

        pages(player, page, p.getTotalPages());
    }

    private void showBug(Player player, int id){
        Bugs b = new Bugs(id);

        if(!(b.isIDValid())){
            player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.report_does_not_exist", "&cReport &l%id% &cdoes not exist!")
                                                                        .replace("%id%", String.valueOf(id))));
            return;
        }

        if(BugTracker.getInstance().isDBEnabled()){
            BugUtils db = BugTracker.getDB().getItemsFromRow(id);

            for(String reportinfo : BugTracker.getInstance().getReportInfo()){
                player.sendMessage(Utils.getColor(reportinfo.replace("%id%", String.valueOf(id))
                        .replace("%player%", db.getPlayername())
                        .replace("%uuid%", db.getUUID().toString())
                        .replace("%report%", db.getReport())
                        .replace("%created%", db.getCreated())
                        .replace("%world%", db.getWorld())
                        .replace("%x%", String.valueOf(db.getX()))
                        .replace("%y%", String.valueOf(db.getY()))
                        .replace("%z%", String.valueOf(db.getZ()))));
            }
            return;
        }

        String[] pos = b.getPosition().split(" ");

        for(String reportinfo : BugTracker.getInstance().getReportInfo()){
            player.sendMessage(Utils.getColor(reportinfo.replace("%id%", String.valueOf(id))
                                                        .replace("%player%", b.getInitializer())
                                                        .replace("%uuid%", b.getUUID())
                                                        .replace("%report%", b.getReport())
                                                        .replace("%created%", b.getCreation())
                                                        .replace("%world%", pos[0])
                                                        .replace("%x%", pos[1])
                                                        .replace("%y%", pos[2])
                                                        .replace("%z%", pos[3])));

        }
    }

    private String noPerms(){
        return Utils.getColor(BugTracker.getInstance().getString("messages.errors.no_perms", "&cYou do not have permission to execute this command."));
    }

    private String notANumber(String s){
        return Utils.getColor(BugTracker.getInstance().getString("messages.errors.no_a_number", "&4&lError &cThat is not a number.").replace("%s%", s));
    }

    private void totalReports(Player player, ArrayList<String> r){
        player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.total_reports", "&7Total reports: &c%reports%")
                .replace("%reports%", String.valueOf(r.size()))));
    }

    private void pages(Player player, int page, int totalPages){
        player.sendMessage(Utils.getColor(BugTracker.getInstance().getString("messages.bugs.pages", "&7Page &c%page%&7/&c%total%")
                .replace("%page%", (page > totalPages ? String.valueOf(totalPages) : String.valueOf(page)))
                .replace("%total%", String.valueOf(totalPages))));
    }
}