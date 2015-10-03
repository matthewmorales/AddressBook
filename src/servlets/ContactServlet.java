package servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entities.ContactEntity;


public class ContactServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ContactServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException{
    	try {
    		Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
    		throw new ServletException(e);
		}
    }

    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();

		if(pathInfo.equals("/all")){
			doGetAll(req, res);
		} else if(pathInfo.equals("/edit")){
			doGetEdit(req, res);
		} else{
			//throw an error or 404 here?
		}
	}
    
    @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String pathInfo =req.getPathInfo();
		if(pathInfo == "/new"){
			doPostNew(req, res);
		}
		else if(pathInfo == "/delete"){
			doPostDelete(req, res);
		}
		else if(pathInfo == "/save"){
			doPostEdit(req, res);
		} else{
			//throw 404 error here
		}
	}

	private void doPostNew(HttpServletRequest req, HttpServletResponse res) {
		res.setContentType("application/json");

	}

	private void doPostDelete(HttpServletRequest req, HttpServletResponse res) {
		res.setContentType("application/json");
		
	}

	private void doPostEdit(HttpServletRequest req, HttpServletResponse res) {
		res.setContentType("application/json");
		
	}
	
	private void doGetEdit(HttpServletRequest req, HttpServletResponse res) {
		res.setContentType("application/json");
		
	}

	private void doGetAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("application/json");
		JSONArray jsonArr = null;
		HashMap<String, String> params = new HashMap<String, String>();
		
		params.put("sorted_by", req.getParameter("sorted_by"));
		params.put("order", req.getParameter("order"));
		
		ContactEntity contact = new ContactEntity();
		try {
			jsonArr = contact.viewAllContacts(params);
		} catch (SQLException e) {
			JSONObject json = new JSONObject("{\"error\":\"something went wrong retrieving all records\"");
			res.getWriter().write(json.toString());
		}
		res.getWriter().write(jsonArr.toString());
	}
}
