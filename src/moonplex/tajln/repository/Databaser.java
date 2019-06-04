package moonplex.tajln.repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import moonplex.tajln.MiniPlugin;
import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.plugin.java.JavaPlugin;

public class Databaser extends MiniPlugin
{
	private HashMap<DataSource, Connection> connections = new HashMap<>();

	public static Databaser Instance;

	public DataSource DB = null;

	public Databaser(JavaPlugin plugin)
	{
		super("Database Manager", plugin);

		Instance = this;

		String user = plugin.getConfig().getString("connection.username");
		String pass = plugin.getConfig().getString("connection.password");

		String creds = plugin.getConfig().getString("connection.connectionurl");
		String database = plugin.getConfig().getString("connection.database");
		String[] parts = creds.split(":");

		DB = openDataSource("jdbc:mysql://" + parts[0] + "/" + database, user, pass);

		try {
            connect(DB);
        } catch (SQLException e){
		    System.out.println("Error connecting to the database, gave up after 3 times");
		    System.out.println("SERVER SHUTTING DOWN");
		    plugin.getServer().shutdown();
        }
	}

	public static DataSource openDataSource(String url, String username, String password)
	{
		BasicDataSource source = new BasicDataSource();
		source.addConnectionProperty("autoReconnect", "true");
		source.addConnectionProperty("allowMultiQueries", "true");
		source.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		source.setDriverClassName("com.mysql.jdbc.Driver");
		source.setUrl(url);
		source.setUsername(username);
		source.setPassword(password);
		source.setMaxTotal(4);
		source.setMaxIdle(4);
		source.setTimeBetweenEvictionRunsMillis(180 * 1000);
		source.setSoftMinEvictableIdleTimeMillis(180 * 1000);

		return source;
	}

	@Override
	public void disable() {

		disconnect(DB);
		super.disable();

	}

	public void connect(DataSource source) throws SQLException{
		if (!isConnected(source)) {
		    connections.put(source, source.getConnection());
		}
	}

	public void disconnect(DataSource source) {
		if (isConnected(source)) {
			try {
				source.getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isConnected(DataSource source)
	{
		return (connections.get(source) != null);
	}

	public Connection GetConnection(DataSource source)
	{
		return connections.get((source));
	}
}
