/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import mx.gob.cultura.portal.request.ListBICRequest;
import mx.gob.cultura.portal.response.Document;
import mx.gob.cultura.portal.response.Entry;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import org.semanticwb.portal.api.SWBResourceModes;
import org.semanticwb.portal.api.SWBResourceURL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.semanticwb.SWBPlatform;

import static mx.gob.cultura.portal.resources.PagerAction.NUM_PAGE_LIST;

/**
 *
 * @author sergio.tellez
 */
public class SearchCulturalProperty extends PagerAction {
    
    private static final int SEGMENT = 8;
    private static final Logger LOG = Logger.getLogger(SearchCulturalProperty.class.getName());
    
    @Override
    public void doAdmin(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
    	StringBuilder ret = new StringBuilder();
    	try {
            if ("add".equals(paramRequest.getAction()) || "edit".equals(paramRequest.getAction())) {
    		SWBResourceURL url=paramRequest.getRenderUrl();
    		url.setMode(SWBResourceModes.Mode_ADMIN);
    		url.setAction("save");
    		ret.append("<form method=\"POST\" action=\"").append(url.toString()).append("\"> \n");
    		ret.append("	<div class=swbform> \n");
    		ret.append("		<table width=\"100%\"  border=\"0\" cellpadding=\"5\" cellspacing=\"0\"> \n");
    		ret.append("			<tr> \n");
    		ret.append("				<td class=\"datos\">URL Endpoint: </td> \n");
    		ret.append("				<td class=\"valores\"> \n");
    		ret.append("					<input type=\"text\" name=\"endpointURL\" value=\"").append(getResourceBase().getAttribute("endpointURL","").trim()).append("\" size=\"40\">");
    		ret.append("				</td>");
    		ret.append("			</tr> \n");
    		ret.append("			<tr> \n");
    		ret.append("				<td class=\"datos\">Resultados por página: </td> \n");
    		ret.append("				<td class=\"valores\"> \n");
    		ret.append("					<input type=\"text\" name=\"resultsPage\" value=\"").append(getResourceBase().getAttribute("resultsPage","").trim()).append("\" size=\"40\">");
    		ret.append("				</td>");
    		ret.append("			</tr> \n");
    		ret.append("			<tr> \n");
    		ret.append("				<td colspan=2 align=right> \n");
    		ret.append("					<br><hr size=1 noshade> \n");
    		ret.append("					<input type=submit name=btnSave value=\"Enviar\" class=boton> \n");
    		ret.append("				</td> \n");
    		ret.append("			</tr> \n"); 
    		ret.append("		</table> \n");
    		ret.append("	</div> \n");
    		ret.append("</form> \n");
            }else if ("save".equals(paramRequest.getAction())) {
    		getResourceBase().setAttribute("endpointURL", request.getParameter("endpointURL"));
    		getResourceBase().setAttribute("resultsPage", request.getParameter("resultsPage"));
    		getResourceBase().updateAttributesToDB();
   		SWBResourceURL url=paramRequest.getRenderUrl();
                url.setMode(SWBResourceModes.Mode_ADMIN);
                url.setAction("resume");
                response.sendRedirect(url.toString());
            }else if ("resume".equals(paramRequest.getAction())) {
    		SWBResourceURL url=paramRequest.getRenderUrl();
    		url.setMode(SWBResourceModes.Mode_ADMIN);
    		url.setAction("edit");
    		ret.append("<form method=\"POST\" action=\"").append(url.toString()).append("\"> \n");
    		ret.append("	<div class=swbform> \n");
    		ret.append("		<table width=\"100%\"  border=\"0\" cellpadding=\"5\" cellspacing=\"0\"> \n");
    		ret.append("			<tr> \n");
    		ret.append("				<td class=\"datos\">URL Endpoint: </td> \n");
    		ret.append("				<td class=\"valores\"> \n").append(getResourceBase().getAttribute("endpointURL","")).append("</td>");
    		ret.append("			</tr> \n");
    		ret.append("			<tr> \n");
    		ret.append("				<td class=\"datos\">Resultados por página: </td> \n");
    		ret.append("				<td class=\"valores\"> \n").append(getResourceBase().getAttribute("resultsPage","").trim()).append("</td>");
    		ret.append("			</tr> \n");
    		ret.append("			<tr> \n");
    		ret.append("				<td colspan=2 align=right> \n");
    		ret.append("					<br><hr size=1 noshade> \n");
    		ret.append("					<input type=submit name=btnSave value=\"Regresar\" class=boton> \n");
    		ret.append("				</td> \n");
    		ret.append("			</tr> \n"); 
    		ret.append("		</table> \n");
    		ret.append("	</div> \n");
    		ret.append("</form> \n");
            }
    	}catch (Exception e) { LOG.info(e.getMessage());}
    	response.getWriter().print(ret.toString());
    }
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
    	request.setCharacterEncoding("UTF-8");
        Document document = null;
        List<Entry> publicationList = new ArrayList<>();
    	String url = "/swbadmin/jsp/rnc/documents.jsp";
    	RequestDispatcher rd = request.getRequestDispatcher(url);
    	try {
            if (null != request.getParameter("word") && !request.getParameter("word").isEmpty()) {
    		document = getReference(request);
                if (null != document) {
                    publicationList = document.getRecords();
                    request.setAttribute("aggs", document.getAggs());
                    request.getSession().setAttribute("NUM_RECORDS_TOTAL", document.getTotal());
                    request.getSession().setAttribute(FULL_LIST, document.getRecords());
                }
                request.setAttribute("word", request.getParameter("word"));
                init(request, response, paramRequest);
    	    }
            request.setAttribute("references", publicationList);
	    request.setAttribute("paramRequest", paramRequest);
	    rd.include(request, response);
	}catch (ServletException se) {
            LOG.log(Level.SEVERE, se.getMessage());
	}
    }
    
    @Override
    public void doSort(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        Document document = null;
        List<Entry> publicationList = new ArrayList<>();
        try {
            if (null != request.getParameter("word") && !request.getParameter("word").isEmpty()) {
    		document = getReference(request);
                if (null != document) {
                    publicationList = document.getRecords();
                    request.getSession().setAttribute(FULL_LIST, document.getRecords());
                }
                request.setAttribute("f", request.getParameter("sort"));
                request.setAttribute("word", request.getParameter("word"));
                init(request, response, paramRequest);
    	    }
            request.setAttribute("references", publicationList);
	    request.setAttribute("paramRequest", paramRequest);
	}catch (Exception se) {
            LOG.log(Level.SEVERE, se.getMessage());
	}
        super.doSort(request, response, paramRequest);
    }
    
    @Override
    public void doPage(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws java.io.IOException {
        int pagenum = 0;
        Document document = null;
        String p = request.getParameter("p");
        HttpSession session = request.getSession();
        List<Entry> publicationList = null;
        if (null != p)
            pagenum = Integer.parseInt(p);
        if (pagenum<=0) pagenum = 1;
        session.setAttribute(NUM_PAGE_LIST, pagenum);
        session.setAttribute("PAGE_NUM_ROW", PAGE_NUM_ROW);
        document = getReference(request);
        if (null != document) {
            publicationList = document.getRecords();
            request.getSession().setAttribute(FULL_LIST, publicationList);
            page(pagenum, session);
        }
        request.setAttribute("word", request.getParameter("word"));
        String url = "/swbadmin/jsp/rnc/rows.jsp";
        if (null != request.getParameter("m") && "l".equalsIgnoreCase(request.getParameter("m")))
            request.setAttribute("mode", "row lista");
        else request.setAttribute("mode", "row");
        request.setAttribute("m",request.getParameter("m"));
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        }catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
    
    private Document getReference(HttpServletRequest request) {
        Document document = null;
        String words = request.getParameter("word");
    	String uri = SWBPlatform.getEnv("rnc/endpointURL",getResourceBase().getAttribute("endpointURL","http://localhost:8080")).trim() + "/api/v1/search?q=";
    	uri += getParamSearch(words);
        uri += getRange(request);
        if (null != request.getParameter("sort")) {
            if (request.getParameter("sort").equalsIgnoreCase("datedes")) uri += "&sort=-datecreated.value";
            if (request.getParameter("sort").equalsIgnoreCase("dateasc")) uri += "&sort=datecreated.value";
            if (request.getParameter("sort").equalsIgnoreCase("statdes")) uri += "&sort=-resourcestats.views";
            if (request.getParameter("sort").equalsIgnoreCase("statasc")) uri += "&sort=resourcestats.views";
        }
	ListBICRequest req = new ListBICRequest(uri);
        try {
            document = req.makeRequest();
        }catch (Exception se) {
            LOG.log(Level.SEVERE, se.getMessage());
        }
        return document;
    }
    
    private String getRange(HttpServletRequest request) {
        int s = 0;
    	StringBuilder range = new StringBuilder("&from=");
    	String start = request.getParameter("p");
        if (null != start) {
            try { 
                s = Integer.parseInt(start);
                if (s > 1) s = (s-1)*SEGMENT;
            }catch (NumberFormatException e) { }
        }
        range.append(String.valueOf(s));
    	range.append("&size=").append(SEGMENT);
    	return range.toString();
    }
    
    /**private List<Entry> getRange(int range, List<Entry> records) {
        List<Entry> publicationListTmp = new ArrayList<>();
        if (null != records && records.size() > SEGMENT) {
            int count = 0;
            for (int i = range; i < records.size(); i++) {
                publicationListTmp.add(records.get(i));
                count++;
                if (count == SEGMENT) break;
            }
            records = publicationListTmp;
        }
        return records;
    }**/
    
    private String getParamSearch(String words) {
    	StringBuilder parameters = new StringBuilder();
    	String fix = words.replaceAll(",", " ").replaceAll(" ,", " ").replaceAll(", ", " ");
    	String[] search = fix.split(" ");
    	if (search.length > 0) {
	    for (int i=0; i<search.length; i++) {
                parameters.append("%2B");
	    	String param  = search[i].trim();
	    	parameters.append(param);
	    }
	    if (parameters.length() > 1)
	    	parameters.delete(0, 3);
	    return parameters.toString();
    	}else 
            return words;
    }
    
    /**private int getStart(HttpServletRequest request) {
    	int s = 1;
    	String start = request.getParameter("s");
    	try { 
            if (null != start)
                s = Integer.parseInt(start); 
        }catch (NumberFormatException e) { }
        if (s > 0) s = s*SEGMENT-1;
    	return s;
    }**/
}