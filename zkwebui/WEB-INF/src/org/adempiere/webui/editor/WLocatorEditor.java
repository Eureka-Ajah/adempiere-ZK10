/******************************************************************************
 * Product: Posterita Ajax UI 												  *
 * Copyright (C) 2007 Posterita Ltd.  All Rights Reserved.                    *
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
 * Posterita Ltd., 3, Draper Avenue, Quatre Bornes, Mauritius                 *
 * or via info@posterita.org or http://www.posterita.org/                     *
 *****************************************************************************/

package org.adempiere.webui.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.EditorBox;
import org.adempiere.webui.event.ContextMenuEvent;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.exceptions.ValueChangeEvent;
import org.adempiere.webui.window.WRecordInfo;
import org.adempiere.webui.window.WLocatorDialog;
import org.compiere.model.GridField;
import org.compiere.model.MLocator;
import org.compiere.model.MLocatorLookup;
import org.compiere.model.MQuery;
import org.compiere.model.MRole;
import org.compiere.model.MTable;
import org.compiere.model.MWarehouse;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * Locator Editor : Based on VLocator
 * 
 * @author  Niraj Sohun
 * @date    Jul 23, 2007
 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
 *		<li> FR [ 146 ] Remove unnecessary class, add support for info to specific column
 *		@see https://github.com/adempiere/adempiere/issues/146
 */

public class WLocatorEditor extends WEditor implements EventListener, PropertyChangeListener, ContextMenuListener, IZoomableEditor
{
	private static final String[] LISTENER_EVENTS = {Events.ON_CLICK};

	private MLocatorLookup m_mLocator;
	private Object m_value;
	private Object oldValue;
	private int m_WindowNo;

	private WEditorPopupMenu popupMenu;

	private static CLogger log = CLogger.getCLogger(WLocatorEditor.class);
	/**
	 *  IDE Constructor
	 */
	
	public WLocatorEditor()
	{
		this("M_Locator_ID", false, false, true, null, 0);
	}
	
	/**
	 *	Constructor
	 *
	 * 	@param columnName ColumnName
	 *	@param mandatory mandatory
	 *	@param isReadOnly read only
	 *	@param isUpdateable updateable
	 *	@param mLocator locator (lookup) model
	 * 	@param WindowNo window no
	 */
	

	public WLocatorEditor(String columnName, boolean mandatory, boolean isReadOnly,
			boolean isUpdateable, MLocatorLookup mLocator, int windowNo)
	{
		super(new EditorBox(), "Locator", "", mandatory, isReadOnly, isUpdateable);

		setColumnName(columnName);
		m_mLocator = mLocator;
		getComponent().setButtonImage("/images/Locator10.png");

		m_WindowNo = windowNo;	//Yvonne: move it b4 setDefault_Locator_ID()
		setDefault_Locator_ID(); // set default locator, teo_sarca [ 1661546 ]
		
	}
	
	
	/**
	 * @param gridField
	 */

	public WLocatorEditor(GridField gridField)
	{
		super(new EditorBox(), gridField);
		m_mLocator = (MLocatorLookup) gridField.getLookup();

		getComponent().setButtonImage("/images/Locator10.png");

		m_WindowNo = gridField.getWindowNo();
		setDefault_Locator_ID(); // set default locator, teo_sarca [ 1661546 ]

		if (gridField != null)
		{
			popupMenu = new WEditorPopupMenu(true, true, false);
			if (gridField.getGridTab() != null)
			{
				WRecordInfo.addMenu(popupMenu);
			}
			getComponent().setContext(popupMenu.getId());
		}
	}
	/**
	 * 	Set Value
	 *	@param value value
	 *	@param fire data binding
	 */
	
	public void setValue(Object value)
	{
		setValue(value, false);
	}

	private void setValue(Object value, boolean fire)
	{
		if (value != null)
		{
			m_mLocator.setOnly_Warehouse_ID(getOnly_Warehouse_ID());
			m_mLocator.setOnly_Product_ID(getOnly_Product_ID());

			if (!m_mLocator.isValid(value))
			{
				value = null;
				if (gridField != null)
					gridField.setValue(null, false);
			}
		}

		oldValue = m_value;
		m_value = value;
		getComponent().setText(m_mLocator.getDisplay(value));

		if (fire)
		{
			ValueChangeEvent val = new ValueChangeEvent(this, getColumnName(), oldValue, value);
			fireValueChange(val);
		}
	}
	
