package test;


import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet Filter implementation class MyExceptionFilter
 */
@WebFilter("/MyExceptionFilter")
public class MyExceptionFilter implements Filter {

	FilterConfig fConfig;
	private Logger mLog = LogManager.getLogger(MyExceptionFilter.class);
	
	public FilterConfig getfConfig() {
		return fConfig;
	}

	public void setfConfig(FilterConfig fConfig) {
		this.fConfig = fConfig;
	}	
	
    private void getInitParameterNames(){	
    }
    
	/**
     * Default constructor. 
     */
    public MyExceptionFilter() {
       
    }

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fchain) throws IOException, ServletException {
           	
		mLog.debug("Start MyExceptionFilter.doFilter(ServletRequest, ServletResponse, Filterchain)");

    	try{
    		fchain.doFilter(request, response);

    	} catch(Exception e){    
    		mLog.error(e );
        	ServletContext context = getfConfig().getServletContext();
    		request.setAttribute("context", context);
    		RequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/error.jsp");
    		rd.forward(request, response);
    	}
    	
		mLog.debug("End MyExceptionFilter.doFilter(ServletRequest, ServletResponse, Filterchain)");

    }
	
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		
		this.fConfig=fConfig;
		getInitParameterNames();
	}
    
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		fConfig = null;
	}
}
