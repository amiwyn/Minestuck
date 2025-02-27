package com.mraof.minestuck.network.computer;

import com.mraof.minestuck.network.PlayToBothPacket;
import com.mraof.minestuck.player.IdentifierHandler;
import com.mraof.minestuck.skaianet.SburbConnection;
import com.mraof.minestuck.skaianet.SkaianetHandler;
import com.mraof.minestuck.skaianet.client.ReducedConnection;
import com.mraof.minestuck.skaianet.client.SkaiaClient;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkaianetInfoPacket implements PlayToBothPacket
{
	public int playerId;
	public boolean isClientResuming, isServerResuming;
	public Map<Integer, String> openServers;
	public List<SburbConnection> connectionsFrom;
	public List<ReducedConnection> connectionsTo;
	public List<List<RegistryKey<World>>> landChains;
	
	public static SkaianetInfoPacket landChains(List<List<RegistryKey<World>>> landChains)
	{
		SkaianetInfoPacket packet = new SkaianetInfoPacket();
		packet.landChains = landChains;
		
		return packet;
	}
	
	public static SkaianetInfoPacket update(int playerId, boolean isClientResuming, boolean isServerResuming, Map<Integer, String> openServers, List<SburbConnection> connections)
	{
		SkaianetInfoPacket packet = new SkaianetInfoPacket();
		packet.playerId = playerId;
		packet.isClientResuming = isClientResuming;
		packet.isServerResuming = isServerResuming;
		packet.openServers = openServers;
		packet.connectionsFrom = connections;
		
		return packet;
	}
	
	public static SkaianetInfoPacket request(int playerId)
	{
		SkaianetInfoPacket packet = new SkaianetInfoPacket();
		packet.playerId = playerId;
		
		return packet;
	}
	
	@Override
	public void encode(PacketBuffer buffer)
	{
		if(landChains != null) //Land chain data
		{
			buffer.writeBoolean(true);
			for(List<RegistryKey<World>> list : landChains)
			{
				buffer.writeInt(list.size());
				for(RegistryKey<World> land : list)
				{
					if(land == null)
						buffer.writeUtf("");
					else buffer.writeUtf(land.location().toString());
				}
			}
		} else
		{
			buffer.writeBoolean(false);
			buffer.writeInt(playerId);
			
			if(connectionsFrom != null)
			{
				
				buffer.writeBoolean(isClientResuming);
				buffer.writeBoolean(isServerResuming);
				
				buffer.writeInt(openServers.size());
				for(Map.Entry<Integer, String> entry : openServers.entrySet())
				{
					buffer.writeInt(entry.getKey());
					buffer.writeUtf(entry.getValue(), 16);
				}
				
				for(SburbConnection connection : connectionsFrom)
					connection.toBuffer(buffer);
			}
		}
	}
	
	public static SkaianetInfoPacket decode(PacketBuffer buffer)
	{
		SkaianetInfoPacket packet = new SkaianetInfoPacket();
		if(buffer.readBoolean())
		{
			packet.landChains = new ArrayList<>();
			while(buffer.readableBytes() > 0)
			{
				int size = buffer.readInt();
				List<RegistryKey<World>> list = new ArrayList<>();
				for(int k = 0; k < size; k++)
				{
					String landName = buffer.readUtf(32767);
					if(landName.isEmpty())
						list.add(null);
					else list.add(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(landName)));
				}
				packet.landChains.add(list);
			}
		} else
		{
			packet.playerId = buffer.readInt();
			
			if(buffer.readableBytes() > 0)
			{
				packet.isClientResuming = buffer.readBoolean();
				packet.isServerResuming = buffer.readBoolean();
				int size = buffer.readInt();
				packet.openServers = new HashMap<>();
				for(int i = 0; i < size; i++)
					packet.openServers.put(buffer.readInt(), buffer.readUtf(16));
				packet.connectionsTo = new ArrayList<>();
				while(buffer.readableBytes() > 0)
					packet.connectionsTo.add(ReducedConnection.read(buffer));
			}
		}
		
		return packet;
	}
	
	public void execute()
	{
		SkaiaClient.consumePacket(this);
	}
	
	public void execute(ServerPlayerEntity player)
	{
		SkaianetHandler.get(player.server).requestInfo(player, IdentifierHandler.getById(this.playerId));
	}
}