	/**
	 *	Return Editor value
	 *  @return value
	 */

	public Object getValue()
	{
		if (getM_Locator_ID() == 0)
			return null;

		return m_value;
	}

	@Override
	public EditorBox getComponent()
	{
		return (EditorBox) component;
	}

	@Override
	public boolean isReadWrite()
	{
		return getComponent().isEnabled();
	}

	@Override
	public void setReadWrite(boolean readWrite)
	{
		getComponent().setEnabled(readWrite);
	}
	
	/**
	 * 	Get M_Locator_ID
	 *	@return id
	 */
	public int getM_Locator_ID()
	{
		if (m_value != null && m_value instanceof Integer)
			return ((Integer) m_value).intValue();

		return 0;
	}//getM_Locator_ID()
	
	/**
	 *  Return Display Value
	 *  @return display value
	 */
	
	public String getDisplay()
	{
		return getComponent().getText();
	}

	public void onEvent(Event event) throws Exception
	{
		if (!Events.ON_CLICK.equalsIgnoreCase(event.getName()))
			return;

		int only_Warehouse_ID = getOnly_Warehouse_ID();
		int only_Product_ID = getOnly_Product_ID();

		log.config("Only Warehouse_ID=" + only_Warehouse_ID + ", Product_ID=" + only_Product_ID);

		if (event.getTarget() == getComponent() && actionText(only_Warehouse_ID, only_Product_ID))
			return;

		int M_Locator_ID = 0;
		if (m_value instanceof Integer)
			M_Locator_ID = ((Integer) m_value).intValue();

		m_mLocator.setOnly_Warehouse_ID(only_Warehouse_ID);
		m_mLocator.setOnly_Product_ID(only_Product_ID);

		final WLocatorDialog ld = new WLocatorDialog(
				Msg.translate(Env.getCtx(), getColumnName()),
				m_mLocator,
				M_Locator_ID,
				isMandatory(),
				only_Warehouse_ID,
				this.m_WindowNo);

		ld.setLocatorDialogListener(new WLocatorDialog.LocatorDialogListener()
		{
			@Override
			public void onClose(Integer locatorId, boolean isChanged, boolean isCancel)
			{
				log.warning("WLocatorEditor.onClose - locatorId=" + locatorId
						+ ", isChanged=" + isChanged
						+ ", isCancel=" + isCancel);

				m_mLocator.setOnly_Warehouse_ID(0);

				if (isCancel || !isChanged)
					return;

				setValue(locatorId, true);
			}
		});

		ld.setVisible(true);
		AEnv.showWindow(ld);
	}

	public WEditorPopupMenu getPopupMenu()
	{
		return popupMenu;
	}

	public void actionRefresh()
	{
		if (m_mLocator != null)
		{
			Object curValue = getValue();

			if (isReadWrite())
				m_mLocator.refresh();

			if (curValue != null)
				setValue(curValue);
		}
	}

	public void actionZoom()
	{
		int AD_Window_ID = MTable.get(Env.getCtx(), MLocator.Table_ID).getAD_Window_ID();
		if (AD_Window_ID <= 0)
			AD_Window_ID = 139;

		log.info("");

		MQuery zoomQuery = new MQuery();
		zoomQuery.addRestriction(MLocator.COLUMNNAME_M_Locator_ID, MQuery.EQUAL, getValue());
		zoomQuery.setRecordCount(1);

		AEnv.zoom(AD_Window_ID, zoomQuery);
	}

	public void onMenu(ContextMenuEvent evt)
	{
		if (WEditorPopupMenu.REQUERY_EVENT.equals(evt.getContextEvent()))
		{
			actionRefresh();
		}
		else if (WEditorPopupMenu.ZOOM_EVENT.equals(evt.getContextEvent()))
		{
			actionZoom();
		}
		else if (WEditorPopupMenu.CHANGE_LOG_EVENT.equals(evt.getContextEvent()))
		{
			WRecordInfo.start(gridField);
		}
	}

