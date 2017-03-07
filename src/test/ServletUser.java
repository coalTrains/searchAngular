package test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(urlPatterns="/ServletUser", loadOnStartup=1)

public class ServletUser extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Logger mLog = LogManager.getRootLogger();	

	 public	void init() {
	 }	
	
	public ServletUser() {
		super();
	}
	
	@Override
	protected void doGet(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		
		mLog.debug("start ServletUser.doGet(HttpServletRequest,HttpServletResponse)");
		UserBean lUserBeanUser= new UserBean();
		UserManager lUserManager = new UserManager();
		String lStrAction = "";
		lStrAction = pRequest.getParameter("action");
		
		JSONObject lJson = new JSONObject();
		JSONArray lJsonArray  = new JSONArray();
		JSONObject lJsonUser;
		
		if (StringUtils.isBlank(lStrAction)){
			RequestDispatcher lRequestDispatcher = pRequest.getRequestDispatcher("/WEB-INF/index.jsp");
            lRequestDispatcher.forward(pRequest,pResponse);
			}else {
		
		// Ricerca	
		if (lStrAction.equalsIgnoreCase("search")) {
			lUserManager = new UserManager();
			lUserBeanUser.setName(pRequest.getParameter("nome"));
			lUserBeanUser.setSurname(pRequest.getParameter("cognome"));  
			lUserBeanUser.setDate(pRequest.getParameter("data"));
			
			List <UserBean> lListusers = new ArrayList<UserBean>(lUserManager.getUsers(lUserBeanUser));
			
			try {
				for (UserBean pUserBean : lListusers ){
					lJsonUser = new JSONObject();
					lJsonUser.put("id", pUserBean.getId());
					lJsonUser.put("nome", pUserBean.getName());
					lJsonUser.put("cognome", pUserBean.getSurname());
					lJsonUser.put("data", pUserBean.getDate());
					lJsonArray.put(lJsonUser); 
				}
				lJson.put("pUsers", lJsonArray);
				pResponse.setContentType("application/json");
				pResponse.getWriter().write(lJsonArray.toString());
				
			} catch (Exception e){
				mLog.error(e);
				throw new MyException(e);
			}
	    }	

		mLog.debug("end ServletUser.doGet(HttpServletRequest,HttpServletResponse)");
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest pRequest, HttpServletResponse pResponse) throws ServletException, IOException {
		
		mLog.debug("start ServletUser.doPost(HttpServletRequest,HttpServletResponse)");
		
		UserBean lUserBean = new UserBean();
		UserManager lUserManager= new UserManager();
		String mStrAction;
		int mIntId=0;
		mStrAction = pRequest.getParameter("action");
		JSONObject lJson = new JSONObject();
		JSONObject lJsonUser;
		
		// Elimina 
		if (mStrAction.equalsIgnoreCase("delete")) {
			lUserManager = new UserManager();
			mIntId = Integer.parseInt(pRequest.getParameter("id"));
			lUserBean = lUserManager.deleteUser(mIntId);
			try{
				lJsonUser = new JSONObject();				
				lJsonUser.put("id", lUserBean.getId());
				lJsonUser.put("nome", lUserBean.getName());
				lJsonUser.put("cognome", lUserBean.getSurname());
				lJsonUser.put("data", lUserBean.getDate());
				
				lJson.put("deletedUser", lJsonUser);
				pResponse.setContentType("application/json");
				pResponse.getWriter().write(lJsonUser.toString());
				
			} catch(Exception e){
				mLog.error(e);
				throw new MyException(e);
			}

			
		// Aggiungi	
		} else if(mStrAction.equalsIgnoreCase("add")){   
				lUserManager=new UserManager();
				lUserBean.setName(pRequest.getParameter("name"));
				lUserBean.setSurname(pRequest.getParameter("surname"));
				lUserBean.setDate(pRequest.getParameter("date"));
				int lIntIndex = lUserManager.addUser(lUserBean);
				lUserBean.setId(lIntIndex);
				
				try{
					lJsonUser = new JSONObject();
					lJsonUser.put("id", lUserBean.getId());
					lJsonUser.put("nome", lUserBean.getName());
					lJsonUser.put("cognome", lUserBean.getSurname());
					lJsonUser.put("data", lUserBean.getDate());
					
					lJson.put("newUser", lJsonUser);
					pResponse.setContentType("application/json");
					pResponse.getWriter().write(lJsonUser.toString());
					
				} catch(Exception e){
					mLog.error(e);
					throw new MyException(e);
				}
				
		// Modifica
		}else if (mStrAction.equalsIgnoreCase("update")) {
			lUserManager = new UserManager();
			lUserBean.setId(Integer.parseInt(pRequest.getParameter("id")));
			lUserBean.setName(pRequest.getParameter("nome"));
			lUserBean.setSurname(pRequest.getParameter("cognome"));
			lUserBean.setDate(pRequest.getParameter("data"));
			UserBean lUserBeanOld = new UserBean();
			lUserBeanOld = lUserManager.updateUser(lUserBean);
			
			try{
				lJsonUser = new JSONObject();
				lJsonUser.put("id", lUserBeanOld.getId());
				lJsonUser.put("nome", lUserBeanOld.getName());
				lJsonUser.put("cognome", lUserBeanOld.getSurname());
				lJsonUser.put("data", lUserBeanOld.getDate());
				
				lJson.put("oldUser", lJsonUser);
				pResponse.setContentType("application/json");
				pResponse.getWriter().write(lJsonUser.toString());
				
			} catch(Exception e){
				mLog.error(e);
				throw new MyException(e);
			}
		}
		mLog.debug("end ServletUser.doPost(HttpServletRequest,HttpServletResponse)");
	}
}
