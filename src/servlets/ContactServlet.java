package servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;
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
		} else{
			res.setContentType("application/json");
			JSONObject json = new JSONObject("{\"error\":\"The path " + pathInfo + " does not exist on this server.\"}");
			res.getWriter().write(json.toString());	
		}
	}
    
    @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String pathInfo =req.getPathInfo();
		if(pathInfo.equals("/new")){
			doPostNew(req, res);
		}
		else if(pathInfo.equals("/delete")){
			doPostDelete(req, res);
		}
		else if(pathInfo.equals("/save")){
			doPostEdit(req, res);
		} else{
			res.setContentType("application/json");
			JSONObject json = new JSONObject("{\"error\":\"The path " + pathInfo + " does not exist on this server.\"}");
			res.getWriter().write(json.toString());	
		}
	}

	private void doPostNew(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("application/json");
		JSONObject jsonResponse;
		ContactEntity contact = new ContactEntity();

		setContactValues(req, contact);
		
		try {
			jsonResponse = contact.createNewContact();
		} catch (SQLException e) {
			jsonResponse = new JSONObject("{\"error\": \"SQL Error\"");
			e.printStackTrace();
		}
		
		res.getWriter().write(jsonResponse.toString());
	}

	private void doPostDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("application/json");
		JSONObject jsonResponse;
		Integer id;
		boolean loaded = false;
		
		id = Integer.parseInt(req.getParameter("id"));
		
		ContactEntity contact = new ContactEntity();
		contact.setId(id);
		
		try {
			loaded = contact.loadEntity();
		} catch (SQLException e) {
			loaded = false;
			jsonResponse = new JSONObject("{\"error\":\"An error occured loading the contact, it may no longer exist in the databse. Refresh the page and try again.\"}");
		}
		
		if(loaded){
			try {
				jsonResponse = contact.deleteEntity();
			} catch (SQLException e) {
				jsonResponse = new JSONObject("{\"error\":\"there was an error deleting the contact.\"}");
			}
		} else {
			jsonResponse = new JSONObject("{\"error\":\"An error occured deleting the contact, it may no longer exist in the databse. Refresh the page and try again.\"}");
		}
		res.getWriter().write(jsonResponse.toString());	
	}

	private void doPostEdit(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("application/json");
		JSONObject jsonResponse = null;
		Integer id;
		boolean loaded = false;
		
		id = Integer.parseInt(req.getParameter("id"));
		ContactEntity contact = new ContactEntity();
		contact.setId(id);
		
		try {
			loaded = contact.loadEntity();
		} catch (SQLException e) {
			loaded = false;
			jsonResponse = new JSONObject("{\"error\":\"An error occured loading the contact, it may no longer exist in the databse. Refresh the page and try again.\"}");
		}
		
		if(loaded){
			setContactValues(req, contact);
			try {
				jsonResponse = contact.save();
			} catch (SQLException e) {
				jsonResponse = new JSONObject("{\"error\":\"Something went wrong saving the contact\"}");
			}
		}
		res.getWriter().write(jsonResponse.toString());	
	}

	private void doGetAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setContentType("application/json");
		JSONArray jsonArr = null;
		HashMap<String, String> params = new HashMap<String, String>();
		
		params.put("sorted_by", req.getParameter("sorted_by"));
		params.put("order", req.getParameter("order"));
		//ADD PARAMETER TO SELECT ENTRIES BY CRITERIA
		params.put("search_category", req.getParameter("search_category"));
		params.put("search_term", req.getParameter("search_term"));
		
		ContactEntity contact = new ContactEntity();
		try {
			jsonArr = contact.viewAllContacts(params);
		} catch (SQLException e) {
			JSONObject json = new JSONObject("{\"error\":\"something went wrong retrieving all records\"");
			res.getWriter().write(json.toString());
		}
		res.getWriter().write(jsonArr.toString());
	}

	private void setContactValues(HttpServletRequest req, ContactEntity contact){
		Enumeration<String> params = req.getParameterNames();
		String param = null;
		
		while(params.hasMoreElements()){
			//doing this so that all parameters do not need to be passed from the front end, just the ones to be edited/saved.
			param = params.nextElement().toString();
			switch(param.toLowerCase()){
			case "first_name": 	contact.setFirstName(req.getParameter(param));
				break;
			case "last_name": 	contact.setLastName(req.getParameter(param));
				break;
			case "phone_number":contact.setPhoneNumber(req.getParameter(param));
				break;
			case "city":contact.setCity(req.getParameter(param));
				break;
			case "state":contact.setState(req.getParameter(param));
				break;
			case "street_name":contact.setStreetName(req.getParameter(param));
				break;
			case "email_address":contact.setEmailAddress(req.getParameter(param));
				break;
			case "location":contact.setLocation(req.getParameter(param));
				break;
			case "street_number":contact.setStreetNumber(req.getParameter(param));
				break;
			case "zip_code":contact.setZipCode(req.getParameter(param));
				break;
			case "id": break; //don't really care about the id param for setting contact variables. If editing, id is already set. If creating new contact, id is automatically set.
			default: System.out.println("Invalid Parameter Entered: " + param);
				break;
			}
		}
	}
}