	private boolean actionText(int only_Warehouse_ID, int only_Product_ID)
	{
		String text = getComponent().getText();
		log.fine(text);

		if (text == null || text.length() == 0)
		{
			if (isMandatory())
				return false;

			setValue(null, true);
			return true;
		}

		if (text.endsWith("%"))
			text = text.toUpperCase();
		else
			text = text.toUpperCase() + "%";

		StringBuffer sql = new StringBuffer("SELECT M_Locator_ID FROM M_Locator ")
				.append(" WHERE IsActive='Y' AND UPPER(Value) LIKE ")
				.append(DB.TO_STRING(text));

		if (getOnly_Warehouse_ID() != 0)
			sql.append(" AND M_Warehouse_ID=?");

		if (getOnly_Product_ID() != 0)
			sql.append(" AND (IsDefault='Y' ")
			   .append("OR EXISTS (SELECT * FROM M_Product p ")
			   .append("WHERE p.M_Locator_ID=M_Locator.M_Locator_ID AND p.M_Product_ID=?)")
			   .append("OR EXISTS (SELECT * FROM M_Storage s ")
			   .append("WHERE s.M_Locator_ID=M_Locator.M_Locator_ID AND s.M_Product_ID=?))");

		String finalSql = MRole.getDefault(Env.getCtx(), false).addAccessSQL(
				sql.toString(), "M_Locator", MRole.SQL_NOTQUALIFIED, MRole.SQL_RO);

		int M_Locator_ID = 0;
		PreparedStatement pstmt = null;

		try
		{
			pstmt = DB.prepareStatement(finalSql, null);
			int index = 1;

			if (only_Warehouse_ID != 0)
				pstmt.setInt(index++, only_Warehouse_ID);

			if (only_Product_ID != 0)
			{
				pstmt.setInt(index++, only_Product_ID);
				pstmt.setInt(index++, only_Product_ID);
			}

			ResultSet rs = pstmt.executeQuery();

			if (rs.next())
			{
				M_Locator_ID = rs.getInt(1);
				if (rs.next())
					M_Locator_ID = 0;
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (SQLException ex)
		{
			log.log(Level.SEVERE, finalSql, ex);
		}
		finally
		{
			try
			{
				if (pstmt != null)
					pstmt.close();
			}
			catch (SQLException ex1)
			{
			}
		}

		if (M_Locator_ID == 0)
			return false;

		setValue(Integer.valueOf(M_Locator_ID), true);
		return true;
	}

	public void setField(org.compiere.model.GridField mField)
	{
	}

	private int getOnly_Warehouse_ID()
	{
		String only_Warehouse = Env.getContext(Env.getCtx(), m_WindowNo, "M_Warehouse_ID", true);
		int only_Warehouse_ID = 0;

		try
		{
			if (only_Warehouse != null && only_Warehouse.length() > 0)
				only_Warehouse_ID = Integer.parseInt(only_Warehouse);
		}
		catch (Exception ex)
		{
		}

		return only_Warehouse_ID;
	}

	private int getOnly_Product_ID()
	{
		if (!Env.isSOTrx(Env.getCtx(), m_WindowNo))
			return 0;

		String only_Product = Env.getContext(Env.getCtx(), m_WindowNo, "M_Product_ID", true);
		int only_Product_ID = 0;

		try
		{
			if (only_Product != null && only_Product.length() > 0)
				only_Product_ID = Integer.parseInt(only_Product);
		}
		catch (Exception ex)
		{
		}
		return only_Product_ID;
	}

	private void setDefault_Locator_ID()
	{
		if (!isMandatory() || m_mLocator == null)
			return;

		int M_Warehouse_ID = getOnly_Warehouse_ID();
		if (M_Warehouse_ID <= 0)
			return;

		MWarehouse wh = MWarehouse.get(Env.getCtx(), M_Warehouse_ID);
		if (wh == null || wh.get_ID() <= 0)
			return;

		MLocator loc = wh.getDefaultLocator();
		if (loc == null || loc.get_ID() <= 0)
			return;

		setValue(Integer.valueOf(loc.get_ID()));
	}
	
	 /**
     *  Property Change Listener
     *  @param evt PropertyChangeEvent
     */
	
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals(org.compiere.model.GridField.PROPERTY))
			setValue(evt.getNewValue());
	}

	public String[] getEvents()
	{
		return LISTENER_EVENTS;
	}
}
