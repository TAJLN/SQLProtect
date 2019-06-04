package moonplex.tajln.commands;

import moonplex.tajln.SQLProtect;
import moonplex.tajln.repository.SQLRepository;
import moonplex.tajln.utils.CommandInfo;
import moonplex.tajln.utils.CommandManager;
import moonplex.tajln.utils.SimpleCommand;
import moonplex.tajln.utils.UUIDFetcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandInfo(description="SQLProtect command", usage=" /<command>", permission="sqlprotect.command", onlyIngame = true)
public class SQLProtectcommand extends SimpleCommand {

    SQLRepository sqlRepository;

    public SQLProtectcommand(CommandManager commandManager, SQLRepository sqlRepository) {
        super(commandManager, "sqlprotect");
        this.sqlRepository = sqlRepository;
    }

    @Override
    public boolean onCommand(CommandSender sender, String command, String[] args) {
        if (args.length == 0){
            sender.sendMessage(getPlugin().getConfig().getString("messages.cmdmsg1"));
            sender.sendMessage(getPlugin().getConfig().getString("messages.cmdmsg2"));
            sender.sendMessage(getPlugin().getConfig().getString("messages.cmdmsg3"));
            return true;
        }

        Player p = (Player) sender;

        switch (args[0]){
            case "friend":
                friend(p, args);
                return true;
            case "unfriend":
                unfriend(p, args);
                return true;
            case "ignore":
                ignore(p);
                return true;
        }
        return false;
    }

    void friend(Player p, String[] args){
        if (args.length<2){
            p.sendMessage(getPlugin().getConfig().getString("messages.cmdmsg1"));
            return;
        }

        UUID frienduuid = UUIDFetcher.getUUID(args[1]);

        if (!sqlRepository.playerexists(frienduuid)){
            p.sendMessage(getPlugin().getConfig().getString("messages.nojoin"));
            return;
        }

        if (sqlRepository.isFriend(p.getUniqueId(), frienduuid)){
            p.sendMessage(getPlugin().getConfig().getString("messages.isfriend"));
            return;
        }

        sqlRepository.addFriend(p.getUniqueId(), frienduuid);
        p.sendMessage(getPlugin().getConfig().getString("messages.addfriend"));
    }

    void unfriend(Player p, String[] args){
        if (args.length<2){
            p.sendMessage(getPlugin().getConfig().getString("messages.cmdmsg2"));
            return;
        }

        UUID frienduuid = UUIDFetcher.getUUID(args[1]);

        if (!sqlRepository.isFriend(p.getUniqueId(), frienduuid)){
            p.sendMessage(getPlugin().getConfig().getString("messages.isntfriend"));
            return;
        }

        sqlRepository.delFriend(p.getUniqueId(), frienduuid);
        p.sendMessage(getPlugin().getConfig().getString("messages.delfriend"));
    }

    void ignore(Player p){
        if (!p.hasPermission("sqlprotect.admin")){
            p.sendMessage(getPlugin().getConfig().getString("messages.noperms"));
            return;
        }

        if (SQLProtect.ignore.contains(p.getUniqueId())){
            SQLProtect.ignore.remove(p.getUniqueId());
            p.sendMessage(getPlugin().getConfig().getString("messages.noignore"));
        } else {
            SQLProtect.ignore.add(p.getUniqueId());
            p.sendMessage(getPlugin().getConfig().getString("messages.ignore"));
        }
    }
}
