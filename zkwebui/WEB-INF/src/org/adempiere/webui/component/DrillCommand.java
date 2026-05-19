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
import org.compiere.model.MQuery;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

/**
 * 
 * @author hengsin
 *
 */
public class DrillCommand implements AuService {

	 private final Component comp;

	    public DrillCommand(Component comp) {
	        this.comp = comp;
	    }

	    @Override
	    public boolean service(AuRequest request, boolean everError) {
	        String command = request.getCommand();
	        if (!DrillEvent.ON_DRILL_DOWN.equals(command) && !DrillEvent.ON_DRILL_ACROSS.equals(command)) {
	            return false; // Not our command
	        }

	        Map<String, Object> data = request.getData();
	        if (data == null || !data.containsKey("columnName") || !data.containsKey("code")) {
	            throw new UiException("Illegal request data: " + data);
	        }

	        String columnName = String.valueOf(data.get("columnName"));
	        String code = String.valueOf(data.get("code"));
	        String tableName = MQuery.getZoomTableName(columnName);

	        MQuery query = new MQuery(tableName);
	        query.addRestriction(columnName, MQuery.EQUAL, code);

	        Events.postEvent(new DrillEvent(command, comp, query));
	        return true;
	    }
}
