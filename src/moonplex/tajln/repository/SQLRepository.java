package moonplex.tajln.repository;

import moonplex.tajln.SQLProtect;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLRepository implements Listener {

    private String ADD_PLAYER = "INSERT INTO account (uuid) VALUES (?);";
    private String GETID = "SELECT * from account WHERE uuid = ?;";
    private String GETUUID = "SELECT * from account WHERE id = ?;";
    private String GETFRIEND = "SELECT * from friends where playerid = ?;";
    private String ADDFRIEND = "INSERT INTO friends (playerid, frienduuid) VALUES (?,?);";
    private String REMOVEFRIEND = "DELETE FROM friends WHERE playerid=? AND frienduuid=?;";
    private String ADDBLOCK = "INSERT INTO blocks (playerid, blocklocation, world) VALUES (?,?,?);";
    private String GETBLOCK = "SELECT * from blocks where blocklocation = ? AND world = ?;";
    private String DELBLOCK = "DELETE FROM blocks WHERE blocklocation = ? AND world = ?;";

    public SQLRepository()
    {

    }

    @EventHandler
    public void addPlayer(PlayerJoinEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        if (!playerexists(uuid)) {
            try {
                PreparedStatement p = Databaser.Instance.GetConnection(Databaser.Instance.DB).prepareStatement(ADD_PLAYER);
                p.setString(1, uuid.toString());
                p.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean playerexists(UUID uuid){
        return getPlayerID(uuid) != -1;
    }

    public Integer getPlayerID(UUID uuid){
        int id = -1;
        try
        {
            PreparedStatement p = Databaser.Instance.GetConnection(Databaser.Instance.DB).prepareStatement(GETID);
            p.setString(1, uuid.toString());
            p.executeQuery();

            ResultSet resultSet = p.getResultSet();

            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return id;
    }

    public String getPlayerUUID(int id){
        String uuid = "";
        try
        {
            PreparedStatement p = Databaser.Instance.GetConnection(Databaser.Instance.DB).prepareStatement(GETUUID);
            p.setInt(1, id);
            p.executeQuery();

            ResultSet resultSet = p.getResultSet();

            while (resultSet.next()) {
                uuid = resultSet.getString("uuid");
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return uuid;
    }

    public boolean isFriend(UUID uuid, UUID frienduuid){
        boolean friend = false;
        int id = getPlayerID(uuid);
        try
        {
            PreparedStatement p = Databaser.Instance.GetConnection(Databaser.Instance.DB).prepareStatement(GETFRIEND);
            p.setInt(1, id);
            p.executeQuery();

            ResultSet resultSet = p.getResultSet();

            while (resultSet.next()) {
                if (resultSet.getString("frienduuid").equals(frienduuid.toString()))
                    friend = true;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return friend;
    }

    public void addFriend(UUID useruuid, UUID frienduuid){
        int id = getPlayerID(useruuid);
        try {
            PreparedStatement p = Databaser.Instance.GetConnection(Databaser.Instance.DB).prepareStatement(ADDFRIEND);
            p.setInt(1, id);
            p.setString(2, frienduuid.toString());
            p.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void delFriend(UUID useruuid, UUID frienduuid){
        int id = getPlayerID(useruuid);
        try {
            PreparedStatement p = Databaser.Instance.GetConnection(Databaser.Instance.DB).prepareStatement(REMOVEFRIEND);
            p.setInt(1, id);
            p.setString(2, frienduuid.toString());
            p.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addBlock(UUID uuid, Location location, String world){
        try {
            int loc = Integer.parseInt(location.getBlockX() + String.valueOf(location.getBlockY()) + location.getBlockZ());
            int id = getPlayerID(uuid);
            PreparedStatement p = Databaser.Instance.GetConnection(Databaser.Instance.DB).prepareStatement(ADDBLOCK);
            p.setInt(1, id);
            p.setInt(2, loc);
            p.setString(3, world);
            p.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean Break(UUID uuid, Location location, String world) {
        int id = 0;
        int loc = Integer.parseInt(location.getBlockX() + String.valueOf(location.getBlockY()) + location.getBlockZ());
        try {
            PreparedStatement p = Databaser.Instance.GetConnection(Databaser.Instance.DB).prepareStatement(GETBLOCK);
            p.setInt(1, loc);
            p.setString(2, world);
            p.executeQuery();

            ResultSet resultSet = p.getResultSet();


            while (resultSet.next()) {
                id = resultSet.getInt("playerid");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (id == 0){
            return true;
        }


        String playeruuid = getPlayerUUID(id);
        if (playeruuid.equals(uuid.toString()) || isFriend(UUID.fromString(playeruuid), uuid) || SQLProtect.ignore.contains(uuid)){
            try {
                PreparedStatement p = Databaser.Instance.GetConnection(Databaser.Instance.DB).prepareStatement(DELBLOCK);
                p.setInt(1, loc);
                p.setString(2, world);
                p.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;

    }


}
