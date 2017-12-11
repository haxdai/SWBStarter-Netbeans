/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.infotec.semanticwebbuilder.resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import mx.gob.cultura.portal.response.Entry;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.response.DigitalObject;
import mx.gob.cultura.portal.response.Identifier;
import mx.gob.cultura.portal.response.Title;
import org.semanticwb.SWBUtils;

import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import org.semanticwb.portal.api.SWBResourceModes;
import org.semanticwb.portal.api.SWBResourceURL;

/**
 *
 * @author sergio.tellez
 */
public class SearchCulturalProperty extends PagerAction {
    
    private static final Logger LOG = Logger.getLogger(SearchCulturalProperty.class.getName());
    
    private static final int SEGMENT = 8;
    
    @Override
    public void doAdmin(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
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
    	List<Entry> references = null;
        List<Entry> publicationList = new ArrayList<>();
    	String url = "/swbadmin/jsp/rnc/documents.jsp";
    	RequestDispatcher rd = request.getRequestDispatcher(url);
    	try {
            if (null != request.getParameter("word") && !request.getParameter("word").isEmpty()) {
    		references = getReferences(request.getParameter("word"), request);
                int results = references.size();
                publicationList = getRange(getStart(request), references);
    		request.setAttribute("count", results);
                request.setAttribute("word", request.getParameter("word"));
                request.getSession().setAttribute(FULL_LIST, references);
                init(request, response, paramRequest);
    		//request.setAttribute("pages", getPages(request, paramRequest, results));
    	    }
            request.setAttribute("references", publicationList);
	    request.setAttribute("paramRequest", paramRequest);
            //paramRequest.getArguments().put("references", references);
	    rd.include(request, response);
	}catch (ServletException se) {
            LOG.info(se.getMessage());
	}
    }
    
    private String getGrid(List<Entry> references) {
        StringBuilder grid = new StringBuilder();
        if (!references.isEmpty()) {
            for (Entry reference : references) {
                Identifier identifier = new Identifier();
                DigitalObject digital = new DigitalObject();
                List<Title> titles = reference.getTitle();
                List<DigitalObject> digitalobject = reference.getDigitalobject();
                List<Identifier> identifiers = reference.getIdentifier();
                if (!digitalobject.isEmpty()) digital = digitalobject.get(0);
                for (Identifier id : identifiers) {
                    if (id.isPreferred()) identifier = id;
                }
                grid.append("<div class=\"pieza\">");
                grid.append("   <div>");
                grid.append("        <a href=\"/swb/cultura/detalle?id=").append(identifier.getValue()).append("\">");
                grid.append("           <img src=\"").append(digital.getUrl()).append("\">");
                grid.append("        </a>");
                grid.append("   </div>");
                grid.append("   <p class=\"oswB azul tit\"><a href=\"#\">").append(titles.get(0).getValue()).append("</a></p>");
                grid.append("   <p class=\"azul autor\"><a href=\"#\">").append(reference.getCreator().get(0)).append("</a></p>");
                grid.append("</div>");
            } 
        }
        return grid.toString();
    }
    
