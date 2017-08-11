package controller;

import java.beans.Statement;
import java.io.*;
import java.util.*;
import javax.faces.context.*;
import javax.servlet.http.HttpServletRequest;

import application.AppBean;
import application.ErrorMessageContainer;
import application.Monitor;
import beans.*;
import beans.scripts.*;
import util.*;

public class AjaxController {
	public static final String REQPARAM_USER = "user_id";
	public static final String REQPARAM_USER_EXT = "userid_ext"; //an external userId (from another system)
	public static final String REQPARAM_SCRIPT = "script_id";
	public static final String REQPARAM_VP = "vp_id";
	public static final String REQPARAM_LOC = "locale";
	public static final String REQPARAM_SCRIPTLOC = "script_locale";
	public static final String REQPARAM_STAGE = "stage";
	public static final String REQPARAM_CHARTTYPE = "chart_type";	//see LearningAnalyticsContainer
	public static final String REQPARAM_CHARTSIZE = "chart_size"; //sm or lg
	public static final String REQPARAM_SEARCHTERM = "search";	//searchterm
	public static final String REQPARAM_SECRET = "secret";	//a shared secret is included in the query
	public static final String REQPARAM_ENCODED = "encoded"; //are the query params encoded (true | false)
	public static final String REQPARAM_SYSTEM = "system_id"; //are the query params encoded (true | false)
	public static final String REQPARAM_CHARTPEER = "chart_peer"; //are the query params encoded (true | false)
	public static final String REQPARAM_EXTUID = "uid"; //are the query params encoded (true | false)
	public static final String REQPARAM_REPORTS_VP = "r_vp_id"; //are the query params encoded (true | false)
	public static final String REQPARAM_REPORTS_SCRIPT_ID = "r_scriptid"; //are the query params encoded (true | false)
	public static final String REQPARAM_EXP_FB_MODE = "expfbmode";
	public static final String REQPARAM_PEER_FB_MODE = "peerfbmode";
	public static final String REQPARAM_DDX_MODE = "ddx_mode";

	public static final String REQPARAM_API = "api";
	public static final String REQPARAM_MAXSTAGE = "maxstage";
	public static final String REQPARAM_VP_NAME = "vp_name";


	static private AjaxController instance = new AjaxController();
	static public AjaxController getInstance() { return instance; }
	
