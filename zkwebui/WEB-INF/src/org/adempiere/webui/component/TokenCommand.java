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

import org.adempiere.webui.event.TokenEvent;
import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

/**
 * Command class to handle user authentication token event
 * @author hengsin
 *
 */
public class TokenCommand implements AuService {
	private final Component comp;
	public TokenCommand(Component comp) {
        this.comp = comp;
    }

	@Override
    public boolean service(AuRequest request, boolean everError) {
        if (!"onToken".equals(request.getCommand())) {
            return false; 
        }

        Map<String, Object> dataMap = request.getData();
        if (dataMap == null || !dataMap.containsKey("arg0") || !dataMap.containsKey("arg1")) {
            throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA, new Object[] {
                Objects.toString(dataMap), this
            });
        }

        // Extraer los datos como arreglo
        String[] data = new String[] {
            (String) dataMap.get("arg0"),
            (String) dataMap.get("arg1")
        };

        Events.postEvent(new TokenEvent("onToken", comp, data));
        return true;
    }
}
