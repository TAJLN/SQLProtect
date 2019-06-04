package moonplex.tajln;


import moonplex.tajln.commands.SQLProtectcommand;
import moonplex.tajln.repository.Databaser;
import moonplex.tajln.repository.SQLRepository;
import moonplex.tajln.utils.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLProtect extends JavaPlugin implements Listener
{
    private static SQLProtect instance;

    private static SQLRepository _repository;

    public static ArrayList<UUID> ignore = new ArrayList<>();

    public void onEnable(){

        FileConfiguration config = this.getConfig();
        config.addDefault("connection.username", "root");
        config.addDefault("connection.password", "whateverpasswordyouwant");
        config.addDefault("connection.connectionurl", "localhost:3306");
        config.addDefault("connection.database", "sqlprotect");

        config.addDefault("messages.cmdmsg1", "SQLProtect> /sqlprotect friend <playername>");
        config.addDefault("messages.cmdmsg2", "SQLProtect> /sqlprotect unfriend <playername>");
        config.addDefault("messages.cmdmsg3", "SQLProtect> /sqlprotect ignore");
        config.addDefault("messages.nojoin", "SQLProtect> That player never joined this server");
        config.addDefault("messages.isfriend", "SQLProtect> This player is already your friend");
        config.addDefault("messages.isntfriend", "SQLProtect> That player isn't your friend");
        config.addDefault("messages.addfriend", "SQLProtect> That player has been added to your friendlist. They can now break your blocks");
        config.addDefault("messages.delfriend", "SQLProtect> That player has been removed from your friendlist. They can no longer break your blocks");
        config.addDefault("messages.noperms", "SQLProtect> You are not allowed to use this");
        config.addDefault("messages.ignore", "SQLProtect> You are now ignoring SQLProtect");
        config.addDefault("messages.noignore", "SQLProtect> You are no longer ignoring SQLProtect");
        config.options().copyDefaults(true);
        saveConfig();
        this.getConfig();

        new Databaser(this);

        _repository = new SQLRepository();



        Bukkit.getConsoleSender().sendMessage("\n  " +
                "  /$$$$$$   /$$$$$$  /$$       /$$$$$$$                       /$$                           /$$          \n" +
                " /$$__  $$ /$$__  $$| $$      | $$__  $$                     | $$                          | $$          \n" +
                "| $$  \\__/| $$  \\ $$| $$      | $$  \\ $$ /$$$$$$   /$$$$$$  /$$$$$$    /$$$$$$   /$$$$$$$ /$$$$$$        \n" +
                "|  $$$$$$ | $$  | $$| $$      | $$$$$$$//$$__  $$ /$$__  $$|_  $$_/   /$$__  $$ /$$_____/|_  $$_/        \n" +
                " \\____  $$| $$  | $$| $$      | $$____/| $$  \\__/| $$  \\ $$  | $$    | $$$$$$$$| $$        | $$          \n" +
                " /$$  \\ $$| $$/$$ $$| $$      | $$     | $$      | $$  | $$  | $$ /$$| $$_____/| $$        | $$ /$$      \n" +
                "|  $$$$$$/|  $$$$$$/| $$$$$$$$| $$     | $$      |  $$$$$$/  |  $$$$/|  $$$$$$$|  $$$$$$$  |  $$$$/      \n" +
                " \\______/  \\____ $$$|________/|__/     |__/       \\______/    \\___/   \\_______/ \\_______/   \\___/        \n" +
                "                \\__/                                                                                     \n" +
                "                                                                                                         \n" +
                "/$$$$$$$  /$$     /$$       /$$$$$$$$ /$$$$$$     /$$$$$ /$$       /$$   /$$\n" +
                "| $$__  $$|  $$   /$$/      |__  $$__//$$__  $$   |__  $$| $$      | $$$ | $$\n" +
                "| $$  \\ $$ \\  $$ /$$/          | $$  | $$  \\ $$      | $$| $$      | $$$$| $$\n" +
                "| $$$$$$$   \\  $$$$/           | $$  | $$$$$$$$      | $$| $$      | $$ $$ $$\n" +
                "| $$__  $$   \\  $$/            | $$  | $$__  $$ /$$  | $$| $$      | $$  $$$$\n" +
                "| $$  \\ $$    | $$             | $$  | $$  | $$| $$  | $$| $$      | $$\\  $$$\n" +
                "| $$$$$$$/    | $$             | $$  | $$  | $$|  $$$$$$/| $$$$$$$$| $$ \\  $$\n" +
                "|_______/     |__/             |__/  |__/  |__/ \\______/ |________/|__/  \\__/\n" +
                "                                                                             \n" +
                "                                                                             \n" +
                "                                                                             ");

        instance = this;


        CommandManager cm = new CommandManager(this);
        cm.registerCommand(new SQLProtectcommand(cm, _repository));

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(_repository, this);

    }

    public static SQLProtect getPlugin(){
        return instance;
    }

    @EventHandler
    public void blockplace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        String world = p.getWorld().getName();

        if (e.getBlock().getType() == Material.SAND || e.getBlock().getType() == Material.GRAVEL){
            return;
        }

        _repository.addBlock(p.getUniqueId(), e.getBlock().getLocation(), world);
    }

    @EventHandler
    public void blockbreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        String world = p.getWorld().getName();
        Location location = e.getBlock().getLocation();

        if (!_repository.Break(p.getUniqueId(), location, world)){
            e.setCancelled(true);
            p.sendMessage("This block wasn't placed by you and is protected with SQLProtect");
        }

    }
}

