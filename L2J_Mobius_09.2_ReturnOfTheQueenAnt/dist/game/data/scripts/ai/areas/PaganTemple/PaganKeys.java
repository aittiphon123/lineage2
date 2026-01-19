/*
 * Copyright (c) 2013 L2jMobius
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ai.areas.PaganTemple;

import org.l2jmobius.gameserver.config.PlayerConfig;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.script.Script;

/**
 * Pagan Temple teleport AI
 * @version Prelude of War Part 2 - 27 September 2019
 * @author Notorion
 */
public class PaganKeys extends Script
{
	// Item
	private static final int KEY_OF_PAGANS = 36247;
	
	// NPC
	private static final int RESURRECTED_WORKER = 22140;
	
	// Chance
	private static final int DROP_CHANCE = 55;
	
	private PaganKeys()
	{
		addKillId(RESURRECTED_WORKER);
	}
	
	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (getRandom(100) < DROP_CHANCE)
		{
			if (PlayerConfig.AUTO_LOOT)
			{
				giveItems(killer, KEY_OF_PAGANS, 1);
			}
			else
			{
				npc.dropItem(killer, KEY_OF_PAGANS, 1);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new PaganKeys();
	}
}
