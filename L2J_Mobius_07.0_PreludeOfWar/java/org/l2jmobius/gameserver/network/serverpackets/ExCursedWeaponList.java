package org.l2jmobius.gameserver.network.serverpackets;

import java.util.Set;

import org.l2jmobius.commons.network.WritableBuffer;
import org.l2jmobius.gameserver.managers.CursedWeaponsManager;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author -Wooden-
 */
public class ExCursedWeaponList extends ServerPacket
{
	private final Set<Integer> _ids;
	
	public ExCursedWeaponList()
	{
		_ids = CursedWeaponsManager.getInstance().getCursedWeaponsIds();
	}
	
	public ExCursedWeaponList(Set<Integer> ids)
	{
		_ids = ids;
	}
	
	@Override
	public void writeImpl(GameClient client, WritableBuffer buffer)
	{
		ServerPackets.EX_CURSED_WEAPON_LIST.writeId(this, buffer);
		buffer.writeInt(_ids.size());
		_ids.forEach(buffer::writeInt);
	}
}