	/**
	 * Receive an ajax call and process it. 
	 * params: type = method to call, id=id to add/remove...
	 * @param patillscript
	 * @throws IOException
	 */
	public void receiveAjax(PatientIllnessScript patillscript) throws IOException {
		ExternalContext externalContext = FacesContextWrapper.getCurrentInstance().getExternalContext();

	    Map<String, String> reqParams = externalContext.getRequestParameterMap();
	    if(reqParams!=null){
	    	String patillscriptId = reqParams.get(REQPARAM_SCRIPT);
	    	if(patillscript==null && patillscriptId!=null){ //could be a timeout 
	    		//long patIllScriptId = Long.parseLong(patillscriptId);
	    		new NavigationController().getMyFacesContext().initSession();
	    	}
	    	if(patillscript==null || patillscriptId==null|| Long.parseLong(patillscriptId)!=patillscript.getId()){
	    		
	    		CRTLogger.out("Error: patillscriptId is:"+ patillscriptId + " is null", CRTLogger.LEVEL_ERROR);
	    		return; //TODO we need some more error handling here, how can this happen? What shall we do? 
	    	}
	    	String methodName = reqParams.get("type");
	    	String idStr = reqParams.get("id");
	    	String nameStr = reqParams.get("name"); //the list entry
	    	String orgNameStr = reqParams.get("orgname"); //what the learner has typed in
	    	String x = reqParams.get("x"); //either x-position of an item or startEpId
	    	String y = reqParams.get("y"); //either y-position of an item or targetEpId
	    	patillscript.updateStage(reqParams.get(REQPARAM_STAGE));

	    	//String patIllScriptId = reqParams.get(REQPARAM_SCRIPT); //TODO check whether belongs to currently loaded script!
	    	Statement stmt; 
	    	if(x!=null && !x.trim().equals("")){
	    		stmt = new Statement(patillscript, methodName, new Object[]{idStr, nameStr,x,y});
	    	}
	    	else if(nameStr!=null && !nameStr.trim().equals("")) 
	    		stmt = new Statement(patillscript, methodName, new Object[]{idStr, nameStr});
	    	else stmt = new Statement(patillscript, methodName, new Object[]{idStr});
	    	
	    	try {
				stmt.execute();				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_PROD);
			}
	    	patillscript.toString();
    		
	    	//use this if making a call that returns an object in a template:
	    	/*Object o = 	((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getAttribute("prob");
	    	if(o==null) o = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getAttribute("ddx");
	    	//if o is null, we assume that an error occured and we have to give back the error messages!!!
	    	if(methodName.startsWith("add") && o!=null){
	    		responseAjaxTemplate(externalContext, reqParams.get("id"), patillscript);
	    	}

	    	else*/ responseAjax(externalContext, reqParams.get("id"));
	    }
	}
	
	/**
	 * Called for chart ajax calls...
	 * @param context
	 */
	public void receiveChartAjax(CRTFacesContext context) throws IOException{
		ExternalContext externalContext = FacesContextWrapper.getCurrentInstance().getExternalContext();
		Map<String, String> reqParams = externalContext.getRequestParameterMap();
		String methodName = reqParams.get("type");
		String idStr = reqParams.get("id");
    	Statement stmt = new Statement(context.getLearningAnalyticsContainer(), methodName, new Object[]{idStr});
    	
    	try {
			stmt.execute();				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
    	responseAjax(externalContext, reqParams.get("id"));

	}
	
	public void receiveMonitorAjax(Monitor m) throws IOException{
		ExternalContext externalContext = FacesContextWrapper.getCurrentInstance().getExternalContext();
		Map<String, String> reqParams = externalContext.getRequestParameterMap();
		String methodName = reqParams.get("type");
		String idStr = reqParams.get("id");
    	Statement stmt = new Statement(m, methodName, new Object[]{idStr});
    	
    	try {
			stmt.execute();				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
    	responseAjax(externalContext, reqParams.get("id"));

	}
	
	public void receiveResportsAjax(ReportBean b) throws IOException{
		ExternalContext externalContext = FacesContextWrapper.getCurrentInstance().getExternalContext();
		Map<String, String> reqParams = externalContext.getRequestParameterMap();
		String methodName = reqParams.get("type");
		String idStr = reqParams.get(REQPARAM_REPORTS_VP);
    	Statement stmt = new Statement(b, methodName, new Object[]{idStr});
    	
    	try {
			stmt.execute();				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
    	responseAjax(externalContext, reqParams.get(REQPARAM_REPORTS_VP));

	}
	
	/**
	 * Receive an ajax call and process it. 
	 * params: type = method to call, id=id to add/remove...
	 * @param patillscript
	 * @throws IOException
	 */
	public void receiveAjax(CRTFacesContext crContext) throws IOException {
		ExternalContext externalContext = FacesContextWrapper.getCurrentInstance().getExternalContext();
	    //ExternalContext externalContext = facesContext2.getExternalContext();
	    //ExternalContext ec = fcw.getExternalContext();
	   // Map<String, Object> sessionMap = externalContext.getSessionMap().get("patIll);
	    Map<String, String> reqParams = externalContext.getRequestParameterMap();
	    if(reqParams!=null){
	    	String patillscriptId = reqParams.get(REQPARAM_SCRIPT);
	    	if(crContext==null || patillscriptId==null) {
	    		CRTLogger.out("Error: receiveAjax", CRTLogger.LEVEL_PROD);
	    		return; //TODO we need some more error handling here, how can this happen? What shall we do? 
	    	}
	    	if(!patillscriptId.equals("-99") && (crContext.getPatillscript()==null || Long.parseLong(patillscriptId)!=crContext.getPatillscript().getId())){
	    		CRTLogger.out("Error: receiveAjax", CRTLogger.LEVEL_PROD);
	    		return;
	    	}
	    	String methodName = reqParams.get("type");
	    	String idStr = reqParams.get("id");
	    	String nameStr = reqParams.get("name");
	    	String x = reqParams.get("x");
	    	String y = reqParams.get("y");
	    	//patillscript.updateStage(reqParams.get(REQPARAM_STAGE));

	    	//String patIllScriptId = reqParams.get(REQPARAM_SCRIPT); //TODO check whether belongs to currently loaded script!
	    	Statement stmt; 
	    	if(x!=null && !x.trim().equals("")){
	    		stmt = new Statement(crContext, methodName, new Object[]{idStr, nameStr,x,y});
	    	}
	    	else if(nameStr!=null && !nameStr.trim().equals("")) 
	    		stmt = new Statement(crContext, methodName, new Object[]{idStr, nameStr});
	    	else stmt = new Statement(crContext, methodName, new Object[]{idStr});
	    	
	    	try {
				stmt.execute();				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_PROD);
			}
	    	//patillscript.toString();
	    	responseAjax(externalContext, reqParams.get("id"));
	    }
	}
	
	/**
	 * handles the ajax response (xml)
	 * @param externalContext
	 * @throws IOException
	 */
	private void responseAjax(ExternalContext externalContext, String responseId) throws IOException{
		FacesContext facesContext = FacesContext.getCurrentInstance();
	    externalContext.setResponseContentType("text/xml");
	    externalContext.setResponseCharacterEncoding("UTF-8"); 
	    //for additional id info (e.g. when adding a new connection)
		String id2 = (String) ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getAttribute("id2");
		StringBuffer sb = new StringBuffer("<id>"+responseId+"</id>");
		if(id2!=null && !id2.trim().isEmpty())
				sb.append("<id2>"+id2+"</id2>");
	    externalContext.getResponseOutputWriter().write(createResponseXML(sb.toString()));
		

	    facesContext.responseComplete();
	}
	
	/**
	 * handles the ajax response (xml) and returns an xml file with the filled subtemplate for the 
	 * created object
	 * @param externalContext
	 * @throws IOException
	 */
	private void responseAjaxTemplate(ExternalContext externalContext, String responseId, PatientIllnessScript patillscript) throws IOException{
		//FacesContext facesContext = FacesContext.getCurrentInstance();
		//List msgs = null;
		//externalContext.setResponseContentType("text/xml");
	    //externalContext.setResponseCharacterEncoding("UTF-8");    
	    
	    try {
			////String template = "/src/html/probbox2.xhtml";
			
			//HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
			//HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
			//TemplateResponseWriter customResponse  = new TemplateResponseWriter(response);
			//facesContext.getExternalContext().dispatch(template);
			//msgs = facesContext.getMessageList();
			//request.getRequestDispatcher(template).forward(request, customResponse);

			//System.out.println(String.format("The output of %s is %s", template, customResponse.getOutput()));
			//String respXml = createResponseXML("", false);
			//String respXml = createResponseXML(customResponse.getOutput(), false);
			//FacesContext.getCurrentInstance().
			//externalContext.getResponseOutputWriter().write(respXml);
			//externalContext.getResponseOutputWriter().write("hallo");
			//FacesContext.getCurrentInstance().getExternalContext().getFlash().clear();
		} 
	    catch (Exception e) {
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}	    	   
	    //facesContext.responseComplete();
	}
	
	
	private String createResponseXML(String responseParam){
		return createResponseXML(responseParam, true);
	}
	/**
	 * assemble the xml response, include the id and if applicable a message.
	 * @param responseParam
	 * @return
	 */
	private String createResponseXML(String responseParam, boolean includeMsg){
		
		//TODO: we might want to add more info to the response....
		StringBuffer xmlResponse = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response>");
		xmlResponse.append(responseParam);
		System.out.println(xmlResponse.toString());
		if(includeMsg) appendMessage(xmlResponse);
		xmlResponse.append("</response>");
		return xmlResponse.toString();
	}
	
	/**
	 * If e.g. an error has occurred we get the message from the FacesContext and include it into the xml response.
	 * @param xmlResponse
	 */
	private void appendMessage(StringBuffer xmlResponse){
		new ErrorMessageContainer().toXml(xmlResponse);
	}
	
	public String getRequestParamByKey(String key){
		if(key==null || key.equals("")) return null;
		key = key.trim();
		//key = key.toLowerCase();
		Map<String,String[]> p = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
		String[] p1 = p.get(key);
		
		if (p1 != null && p1.length>0){
			String param = Encoder.getInstance().decodeQueryParam(p1[0]);
			return param;
		}
		return null;
	}
	
	public String getRequestParamByKeyNoDecrypt(String key){
		if(key==null || key.equals("")) return null;
		key = key.trim();
		//key = key.toLowerCase();
		Map<String,String[]> p = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
		String[] p1 = p.get(key);
		if(p1==null || p1.length==0) return null;
		
			return p1[0];
			//return param;
		//}
		//return null;
	}
	
	public long getLongRequestParamByKey(String key){
		String val = getRequestParamByKey(key);
		if(val!=null && !val.trim().equals("")){
			return Long.parseLong(val);
		}
		return -1;
	}
	
	public boolean getBooleanRequestParamByKey(String key, boolean defVal){
		String val = getRequestParamByKey(key);
		if(val!=null && !val.trim().equals("")){
			return Boolean.parseBoolean(val);
		}
		return defVal;
	}
	
	public int getIntRequestParamByKey(String key, int defVal){
		String val = getRequestParamByKey(key);
		try{
			if(val!=null && !val.trim().equals("") && !val.equals("null"))
				return Integer.parseInt(val);
		}
		catch(Exception e){
			CRTLogger.out(StringUtilities.stackTraceToString(e), CRTLogger.LEVEL_ERROR);
		}
		return defVal;
	}
	
	
	/**
	 * Is the given sharedSecret the same as the application's (comparison is case insensitive!)
	 * @param secret
	 * @return
	 */
	public boolean isValidSharedSecret(String secret){
		if(secret==null || secret.trim().equals("")) return false;
		if(secret.equalsIgnoreCase(AppBean.getSharedSecret())) return true;
		return false;
	}

	
	/**
     * Wraps Response for BufferedResponseFilter
     */
    /*public class BufferResponserTest implements HttpServletResponse {
    	ServletOutputStream out = null;
    	
        public BufferResponserTest() {
            
        }
        
        
        public String getContentType() {
			return "text/html";
		}


		public void setCharacterEncoding(String arg0) {
		}


        public void addCookie(Cookie arg0) {
            
        }

        public void addDateHeader(String arg0, long arg1) {
            
        }

        public void addHeader(String arg0, String arg1) {
            
        }


        public void addIntHeader(String arg0, int arg1) {
            
        }

        public boolean containsHeader(String arg0) {
            return false;
        }

        public String encodeRedirectUrl(String arg0) {
            return null;
        }

        public String encodeRedirectURL(String arg0) {
            return null;
        }

        public String encodeUrl(String arg0) {
            return null;
        }

        public String encodeURL(String arg0) {
            return null;
        }
        public void sendError(int arg0, String arg1) throws IOException {
            
        }

        public void sendError(int arg0) throws IOException {
            
        }

        public void sendRedirect(String arg0) throws IOException {
            
        }

        public void setDateHeader(String arg0, long arg1) {
            
        }

        public void setHeader(String arg0, String arg1) {
            
        }

        public void setIntHeader(String arg0, int arg1) {
            
        }

        public void setStatus(int arg0, String arg1) {
            
        }


        public void setStatus(int arg0) {
            
        }

        public void flushBuffer() throws IOException {
            
        }
        public int getBufferSize() {
            return 0;
        }

        public String getCharacterEncoding() {
            return null;
        }

        public Locale getLocale() {
            return null;
        }


        public ServletOutputStream getOutputStream() throws IOException {
        	if (out != null) return out;
            return new BufferResponseStreamTest();
        }
        
 
        @SuppressWarnings("unused")
		public void setOutputStream(ServletOutputStream in_out) throws IOException {
            out = in_out;
        }
  
        public PrintWriter getWriter() throws IOException {
            return null;
        }

        public boolean isCommitted() {
            return false;
        }

        public void reset() {
            
        }

        public void resetBuffer() {
            
        }

        public void setBufferSize(int arg0) {
            
        }

        public void setContentLength(int arg0) {
            
        }
        public void setContentType(String arg0) {
            
        }

        public void setLocale(Locale loc) {
        	FacesContext facesContext = FacesContext.getCurrentInstance();
        	facesContext.getViewRoot().setLocale(loc);
        }


		public String getHeader(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		public Collection<String> getHeaderNames() {
			// TODO Auto-generated method stub
			return null;
		}

		public Collection<String> getHeaders(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public int getStatus() {
			// TODO Auto-generated method stub
			return 0;
		}

    
        
    }*/
    /**
     * Wraps Response for BufferedResponseFilter
     */
  /*  public class BufferResponseWrapper extends HttpServletResponseWrapper {
        protected HttpServletResponse origResponse = null;
        protected ServletOutputStream stream = null;
        protected PrintWriter writer = null;
        protected int error = 0;
        
        public BufferResponseWrapper(HttpServletResponse response) {
            super(response);
            origResponse = response;
        }
        
        public ServletOutputStream createOutputStream() throws IOException {
            return (new BufferResponseStream(origResponse));
        }
        
        public void finishResponse() {
            try {
                if (writer != null) {
                    writer.close();
                    writer = null;
                } else {
                    if (stream != null) {
                        stream.close();
                        stream = null;
                    }
                }
            } catch (IOException e) {
            }
        }
        
        public void flushBuffer() throws IOException {
            if (stream != null) {
                stream.flush();
            }
        }
        
        public ServletOutputStream getOutputStream() throws IOException {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called!");
            }
            
            if (stream == null) {
                stream = createOutputStream();
            }
            
            return (stream);
        }
        
        public PrintWriter getWriter() throws IOException {
            // From cmurphy@@intechtual.com to fix:
            // https://appfuse.dev.java.net/issues/show_bug.cgi?id=59
            if (this.origResponse != null && this.origResponse.isCommitted()) {
                return super.getWriter();
            }
            
            if (writer != null) {
                return (writer);
            }
            
            if (stream != null) {
                throw new IllegalStateException("getOutputStream() has already been called!");
            }
            
            stream = createOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(stream, origResponse.getCharacterEncoding()));          
            return (writer);
        }
        
        public void setContentLength(int length) {
        }
        
        public void sendError(int error1, String message) throws IOException {
            super.sendError(error1, message);
            this.error = error1;
        }
    }*/
    
    /**
     * Wraps Response Stream for BufferedResponseFilter
     */
   /* public class BufferResponseStreamTest extends ServletOutputStream {
    	OutputStream out = null;

        public void close() throws IOException {
            
        }

        public void flush() throws IOException {
            
        }
        public void write(byte[] b, int off, int len) throws IOException {
            if (out != null) out.write(b, off, len);
        } 
        public void write(byte[] b) throws IOException {
        	if (out != null) out.write(b);
        }
        public void write(int b) throws IOException {
        	if (out != null) out.write(b);
        }

		public OutputStream getOut() {
			return out;
		}

		public void setOut(OutputStream out) {
			this.out = out;
		}        
    }*/
    
    /**
     * Wraps Response Stream for BufferedResponseFilter
     */
    /*public class BufferResponseStream extends ServletOutputStream {
        // abstraction of the output stream used for compression
        protected ByteArrayOutputStream bufferedOutput = null;
        
        // state keeping variable for if close() has been called
        protected boolean closed = false;
        
        // reference to original response
        protected HttpServletResponse response = null;
        
        // reference to the output stream to the client's browser
        protected ServletOutputStream output = null;
        
        protected Exception closeX = null;
        
        
        @SuppressWarnings("unused")
		public BufferResponseStream(HttpServletResponse response)
        throws IOException {
            super();
            closed = false;
            this.response = response;
            this.output = null;
            bufferedOutput = new ByteArrayOutputStream(2048);
        }
        
        public void close() throws IOException {	
        	
            // verify the stream is yet to be closed
            if (closed) {
                
                throw new IOException("This output stream has already been closed");
            }
            
            closeX = new Exception("BufferResponseStream.close()");         
            try {
            // set appropriate HTTP headers
                response.setContentLength(bufferedOutput.toByteArray().length);
                response.getOutputStream().write(bufferedOutput.toByteArray());
                //this.response.getWriter().print(bufferedOutput.toString(this.response.getCharacterEncoding()));
                //System.err.println("BufferResponseStream.close() write " + bufferedOutput.toString());
                output.flush();
                output.close();
                closed = true;
            }
            catch(Exception x) {
                
            }
            finally {
                try {
                    bufferedOutput.close();
                }
                catch (Throwable th) {
                	
                }
                finally {
                    
                }

                bufferedOutput = null;
            }
        }
        
   	public  String stackTraceToString(Throwable throwable)
   	{
   		StringWriter stack;	// tmp
   			
   		stack = new StringWriter();
   		throwable.printStackTrace(new PrintWriter(stack));
   		
   		String s = stack.toString();
   		stack = null;

   		return s;
   		
   	}
        
        public void flush() throws IOException {
            if (closed) {
                throw new IOException("Cannot flush a closed output stream: closeX:" + closeX != null ? this.stackTraceToString(closeX) : "null");
            }
            
            bufferedOutput.flush();
        }
        
        public void write(int b) throws IOException {
            if (closed) {
                throw new IOException("Cannot write to a closed output stream");
            }
            
            // make sure we aren't over the buffer's limit
            checkBufferSize(1);
            
            // write the byte to the temporary output
            bufferedOutput.write((byte) b);
        }
        
        @SuppressWarnings("unused")
		private void checkBufferSize(int length) throws IOException {
            // check if we are buffering too large of a file
        }
        
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }
        
        public void write(byte[] b, int off, int len) throws IOException {
            
            if (closed) {
                throw new IOException("Cannot write to a closed output stream");
            }
            
            // make sure we aren't over the buffer's limit
            checkBufferSize(len);
            
            // write the content to the buffer
            bufferedOutput.write(b, off, len);
        }
        
        public boolean closed() {
            return (this.closed);
        }
        
        public void reset() {
            //noop
        }
    }*/
}
