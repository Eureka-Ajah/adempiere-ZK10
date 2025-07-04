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

import org.adempiere.webui.event.ZoomEvent;
import org.compiere.model.MQuery;
import org.zkoss.lang.Objects;
import org.zkoss.zk.au.AuService;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

/**
 * 
 * @author hengsin
 *
 */
public class ZoomCommand implements AuService {

	private final Component comp;

    public ZoomCommand(Component comp) {
        this.comp = comp;
    }

    @Override
    public boolean service(AuRequest request, boolean everError) {
        if (!"onZoom".equals(request.getCommand())) {
            return false;
        }

        Map<String, Object> data = request.getData();
        if (data == null || !data.containsKey("columnName") || !data.containsKey("code")) {
            throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA, new Object[]{
                Objects.toString(data), this
            });
        }

        String columnName = (String) data.get("columnName");
        String tableName = MQuery.getZoomTableName(columnName);
        Object code;

        try {
            code = (columnName.endsWith("_ID")) ? Integer.parseInt(data.get("code").toString()) : data.get("code");
        } catch (Exception e) {
            code = data.get("code");
        }

        MQuery query = new MQuery(tableName);
        query.addRestriction(columnName, MQuery.EQUAL, code);
        query.setRecordCount(1);

        Events.postEvent(new ZoomEvent(comp, query));
        return true;
    }

}
