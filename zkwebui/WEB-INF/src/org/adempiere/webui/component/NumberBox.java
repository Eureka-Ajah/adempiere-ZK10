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

package org.adempiere.webui.component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.zkoss.zhtml.Table;
import org.zkoss.zhtml.Td;
import org.zkoss.zhtml.Tr;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Vbox;

/**
 *
 * @author  <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @date    Mar 11, 2007
 * @version $Revision: 0.10 $
 * @author Low Heng Sin
 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
 * 		<li><a href="https://github.com/adempiere/adempiere/issues/547">
 * 		FR [ 547 ] Bad align in Number box ZK</a>
 */
public class NumberBox extends Div
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7089099079981906933L;

	private Textbox txtCalc = new Textbox();
    
    boolean integral = false;
    
    NumberFormat format = null;
    
    private Decimalbox decimalBox = null;
    private Button btn;

    private Object m_oldValue = null;

    private boolean btnEnabled = true;

	private Popup popup;
    
    /**
     * 
     * @param integral
     */
    public NumberBox(boolean integral)
    {
        super();
        this.integral = integral;
        init();
    }
    
    /**
     * @return popup
     */
    public Popup getPopupMenu()
    {
    	return popup;
    }
    private void init()
    {
    	Table grid = new Table();
		appendChild(grid);
		grid.setStyle("border: none; padding: 0px; margin: 0px;");
		grid.setDynamicProperty("border", "0");
		grid.setDynamicProperty("cellpadding", "0");
		grid.setDynamicProperty("cellspacing", "0");
		
		Tr tr = new Tr();
		grid.appendChild(tr);
		tr.setStyle("border: none; padding: 0px; margin: 0px; white-space:nowrap; ");

		Td td = new Td();
		tr.appendChild(td);
		td.setStyle("border: none; padding: 0px; margin: 0px;");
		decimalBox = new Decimalbox();
    	if (integral)
    		decimalBox.setScale(0);
    	//	FR 547
    	decimalBox.setStyle("display: inline; text-align: right; padding-right: 2px");
		td.appendChild(decimalBox);
		
		Td btnColumn = new Td();
		tr.appendChild(btnColumn);
		btnColumn.setStyle("border: none; padding: 0px; margin: 0px;");
		btnColumn.setSclass("editor-button");
		btn = new Button();
        btn.setImage("/images/Calculator10.png");
		btn.setTabindex(-1);
		LayoutUtils.addSclass("editor-button", btn);
		btnColumn.appendChild(btn);
        
        popup = getCalculatorPopup();
        LayoutUtils.addSclass("editor-button", btn);
        btn.setPopup(popup);
        btn.setStyle("text-align: center;");
        appendChild(popup);
     
        String style = AEnv.isFirefox2() ? "display: inline" : "display: inline-block"; 
        style = style + ";white-space:nowrap";
        this.setStyle(style);	     
    }
    
    /**
     * 
     * @param format
     */
    public void setFormat(NumberFormat format)
    {
    	this.format = format;
    }
    
    /**
     * 
     * @param value
     */
    public void setValue(Object value) {
        if (value == null) {
            decimalBox.setValue((BigDecimal) null);
        } else if (value instanceof BigDecimal) {
            decimalBox.setValue((BigDecimal) value);
        } else if (value instanceof Number) {
            decimalBox.setValue(BigDecimal.valueOf(((Number) value).doubleValue()));
        } else {
            decimalBox.setValue(new BigDecimal(value.toString()));
        }
    }
    
    /**
     * 
     * @return BigDecimal
     */
    public BigDecimal getValue()
    {
    	return decimalBox.getValue();
    }
    
    /**
     * 
     * @return text
     */
    public String getText()
    {
    	BigDecimal value = decimalBox.getValue();
    	if (value == null) return null;
    	
    	if (format != null)
    		return format.format(value);
    	else
    		return value.toPlainString();
    }
    
    /**
     * 
     * @param value
     */
    public void setValue(String value)
    {
    	Number numberValue = null;
    	
    	if (format != null)
    	{
    		try
			{
    			numberValue = format.parse(value);
    			setValue(numberValue);
			}
			catch (ParseException e)
			{
			}
    	}
    	else
    	{
    		decimalBox.setValue(new BigDecimal(value));
    	}    	
    }
    
    private Popup getCalculatorPopup()
    {
    	final Popup p = new Popup();
		Vbox vbox = new Vbox();

		/* Separador decimal local ------------------------------------------ */
		char sepChar = DisplayType.getNumberFormat(DisplayType.Number, //
				Env.getLanguage(Env.getCtx())).getDecimalFormatSymbols().getDecimalSeparator();
		String sepStr = String.valueOf(sepChar);

		/* Textbox de edición ------------------------------------------------ */
		txtCalc = new Textbox();
		String txtCalcId = ensureComponentId(txtCalc);           // Garantiza ID
		String decUuid   = decimalBox.getUuid();                 // UUID del Decimalbox

		String jsValidate = String.format(
				"return calc.validate('%s','%s',%s,%d,event);", //
				decUuid, txtCalcId, integral, (int) sepChar);
		txtCalc.setWidgetListener("onKeyPress", jsValidate);
		txtCalc.setMaxlength(250);
		txtCalc.setCols(30);
		vbox.appendChild(txtCalc);

		/* Fila 1 ----------------------------------------------------------- */
		Hbox row1 = new Hbox();
		row1.appendChild(btnCalc("AC", 40, "calc.clearAll('" + txtCalcId + "')"));
		row1.appendChild(btnCalc("7", 30, "calc.append('" + txtCalcId + "','7')"));
		row1.appendChild(btnCalc("8", 30, "calc.append('" + txtCalcId + "','8')"));
		row1.appendChild(btnCalc("9", 30, "calc.append('" + txtCalcId + "','9')"));
		row1.appendChild(btnCalc("*", 30, "calc.append('" + txtCalcId + "',' * ')"));
		vbox.appendChild(row1);

		/* Fila 2 ----------------------------------------------------------- */
		Hbox row2 = new Hbox();
		row2.appendChild(btnCalc("C", 40, "calc.clear('" + txtCalcId + "')"));
		row2.appendChild(btnCalc("4", 30, "calc.append('" + txtCalcId + "','4')"));
		row2.appendChild(btnCalc("5", 30, "calc.append('" + txtCalcId + "','5')"));
		row2.appendChild(btnCalc("6", 30, "calc.append('" + txtCalcId + "','6')"));
		row2.appendChild(btnCalc("/", 30, "calc.append('" + txtCalcId + "',' / ')"));
		vbox.appendChild(row2);

		/* Fila 3 ----------------------------------------------------------- */
		Hbox row3 = new Hbox();
		row3.appendChild(btnCalc("%", 40,
				"calc.percentage('" + decUuid + "','" + txtCalcId + "','" + sepStr + "')"));
		row3.appendChild(btnCalc("1", 30, "calc.append('" + txtCalcId + "','1')"));
		row3.appendChild(btnCalc("2", 30, "calc.append('" + txtCalcId + "','2')"));
		row3.appendChild(btnCalc("3", 30, "calc.append('" + txtCalcId + "','3')"));
		row3.appendChild(btnCalc("-", 30, "calc.append('" + txtCalcId + "',' - ')"));
		vbox.appendChild(row3);

		/* Fila 4 ----------------------------------------------------------- */
		Hbox row4 = new Hbox();
		Button dummy = new Button("$");
		dummy.setWidth("40px");
		dummy.setDisabled(true);
		row4.appendChild(dummy);

		Button dot = btnCalc(sepStr, 30, //
				"calc.append('" + txtCalcId + "','" + sepStr + "')");
		dot.setDisabled(integral);
		row4.appendChild(dot);

		row4.appendChild(btnCalc("0", 30, "calc.append('" + txtCalcId + "','0')"));
		row4.appendChild(btnCalc("=", 30,
				"calc.evaluate('" + decUuid + "','" + txtCalcId + "','" + sepStr + "')"));
		row4.appendChild(btnCalc("+", 30, "calc.append('" + txtCalcId + "',' + ')"));
		vbox.appendChild(row4);

		p.appendChild(vbox);
		return p;
    }
    
    /** Crea un botón de la calculadora con el listener JS apropiado */
	private Button btnCalc(String label, int widthPx, String js) {
		Button b = new Button(label);
		b.setWidth(widthPx + "px");
		b.setWidgetListener("onClick", js + ";");
		return b;
	}

	private String ensureComponentId(HtmlBasedComponent comp) {
	    if (comp.getId() == null || comp.getId().isEmpty()) {
	    	String newId = "n" + java.util.UUID.randomUUID().toString().replace("-", "");
	        comp.setId(newId);
	    }
	    return comp.getId();
	}


    /**
     * 
     * @return boolean
     */
	public boolean isIntegral() {
		return integral;
	}

	/**
	 * 
	 * @param integral
	 */
	public void setIntegral(boolean integral) {
		this.integral = integral;
		if (integral)
			decimalBox.setScale(0);
		else
			decimalBox.setScale(Decimalbox.AUTO);
	}
	
	/**
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled)
	{
	     decimalBox.setReadonly(!enabled);
	     
	     boolean isCalculatorEnabled = btnEnabled && enabled;
	     btn.setEnabled(isCalculatorEnabled);
	     if (isCalculatorEnabled)
	    	 btn.setPopup(popup);
	     else 
	     {
	    	 Popup p = null;
	    	 btn.setPopup(p);
	     }
	}
	
	/**
	 * 
	 * @return boolean
	 */
	public boolean isEnabled()
	{
		 return decimalBox.isReadonly();
	}
	
	public boolean isReadonly()
	{
		return decimalBox.isReadonly();
	}
	
	@Override
	public boolean addEventListener(String evtnm, EventListener listener)
	{
	     if(Events.ON_CLICK.equals(evtnm))
	     {
	       	 return btn.addEventListener(evtnm, listener);
	     }
	     else
	     {
	         return decimalBox.addEventListener(evtnm, listener);
	     }
	}
	
	@Override
	public void focus()
	{
		decimalBox.focus();
	}
	
	/**
	 * 
	 * @return decimalBox
	 */
	public Decimalbox getDecimalbox()
	{
		return decimalBox;
	}
	
	public void setCalculatorEnabled(boolean enabled)
	{
		btnEnabled = enabled;
		btn.setEnabled(btnEnabled);
		btn.setVisible(btnEnabled);
	}
	public boolean isCalculatorEnabled()
	{
		return this.btnEnabled;
	}

    /**
     * Set the old value of the field.  For use in future comparisons.
     * The old value must be explicitly set though this call.
     */
    public void set_oldValue() {
        this.m_oldValue = getValue();
    }

    /**
     * Get the old value of the field explicitly set in the past
     * @return
     */
    public Object get_oldValue() {
        return m_oldValue;
    }
    /**
     * Has the field changed over time?
     * @return true if the old value is different than the current.
     */
    public boolean hasChanged() {
        // Both or either could be null
        if(getValue() != null)
            if(m_oldValue != null)
                return !m_oldValue.equals(getValue());
            else
                return true;
        else  // getValue() is null
            if(m_oldValue != null)
                return true;
            else
                return false;
    }

	public Decimalbox getDecimalBox() {
		return decimalBox;
	}
}
