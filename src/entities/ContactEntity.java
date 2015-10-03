package entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class ContactEntity extends Entity {
	private final static String table = "contact_info";
	public ContactEntity(){
		//super();
	}
	
	public JSONArray viewAllContacts(HashMap<String, String> params) throws SQLException{
		PreparedStatement  pstmt = null;
		Connection conn = getDBConnection();
		JSONArray jsonArr = new JSONArray();
		String sql = null;
		
		if(params.get("sorted_by") != null && params.get("order") != null){
			sql = String.format("SELECT * FROM %s ORDER BY %s %s", table, params.get("sorted_by"), params.get("order"));
		} else{
			sql = "SELECT * FROM contact_info";			
		}
		
		try{
			//execute Select statement
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			//build json Objects, append to jsonArr
			while(rs.next()){
				StringBuilder sb = new StringBuilder();
				sb.append("{ ");
				sb.append("\"first_name\":\"" + rs.getString("first_name") + "\", ");
				sb.append("\"last_name\":\"" + rs.getString("last_name") + "\", ");
				sb.append("\"phone_number\":\"" + rs.getString("phone_number") + "\", ");
				sb.append("\"city\":\"" + rs.getString("city") + "\", ");
				sb.append("\"state\":\"" + rs.getString("state") + "\", ");
				sb.append("\"street_number\":\"" + rs.getString("street_number") + "\", ");
				sb.append("\"street_name\":\"" + rs.getString("street_name") + "\", ");
				sb.append("\"zip_code\":\"" + rs.getString("zip_code") + "\", ");
				sb.append("\"email_address\":\"" + rs.getString("email_address") + "\"");
				sb.append(" }");
				
				JSONObject json = new JSONObject(sb.toString());
				jsonArr.put(json);
			}
		} catch(SQLException e){
			e.printStackTrace();
		} finally{
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();
			}		
		}
		//System.out.println(jsonArr);
		return jsonArr;
	}	
}
