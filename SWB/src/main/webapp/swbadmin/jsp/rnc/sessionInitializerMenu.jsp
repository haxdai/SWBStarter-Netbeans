<%-- 
    Document   : sessionInitializerMenu
    Created on : 13/02/2018, 05:43:59 PM
    Author     : jose.jimenez
--%><%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.SWBPlatform, mx.gob.cultura.portal.resources.SessionInitializer, org.semanticwb.portal.api.SWBResourceException"%>
<%
    SWBParamRequest paramsRequest = (SWBParamRequest) request.getAttribute("paramRequest");
    StringBuilder text = new StringBuilder(256);
    String mainLabel;
    String faceAppId;
    String faceVersion;
    boolean isSocialNetUser = false;

    try {
        mainLabel = paramsRequest.getLocaleString("lbl_main");
    } catch (SWBResourceException swbe) {
        mainLabel = "Iniciar sesión";
    }
    faceAppId = paramsRequest.getWebPage().getWebSite().getModelProperty("facebook_appid");
    if (null == faceAppId || faceAppId.isEmpty()) {
        throw new SWBResourceException("There is no app id configured for Facebook");
    }
    try {
        faceVersion = paramsRequest.getLocaleString("vl_facebook_version");
    } catch (SWBResourceException swbe) {
        faceVersion = "v2.11";
    }
    
    if (paramsRequest.getUser().isSigned()) {
        if (null != request.getSession(false) &&
                request.getSession(false).getAttribute("isSocialNetUser") != null) {
            isSocialNetUser = Boolean.parseBoolean(
                    (String) request.getSession(false)
                            .getAttribute("isSocialNetUser"));
        }
    }
