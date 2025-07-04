/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.adempiere.webui.component;

import java.util.Map;

import org.adempiere.webui.event.DrillEvent;
import org.adempiere.webui.event.TokenEvent;
import org.adempiere.webui.event.ZoomEvent;
import org.compiere.model.MQuery;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;

/**
 * Command class to handle user authentication token event
 * @author hengsin
 *
 */
public class GlobalCommandDispatcher  implements AuService {

	private final Component comp;

	public GlobalCommandDispatcher(Component comp) {
		this.comp = comp;
	}

	@Override
	public boolean service(AuRequest request, boolean everError) {
		String cmd = request.getCommand();

		switch (cmd) {
			case "onZoom":
				return new ZoomCommand(comp).service(request, everError);
			case "onDrillDown":
			case "onDrillAcross":
				return new DrillCommand(comp).service(request, everError);
			case TokenEvent.ON_USER_TOKEN:
				return new TokenCommand(comp).service(request, everError);
			default:
				return false;
		}
	}
}
