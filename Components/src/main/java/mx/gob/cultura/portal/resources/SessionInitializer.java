package mx.gob.cultura.portal.resources;

import com.hp.hpl.jena.ontology.OntModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Iterator;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.servlet.RequestDispatcher;
import org.semanticwb.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBPortal;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.User;
import org.semanticwb.model.UserRepository;
import org.semanticwb.model.WebPage;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.portal.SWBSessionObject;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

/**
 * Initializes and terminates a user's session on the website
 * @author jose.jimenez
 */
public class SessionInitializer extends GenericResource {

    
    private static final Logger LOG = SWBUtils.getLogger(SessionInitializer.class);
    
    public static final String FACEBOOK = "facebook";
    
    private static final String TWITTER = "twitter";
    
    private static final String FACEBOOKID_URI =
            "http://www.semanticwebbuilder.org/swb4/ontology#facebookId";
    
    private static final String REDIRECT_MODE = "redirect";
    
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        if (paramRequest.getMode().equals(SessionInitializer.REDIRECT_MODE)) {
            doRedirect(request, response, paramRequest);
        } else {
            if (paramRequest.getCallMethod() == SWBParamRequest.Call_STRATEGY) {
                showStrategyView(request, response, paramRequest);
            } else if (paramRequest.getCallMethod() == SWBParamRequest.Call_DIRECT) {
                
            }
        }
    }
    
    /**
     * Genera la interface de usuario correspondiente a la vista de estrategia
     * @param request la peticion del cliente
     * @param response la respuesta al cliente
     * @param paramsRequest reune parametros de SWB, utiles para la respuesta de la peticion
     * @throws SWBResourceException si se genera algun problema en la ejecucion 
     *         de las solicitudes a la API de SWB
     */
    private void showStrategyView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramsRequest) throws SWBResourceException {
        
        String url = "/swbadmin/jsp/rnc/sessionInitializerMenu.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            request.setAttribute("paramRequest", paramsRequest);
            rd.include(request, response);
        } catch (Exception se) {
            se.printStackTrace(System.err);
        }
    }
    
    /**
     * Genera la interface de usuario con enlaces de las redes sociales para iniciar sesion
     * @param request la peticion del cliente
     * @param response la respuesta al cliente
     * @param paramsRequest reune parametros de SWB, utiles para la respuesta de la peticion
     */
    private void showSocialNetLinks(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramsRequest) {
        
        try {
            PrintWriter out = response.getWriter();
            if (!paramsRequest.getUser().isSigned()) {
                out.println(SessionInitializer.getFacebookLink(paramsRequest.getWebPage().getWebSiteId()));
            } else {
                out.println("<!-- user is signed -->");
            }
        } catch (Exception se) {
            se.printStackTrace(System.err);
        }
    }
    
    /**
     * Genera la liga hacia el inicio de sesion con Facebook
     * @param modelId el identificador del sitio a partir del cual se obtiene la imagen a mostrar
     * @return un {@code String} representando un enlace para iniciar sesion con Facebook
     */
    public static String getFacebookLink(String modelId) {
        
        StringBuilder text = new StringBuilder(128);
        text.append("<a href=\"#\" onclick=\"javascript:faceLogin();\"><img src=\"/work/models/");
        text.append(modelId);
        text.append("/img/icono-fb.png\"></a>\n");
        return text.toString();
    }
    
    //Atiende la accion de una peticion para registrar al usuario como firmado en la aplicacion,
    //o para desasociarlo de la sesion activa y darla por terminada
    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response)
            throws SWBResourceException, IOException {
        
        String action = response.getAction();
        //System.out.println("--- En processAction --->>>");
        if (action.equals("openSession")) {
            createSignedSession(request, response);
        } else if (action.equals("closeSession")) {
            terminateSession(request, response);
        }
        
        response.setMode(SessionInitializer.REDIRECT_MODE);
    }
    
    /**
     * Agrega al usuario propiamente a la sesion creada para SWB
     * @param request peticion del cliente con los datos del usuario
     * @param response respuesta de SWB para la accion solicitada en la peticion
     * @throws SWBResourceException en caso de que el usuario especificado en la peticion
     * ya este registrado en una sesion
     */
    private void createSignedSession(HttpServletRequest request, SWBActionResponse response)
            throws SWBResourceException {
        
        UserRepository userRepo = response.getWebPage().getWebSite().getUserRepository();
        User user = null;
        boolean isSocialNetUser = false;
        
        //solo crear usuarios si usan una red social
        if (request.getParameter("source") != null && !request.getParameter("source").isEmpty()) {
            user = getSWBUser(request, response, userRepo);
            isSocialNetUser = true;
            request.getSession(true).setAttribute("isSocialNetUser", "true");
        }
        if (SWBPlatform.getSecValues().isMultiple()) {
            String login = !isSocialNetUser ? request.getParameter("wb_username")
                            : request.getParameter("email");
            Iterator<SWBSessionObject> llist = SWBPortal.getUserMgr().listSessionObjects();
            while (llist.hasNext()) {
                SWBSessionObject so = llist.next();
                Iterator<Principal> lpri = so.getSubjectByUserRep(userRepo.getId())
                         .getPrincipals().iterator();
                if (lpri.hasNext() && login.equalsIgnoreCase(((User)lpri.next()).getLogin())) {
                    throw new SWBResourceException("User already logged in");
                }
            }
        }
        if (null != user) {
            //se agrega el usuario a la sesion
            User oldUser = response.getUser();
            String id = request.getParameter("id");
            try {
                user.checkCredential(id.toCharArray());
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            
            
            Subject subject = SWBPortal.getUserMgr().getSubject(request,
                                response.getWebPage().getWebSiteId());
            subject.getPrincipals().clear();
            subject.getPrincipals().add(user);
            oldUser = user;
            if (null == user.getLanguage()) {
                user.setLanguage("es");   //forzar lenguaje si no se dio de alta.
            }
        }
    }

    /**
     * Termina la sesion del usuario, eliminando la asociación de este con el sitio en uso
     * @param request peticion del cliente
     * @param response respuesta de SWB para la accion solicitada en la peticion
     */
    private void terminateSession(HttpServletRequest request, SWBActionResponse response) {
        
        WebPage webpage = response.getWebPage();
        UserRepository ur = webpage.getWebSite().getUserRepository();
        String context = ur.getLoginContext();
        Subject subject = SWBPortal.getUserMgr().getSubject(request, webpage.getWebSiteId());
        User user = null;
        Iterator it = subject.getPrincipals().iterator();
        
        if (it.hasNext()) {
            user = (User) it.next();
        }
        LoginContext lc;
        try {
            if (null != user) {
                //Invalidate user even in cluster
                user.checkCredential("{123}456".toCharArray());
            }
            lc = new LoginContext(context, subject);
            lc.logout();
            request.getSession(false).invalidate();
            String url = request.getParameter("wb_goto");
            if ((url == null)) {
                url = webpage.getUrl(response.getUser().getLanguage());
            }
            SessionInitializer.LOG.debug("LOGOUT (Path, uri, url): " +
                    SWBPlatform.getContextPath() + "   |   " +
                    request.getRequestURI() + "    |  " + url);
        } catch (Exception elo) {
            SessionInitializer.LOG.error("LoggingOut " + subject, elo);
        }
    }
    
    /**
     * Busca un usuario en el repositorio de usuarios correspondiente al sitio en uso,
     * con los datos contenidos en la peticion, si no lo encuentra, lo crea.
     * @param request peticion del usuario con datos para crear un registro de usuario
     * @param response respuesta al usuario
     * @param userRepo repositorio de usuarios al que debe pertenecer el usuario
     * @return una instancia {@code User} con los datos contenidos en la peticion
     */
    private User getSWBUser(HttpServletRequest request, SWBActionResponse response, UserRepository userRepo) {
        
        String source = request.getParameter("source");
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String login = !"".equals(email) ? email : source + "_" + id;
        
        OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
        User user = userRepo.getUserByLogin(login);
        if (null == user) { //Si no existe usuario con ese login
            String encryptdPwd;
            try {
                encryptdPwd = SWBUtils.CryptoWrapper.passwordDigest(id);
            } catch (Exception e) {
                encryptdPwd = id;
            }
            User newUser = userRepo.createUser();
            newUser.setLogin(!"".equals(email) ? email : source + "_" + id);
            newUser.setPassword(encryptdPwd);
            newUser.setLanguage("es");
            newUser.setFirstName(name);
            newUser.setLastName("");
            newUser.setSecondLastName("");
            newUser.setEmail(email);
            newUser.setActive(true);
            
            if (source.equals(SessionInitializer.FACEBOOK)) {
                SemanticObject obj = newUser.getSemanticObject();
                obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                        SessionInitializer.FACEBOOKID_URI), id);
            }
            user = newUser;
            
        } else {  //si existe un usuario con ese correo, revisar si viene de la misma red social
            //revisar que el usuario tenga un identificador correspondiente a la red social
            if (SessionInitializer.FACEBOOK.equals(source)) {
                if (user.getSemanticObject().getRDFResource()
                        .getProperty(ont.createDatatypeProperty(
                                SessionInitializer.FACEBOOKID_URI)) == null) {
                    SemanticObject obj = user.getSemanticObject();
                    obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                            SessionInitializer.FACEBOOKID_URI), id);
                }
            }
        }
        
        return user;
    }
    
    /**
     * Provoca que el navegador del cliente muestre la ruta indicada en {@code url}
     * @param request peticion del usuario para cerrar la sesion
     * @param response respuesta al usuario
     * @param paramRequest reune parametros de SWB, utiles para la respuesta de la peticion
     */
    public void doRedirect(HttpServletRequest request, HttpServletResponse response,
            org.semanticwb.portal.api.SWBParamRequest paramRequest) {
        
        try {
            String url = paramRequest.getWebPage().getRealUrl();
            if (paramRequest.getAction().equals("closeSession")) {
                WebPage home = WebPage.ClassMgr.getWebPage("home", paramRequest.getWebPage().getWebSite());
                url = home.getRealUrl(paramRequest.getUser().getLanguage());
            }
            response.setContentType("Text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><meta http-equiv=\"Refresh\" CONTENT=\"0; URL=" +
                    url + "\" /><script>window.location='" + url +
                    "';</script></head></html>");
            out.flush();
        } catch (IOException e) {
            SessionInitializer.LOG.error("Redirecting user", e);
        }
    }

    //Dirige la peticion al metodo correspondiente al modo solicitado
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        if (paramRequest.getMode().equals(SessionInitializer.REDIRECT_MODE)) {
            doRedirect(request, response, paramRequest);
        } else {
            super.processRequest(request, response, paramRequest);
        }
    }
    
}
