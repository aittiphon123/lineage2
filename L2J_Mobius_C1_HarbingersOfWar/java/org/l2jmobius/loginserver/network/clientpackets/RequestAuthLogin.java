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
package org.l2jmobius.loginserver.network.clientpackets;

import java.util.List;

import org.l2jmobius.loginserver.GameServerTable;
import org.l2jmobius.loginserver.GameServerThread;
import org.l2jmobius.loginserver.LoginController;
import org.l2jmobius.loginserver.LoginServer;
import org.l2jmobius.loginserver.config.LoginConfig;
import org.l2jmobius.loginserver.network.AbstractClientPacket;
import org.l2jmobius.loginserver.network.LoginClient;
import org.l2jmobius.loginserver.network.LoginClient.LoginClientState;
import org.l2jmobius.loginserver.network.serverpackets.AccountKicked;
import org.l2jmobius.loginserver.network.serverpackets.LoginFail;
import org.l2jmobius.loginserver.network.serverpackets.LoginOk;

/**
 * Format: x 0 (a leading null) x: the rsa encrypted block with the login an password.
 */
public class RequestAuthLogin extends AbstractClientPacket
{
	private String _account;
	private String _password;
	
	public RequestAuthLogin(byte[] rawPacket, LoginClient client)
	{
		super(rawPacket, client);
	}
	
	@Override
	public void run()
	{
		final byte[] buffer = getByteBuffer();
		
		_account = new String(buffer, 1, 14).trim();
		_account = _account.toLowerCase();
		_password = new String(buffer, 15, 14).trim();
		
		final LoginController login = LoginController.getInstance();
		
		// IP banned due to entering wrong password many times.
		if (login.isBannedAddress(getClient().getSocket().getInetAddress().getHostAddress()))
		{
			getClient().sendPacket(new AccountKicked(AccountKicked.REASON_ILLEGAL_USE));
			return;
		}
		
		if (!login.isLoginValid(_account, _password, getClient()))
		{
			getClient().sendPacket(new LoginFail(LoginFail.REASON_USER_OR_PASS_WRONG));
			return;
		}
		
		// Account BANNED (must always be checked after isLoginValid)
		if (getClient().getAccessLevel() < 0)
		{
			getClient().sendPacket(new AccountKicked(AccountKicked.REASON_ILLEGAL_USE));
			return;
		}
		
		getClient().setAccount(_account);
		
		final LoginClient connected = login.getConnectedClient(_account);
		if (connected != null)
		{
			connected.sendPacket(new LoginFail(LoginFail.REASON_ACCOUNT_IN_USE));
			getClient().sendPacket(new LoginFail(LoginFail.REASON_ACCOUNT_IN_USE));
			return;
		}
		
		final List<GameServerThread> gslist = LoginServer.getGameServerListener().getGameServerThreads();
		synchronized (gslist)
		{
			for (GameServerThread gameServer : gslist)
			{
				if (gameServer.getPlayersInGame().contains(_account))
				{
					gameServer.kickPlayer(_account);
					getClient().sendPacket(new LoginFail(LoginFail.REASON_ACCOUNT_IN_USE));
					return;
				}
			}
		}
		
		getClient().setState(LoginClientState.AUTHED_LOGIN);
		login.assignKeyToLogin(getClient());
		
		if (LoginConfig.SHOW_LICENCE)
		{
			getClient().sendPacket(new LoginOk(getClient().getSessionKey()));
		}
		else
		{
			GameServerTable.getInstance().createServerList(getClient());
		}
	}
}
