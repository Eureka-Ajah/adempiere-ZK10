/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.adempiere.webui.panel;

import java.util.Date;
import java.util.logging.Level;

import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.window.InfoSchedule;
import org.compiere.model.MResourceAssignment;
import org.compiere.util.CLogger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * Visual and Control Part of Schedule.
 *
 * Implementacion de contingencia pendiente de reemplazo compatible con ZK 10.2.1.
 *
 * La implementacion original dependia de org.zkforge.timeline:
 * - org.zkforge.timeline.Timeline
 * - org.zkforge.timeline.Bandinfo
 * - org.zkforge.timeline.event.BandScrollEvent
 *
 * Esa libreria no esta disponible en el classpath actual de Eureka durante
 * la migracion hacia ZK 10.2.1.
 *
 * Esta clase permite compilar y mantener operativo el flujo general.
 * La vista grafica de agenda queda pendiente de reimplementacion.
 */
public class WSchedule extends Panel implements EventListener
{
    private static final long serialVersionUID = 7714179510197450419L;

    private static final CLogger log = CLogger.getCLogger(WSchedule.class);

    private InfoSchedule infoSchedule;

    private MResourceAssignment assignmentDialogResult;

    /**
     * Constructor.
     *
     * @param is InfoSchedule for callback
     */
    public WSchedule(InfoSchedule is)
    {
        infoSchedule = is;

        try
        {
            init();
        }
        catch (Exception e)
        {
            log.log(Level.SEVERE, "WSchedule", e);
        }
    }

    private void init() throws Exception
    {
        this.getChildren().clear();

        Label label = new Label("Agenda no disponible. Requiere reimplementacion compatible con ZK 10.2.1.");
        label.setStyle("display:block; padding:10px; color:#666;");

        this.appendChild(label);
    }

    /**
     * Recreate View.
     *
     * @param S_Resource_ID Resource
     * @param date Date
     */
    public void recreate(int S_Resource_ID, Date date)
    {
        if (infoSchedule != null && date != null)
        {
            infoSchedule.dateCallback(date);
        }
    }

    public void onAssignmentCallback()
    {
        if (assignmentDialogResult != null && infoSchedule != null)
        {
            infoSchedule.mAssignmentCallback(assignmentDialogResult);
        }

        assignmentDialogResult = null;
    }

    @Override
    public void onEvent(Event event) throws Exception
    {
        // Timeline deshabilitado hasta reimplementarlo sobre ZK 10.2.1.
    }
}	//	WSchedule