%>
        <script>
          (function(d, s, id){
             var js, fjs = d.getElementsByTagName(s)[0];
             if (d.getElementById(id)) {return;}
             js = d.createElement(s); js.id = id;
             js.src = "https://connect.facebook.net/es_MX/sdk.js";
             fjs.parentNode.insertBefore(js, fjs);
          }(document, 'script', 'facebook-jssdk'));
          window.fbAsyncInit = function() {
            FB.init({
              appId      : '<%=faceAppId%>',
              cookie     : true,
              xfbml      : true,
              version    : '<%=text.append(faceVersion)%>'
            });
            FB.getLoginStatus(function(response) {mystatusChangeCallback(response);});
          };
          function mystatusChangeCallback(response) {
<%
        //si no hay sesion con SWB:
        if (!paramsRequest.getUser().isSigned()) {
            //si hay sesion con FB, terminarla, no iniciarla automagicamente
            String sessionUrl = paramsRequest.getActionUrl().setAction("openSession")
                    .setCallMethod(SWBParamRequest.Call_DIRECT).toString();
%>
            console.log('Revisando status:.... ' + JSON.stringify(response));
            if (response.status && response.status === 'connected') {
              FB.logout();
            }
          }
          function openSWBSession() {
            FB.api('/me', function(response) {
              var name = response.name ? response.name : '';
              var faceId = response.id ? response.id : '';
              var email = response.email ? response.email : '';
              var xhttp = new XMLHttpRequest();
              xhttp.onreadystatechange = function() {
                if (this.readyState === 4 && this.status === 200) {
                  location.reload();
                }
              };
              xhttp.open("POST", "<%=sessionUrl%>", false); //false = sincrona
              xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
              xhttp.send("id=" + faceId + "&email=" + email + "&name=" + name + "&source=<%=SessionInitializer.FACEBOOK%>");
            });
          }
          function faceLogin() {
            FB.login(function(response) {
              console.log(JSON.stringify(response));
              if (response.authResponse) {
                openSWBSession();
              }
            });
          }
        </script>
        <div class="sesion dropdown show">
            <a class="" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" >
                <span class="ion-person"></span> <i><%=mainLabel%></i>
                <span class="ion-arrow-down-b"></span>
            </a>
<%
            String loginUrl = new StringBuilder(128).append(SWBPlatform.getContextPath())
                    .append("/login/")
                    .append(paramsRequest.getWebPage().getWebSiteId())
                    .append("/")
                    .append(paramsRequest.getWebPage().getId()).toString();
            String dialogTitle;
            try {
                dialogTitle = paramsRequest.getLocaleString("lbl_dialogTitle");
            } catch (SWBResourceException swbe) {
                dialogTitle = "Si tienes una cuenta, inicia ahora";
            }
            String userField;
            try {
                userField = paramsRequest.getLocaleString("lbl_userField");
            } catch (SWBResourceException swbe) {
                userField = "Usuario:";
            }
            String userplaceHldr;
            try {
                userplaceHldr = paramsRequest.getLocaleString("lbl_userplaceHldr");
            } catch (SWBResourceException swbe) {
                userplaceHldr = "nombre de usuario";
            }
            String pswdField;
            try {
                pswdField = paramsRequest.getLocaleString("lbl_pswdField");
            } catch (SWBResourceException swbe) {
                pswdField = "Contraseña:";
            }
            String pwdplaceHldr;
            try {
                pwdplaceHldr = paramsRequest.getLocaleString("lbl_pwdplaceHldr");
            } catch (SWBResourceException swbe) {
                pwdplaceHldr = "********";
            }
            String submitBtn;
            try {
                submitBtn = paramsRequest.getLocaleString("lbl_submitBtn");
            } catch (SWBResourceException swbe) {
                submitBtn = "Iniciar sesión";
            }
            String socialLogin;
            try {
                socialLogin = paramsRequest.getLocaleString("lbl_socialLogin");
            } catch (SWBResourceException swbe) {
                socialLogin = "Inicia con: ";
            }
%>
            <div class="sesiondisplay dropdown-menu" aria-labelledby="dropdownMenuLink">
                <p><%=dialogTitle%></p>
                <fieldset>
                    <form action="<%=loginUrl%>" method="post">
                      <div class="form-group">
                        <label for="wb_username"><%=userField%></label>
                        <input type="text" id="wb_username" class="form-control" name="wb_username" aria-describedby="emailHelp" placeholder="<%=userplaceHldr%>"/>
                      </div>
                      <div class="form-group">
                        <label for="wb_password"><%=pswdField%></label>
                        <input type="password" id="wb_password" name="wb_password" class="form-control" placeholder="<%=pwdplaceHldr%>"/>
                      </div>
<%--                    <div class="form-check">
                        <input type="checkbox" class="form-check-input" id="dropdownCheck" >
                        <label class="form-check-label" for="dropdownCheck">Recuérdame</label>
                      </div> --%>
                      <button type="submit" class="btn btn-negro"><%=submitBtn%></button>
<%--                    <p><a href="#">Olvidé mi usuario o contraseña</a></p> --%>
                      <hr>
                      <p><%=socialLogin%>
<%--
                      <div class="fb-login-button" data-max-rows="1" 
                     data-size="small" data-button-type="login_with"
                     data-show-faces="false" data-auto-logout-link="false" 
                     data-scope="public_profile,email" 
                     data-use-continue-as="false" onlogin="openSWBSession();"></div>  --%>
                        <a href="#" onclick="javascript:faceLogin();"><img src="/work/models/<%=paramsRequest.getWebPage().getWebSiteId()%>/img/icono-fb.png" ></a>
                      </p>
<%--
//                    <div class="nocuenta">
//                      <p>¿No tienes una cuenta?</p>
//                      <p><a href="#" class="btn" >Crea aquí tu cuenta</a></p>
//                    </div>  --%>
                  </form>
                </fieldset>
            </div>
        </div>
<%
        } else {  //si si existe el usuario en sesion de SWB
            if (isSocialNetUser) {
                //revisar que la sesion de la red social este activa tambien
                //esto es parte de la funcion mystatusChangeCallback
                String url = paramsRequest.getActionUrl().setAction("closeSession")
                        .setCallMethod(SWBParamRequest.Call_DIRECT).toString();
                String sessionAlert;
                try {
                    sessionAlert = paramsRequest.getLocaleString("msg_sessionAlert");
                } catch (SWBResourceException swbe) {
                    sessionAlert = "La sesión de Facebook ha terminado. Favor de iniciar sesión de nuevo.";
                }
%>
            console.log('response: ' + JSON.stringify(response));
            if (response.status && response.status !== 'connected') {
              //reenviar a seccion con id de usuario de facebook y crear sesion con SWB
              alert('<%=sessionAlert%>');
              closeSWBSession();
            }
          }

          function closeSWBSession() {
            FB.logout(function(response) {
              var xhttp = new XMLHttpRequest();
              xhttp.onreadystatechange = function() {
                if (this.readyState === 4 && this.status === 200) {
                  location.reload();
                }
              };
              xhttp.open("GET", "<%=url%>", false); //false = sincrona
              xhttp.send();
            });
<%
            }
            //dar la opcion a terminar la sesion
            try {
                mainLabel = paramsRequest.getLocaleString("lbl_out");
            } catch (SWBResourceException swbe) {
                mainLabel = "Terminar sesión";
            }
%>
          }
            </script>
            <div class="sesion">
<%
            if (isSocialNetUser) {
%>
                  <a class="" href="#" role="button" onclick="closeSWBSession();">
                    <span class="ion-person"></span><i> <%=mainLabel%></i>
                  </a>
<%
            } else {
                String logoutUrl = new StringBuilder()
                        .append(SWBPlatform.getContextPath())
                        .append("/login/")
                        .append(paramsRequest.getWebPage().getWebSiteId())
                        .append("/")
                        .append(paramsRequest.getWebPage().getId()).toString();
%>
                  <a class="" role="button" href="<%=logoutUrl%>?wb_logout=true">
                    <span class="ion-person"></span><i> <%=mainLabel%></i>
                  </a>
<%
            }
%>
            </div>
<%        }
%>
