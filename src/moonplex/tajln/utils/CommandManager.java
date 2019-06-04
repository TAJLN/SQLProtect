package moonplex.tajln.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager{
	private Plugin plugin;
	private static SimpleCommandMap cmap;
	private static List<CommandManager> list = new ArrayList<CommandManager>();
	private List<SimpleCommand> commandList = new ArrayList<SimpleCommand>();

	 public String NOPERMS = "You are not allowed to do that";
	 String ONLYINGAME_MESSAGE = "This is not a console command";

	public CommandManager(Plugin plugin){
		
		this.plugin = plugin;	
		list.add(this);

		try
		{
			Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			f.setAccessible(true);
      
			cmap = (SimpleCommandMap)f.get(Bukkit.getServer());

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void registerCommand(SimpleCommand command)
	{
		cmap.register(plugin.getName(), command);
		this.commandList.add(command);
	}

	private void registerCommands(List<SimpleCommand> commands)
	{
		for (SimpleCommand command : commands)
    {
		registerCommand(command);
    }
  }

	public void registerCommands(SimpleCommand[] commands)
	{
		registerCommands(Arrays.asList(commands));
	}

	public List<SimpleCommand> getCommands()
	{
		return this.commandList;
	}
  
	public Plugin getPlugin()
	{
		return this.plugin;
	}
  
	public static List<CommandManager> getCommandManagers()
	{
		return list;
	}
}