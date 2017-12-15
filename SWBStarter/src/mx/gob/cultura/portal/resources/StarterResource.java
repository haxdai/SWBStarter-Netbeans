package mx.gob.cultura.portal.resources;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;
import org.semanticwb.portal.api.SWBResourceURL;

/**
 * Starter resource. This class shows common use case of a very simple SemanticWebBuilder Resource.
 * @author Hasdai Pacheco
 */
public class StarterResource extends GenericResource {
    private static final String mode_detail = "DETAIL";
    private static final String act_someact = "SOMEACT";
    
    @Override
    public void processRequest(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        /*
        processRequest method manages resource requests to switch between views 
        or do some processing every time a request is sent from the resource.
        */
        
        String mode = paramRequest.getMode(); //Get request mode from SWBParamRequest object
        
        //Compare request mode with some predefined modes and call a different view
        if (mode_detail.equals(mode)) {
            doDetail(request, response, paramRequest); //Calls detail view
        } else {
            super.processRequest(request, response, paramRequest);
        }
    }

    @Override
    public void processAction(javax.servlet.http.HttpServletRequest request, SWBActionResponse response) throws SWBResourceException, IOException {
        /*
        ProcessAction manages special requests that are executed once. 
        On each request, the calling view waits until it is processed,
        then control is returned to the calling view.
        */
        
        String action = response.getAction(); //Get request action from SWBActionResponse object
        
        //Compare request action and do processing accordingly
        if (act_someact.equals(action)) {
            System.out.println("I was called from a view to execute some important action");
        } else {
            super.processAction(request, response);
        }
    }

    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        /*
        This method overrides default view method from parent class.
        */
        
        //Get writer to send data through HttpServletResponse object
        PrintWriter out = response.getWriter();
        
        //Create a new URL to pass control to processAction method using SWBResourceURL object
        SWBResourceURL actUrl = paramRequest.getActionUrl().setAction(act_someact); 
        
        //Create a new URL to pass control to another view through processRequest method using SWBResourceURL object
        SWBResourceURL detailUrl = paramRequest.getRenderUrl().setMode(mode_detail);
        
        //Print some HTML to the response
        out.print("<p>Hello, do some important <a href=\"" + actUrl + "\">action</a></p>");
        out.print("<p>Or go to another <a href=\"" + detailUrl + "\">view</a> through a mode</p>");
    }
    
    public void doDetail(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        /*
        Custom method that implements a "detail" view
        */
        
        //Get writer to send data through HttpServletResponse object
        PrintWriter out = response.getWriter();
        
        //Create a new URL to pass control to another view through processRequest method using SWBResourceURL object
        SWBResourceURL viewUrl = paramRequest.getRenderUrl().setMode(SWBResourceURL.Mode_VIEW);
        
        //Print some HTML to the response
        out.print("<p>This is the detail view, go back to main <a href=\"" + viewUrl + "\">view</a></p>");
    }
    
    public void doJSPView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        /*
        Custom method that includes a JSP file to render resource view.
        This could be convenient to follow MVC pattern.
        */
        
        //Define path to JSP file
        String JSPPath = "/swbadmin/jsp/Hello.jsp";
        
        //Get a request dispatcher from HttpServletRequest object (this dispatcher includes the JSP file in the response)
        RequestDispatcher rd = request.getRequestDispatcher(JSPPath);
        
        try {
            //Put additional attributes in the request needed in the view to implement its logic
            request.setAttribute("paramRequest", paramRequest);
            
            //Include JSP file in the response
            rd.include(request, response);
        } catch (ServletException se) {
            se.printStackTrace();
        }
    }
}