    private List<Entry> getReferences(String words, HttpServletRequest request) {
    	String uri = getResourceBase().getAttribute("endpointURL","http://localhost:8080") + "/api/v1/search?q=";
    	List<Entry> publicationList = new ArrayList<>();
    	uri += getParamSearch(words);
    	try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            InputStream is = connection.getInputStream();
            String jsonText = SWBUtils.IO.readInputStream(is);
            Gson gson = new Gson();
            Type entryListType = new TypeToken<ArrayList<Entry>>(){}.getType();
            if (null != request.getSession().getAttribute(FULL_LIST))
                    publicationList = (List<Entry>)request.getSession().getAttribute(FULL_LIST);
            else
                publicationList = gson.fromJson(jsonText, entryListType);
    	}catch (Exception e) {
            e.printStackTrace();
            publicationList = new ArrayList<>();
    	    LOG.info(e.getMessage());
    	}
    	return publicationList;
    }
    
    private List<Entry> getRange(int range, List<Entry> publicationList) {
        List<Entry> publicationListTmp = new ArrayList<>();
        if (publicationList.size() > SEGMENT) {
            int count = 0;
            for (int i = range; i < publicationList.size(); i++) {
                publicationListTmp.add(publicationList.get(i));
                count++;
                if (count == SEGMENT) break;
            }
            publicationList = publicationListTmp;
        }
        return publicationList;
    }
    
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
    
    private String getPages(HttpServletRequest request, SWBParamRequest paramRequest, int results) {
    	int seg = 8;
    	String innerPages = null;
    	StringBuilder back = new StringBuilder();
    	StringBuilder next = new StringBuilder();
    	StringBuilder pages = new StringBuilder();
    	String id=String.valueOf(getResourceBase().getId());
    	if (results > 0) {
            String[] offset={"s"+id};
            SWBResourceURL urlXML = paramRequest.getRenderUrl();
            urlXML.setMode(SWBResourceURL.Mode_XML);
            urlXML.setCallMethod(SWBResourceURL.Call_DIRECT);
            urlXML.setParameters(request.getParameterMap());
            //String url2 = getParameters(request, offset);
    	    String start = request.getParameter("s"+id);
    	    int _i = 0; // Contador del segmento
    	    int s = 1;  // Start
    	    try { s = Integer.parseInt(start); } 
    	    catch (Exception e) { s=1; }
    	    //pages.append("<table>");
    	    pages.append("<div class=\"container paginacion\">");
    	    if (results >= s && _i < SEGMENT) {
                //pages.append("<tr><td>");
    		pages.append("<p>");
    		pages.append("Resultados: ").append(s).append(" - ");
    		if (s+SEGMENT-1 > results)
                    pages.append(results);
    		else pages.append(s+SEGMENT-1);
    		//pages.append(" de ").append(results).append("</td></tr>");
    		pages.append(" de ").append(results).append("</p>");
            }
            //pages.append("<tr>").append("<td>");
            pages.append("<ul class=\"azul\">");
            if (s > 1) {
                int as = s - SEGMENT;
    		if (as < 1) as = 1;
                    //back.append("<a href=\"")
                    back.append("<li><a href=\"")
                    .append(urlXML.toString()).append("s").append(id).append("=").append(as)
                    //.append("\">Anterior</a>");
                    .append("\" class=\"fa fa-long-arrow-left\" aria-hidden=\"true\" title=\"anterior\">&nbsp;</a></li>");
                    pages.append(back).append(" ");
            }
            if (results > SEGMENT) {
    		int p = s / seg + 1;
    		int step = getNormSegment(seg, results, p);
    		for (int z = step; z < seg + p; z++) {
                    if (z * seg <= results + (seg-1)) {
    			if (p != z) {
                            int leap = z - 1;
                            //innerPages="<a href=\"";
                            innerPages="<li><a href=\"";
                            innerPages+=urlXML.toString() +"s"+id+"="+((leap * seg) + 1);
                            //innerPages+="\">"+String.valueOf((z))+"</a>";
                            innerPages+="\">"+String.valueOf((z))+"</a></li>";
    			}else
                            //innerPages=String.valueOf((z));
                            innerPages="<li><a href=\"#\" class=\"select\">"+String.valueOf((z))+"</a></li>";
                        pages.append(innerPages).append(" ");
                    }
    		}
            }
            if (results >= s + seg) {
    		//next.append("<a href=\"");
    		next.append("<li><a href=\"");
    		next.append(urlXML.toString()).append("s").append(id).append("=").append((s + seg));
    		//next.append("\">Siguiente</a>");
    		next.append("\" class=\"fa fa-long-arrow-right\" aria-hidden=\"true\" title=\"siguiente\">&nbsp;</a></li>");
    		pages.append(next);
            }
    	}
    	//pages.append("</td>").append("</tr>").append("</table>");
    	pages.append("</ul></div>");
    	return pages.toString();
    }
    
    private int getNormSegment(int segment, int results, int step) {
    	int numpages = 0;
        if (step == 1) return step;
    	if (segment < 0) return numpages;
    	int mod = results % segment;
    	if (mod == 0)
            numpages = results / segment;
    	else
            numpages = (results / segment) + 1;
    	if (step + segment <= numpages + 1)
            return step;
    	else
            return getNormSegment(segment, results, step-1);
    }
    
    private String getParameters(HttpServletRequest request, String[] offset) {
        StringBuilder sbRet=new StringBuilder();
        sbRet.append("?");
        Enumeration en = request.getParameterNames();
        while(en.hasMoreElements()) {
            String param = en.nextElement().toString();
            if (offset!=null && offset.length > 0) {
                for(int i=0; i < offset.length; i++) {
                    String off=(String)offset[i];
                    if(!off.equals(param)) {
                        String[] values = request.getParameterValues(param);
                        for (int j = 0; j < values.length; j++)
                            sbRet.append(param).append("=").append(values[j]).append("&");
                    }
                }
            }
        } 
        return sbRet.toString().replaceAll(" ", "%20");
    }
    
    private int getStart(HttpServletRequest request) {
    	int s = 1;
    	String id=String.valueOf(getResourceBase().getId());
    	String start = request.getParameter("s"+id);
    	try { s = Integer.parseInt(start); } 
    	catch (Exception e) { s=1; }
        if (s > 0) s = s*SEGMENT-1;
    	return s;
    }
}