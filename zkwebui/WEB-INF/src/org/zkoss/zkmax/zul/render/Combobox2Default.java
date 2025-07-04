/* Combobox2Default.java

{{IS_NOTE
	Purpose:

	Description:

	History:
		Jun 6, 2008 8:57:53 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;

import org.adempiere.webui.apps.AEnv;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zul.Combobox;

/**
 * {@link Combobox}'s default mold.
 *
 * @author jumperchen
 *
 * @since 3.5.0
 *
 * @author hengsin
 * modify default zk layout for combobox
 */
@SuppressWarnings("serial")
public class Combobox2Default extends Combobox implements ComponentCtrl {

    @Override
    public void redraw(final Writer out) throws IOException {
        // 1 ► Identificación y clases CSS
        final String uuid = getUuid();
        final String zcls = getZclass();
        final Execution exec = Executions.getCurrent();

        // 2 ► Atributos del <span> contenedor
        final String outerAttrs = buildOuterAttrs();

        // 3 ► Atributos del <input> interno
        String inputAttrs = buildInnerAttrs();
        final int stylePos = inputAttrs.indexOf("style");
        if (stylePos >= 0)
            inputAttrs = inputAttrs.substring(0, stylePos); // quita style anterior
        inputAttrs = inputAttrs.trim() + " style='width:100%'";

        /* ------------------------------------------------------------------
         * 4 ► BLOQUE DE APERTURA ( <span> + tablas hasta <table id="…!cave"> )
         * ------------------------------------------------------------------ */
        final StringBuilder open = new StringBuilder();
        open.append("<span id=\"").append(uuid).append("\"")
            .append(outerAttrs)
            .append(" z.type=\"zul.cb.Cmbox\" z.combo=\"true\">");

        open.append("<table border='0' cellspacing='0' cellpadding='0' style='display:")
            .append(AEnv.isInternetExplorer() ? "inline" : "inline-block")
            .append("; width:").append(getWidth() != null ? getWidth() : "auto").append("'>");

        open.append("<tr style='white-space:nowrap;border:none'>");
        open.append("<td style='width:100%;border:none'>");
        open.append("<input id=\"").append(uuid).append("!real\" autocomplete='off' class=\"")
            .append(zcls).append("-inp\" ").append(inputAttrs).append("/></td>");

        open.append("<td style='width:17px'><span id=\"").append(uuid).append("!btn\" class=\"")
            .append(zcls).append("-btn\"");
        if (!isButtonVisible())
            open.append(" style='display:none'");
        else
            open.append(" style='margin-left:2px'");
        open.append(">");
        open.append("<img class=\"").append(zcls).append("-img\" onmousedown='return false;' src=\"")
            .append(exec.encodeURL("~./img/spacer.gif")).append("\"/></span></td>");
        open.append("</tr></table>");

        open.append("<div id=\"").append(uuid).append("!pp\" class=\"").append(zcls)
            .append("-pp\" style='display:none' tabindex='-1'>");
        open.append("<table id=\"").append(uuid).append("!cave\" cellpadding='0' cellspacing='0'>");

        // ► Escribir apertura antes de hijos
        out.write(open.toString());

        /* ------------------------------------------------------------------
         * 5 ► Renderizar hijos dentro de !cave
         * ------------------------------------------------------------------ */
        for (Object child : getChildren()) {
            if (child instanceof ComponentCtrl) {
                ((ComponentCtrl) child).redraw(out);
            }
        }

        /* ------------------------------------------------------------------
         * 6 ► Cierre
         * ------------------------------------------------------------------ */
        out.write("</table></div></span>");
    }

    /* =============================================================== */
    /* ► Utilidades privadas                                           */
    /* =============================================================== */

    /** Atributos externos del <span>. */
    private String buildOuterAttrs() {
        final StringBuilder sb = new StringBuilder();
        if (getSclass() != null && !getSclass().isEmpty())
            sb.append(" class=\"").append(getSclass()).append("\"");
        if (getStyle() != null && !getStyle().isEmpty())
            sb.append(" style=\"").append(getStyle()).append("\"");
        return sb.toString();
    }

    /** Atributos básicos del <input>. */
    private String buildInnerAttrs() {
        final StringBuilder sb = new StringBuilder();
        if (getTooltiptext() != null)
            sb.append(" title=\"").append(getTooltiptext()).append("\"");
        if (getTabindex() >= 0)
            sb.append(" tabindex=\"").append(getTabindex()).append("\"");
        if (isReadonly())
            sb.append(" readonly='readonly'");
        return sb.toString();
    }
}
