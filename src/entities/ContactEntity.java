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
	
	private String firstName;
	private String lastName;
	private String phoneNum;
	private String city;
	private String state;
	private Integer streetNum;
	private String streetName;
	private Integer zipCode;
	private String emailAddress;
	private String location;
	private Integer id;
	
	public ContactEntity(){
		//super();
	}
	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = (id == null) ? this.id : id;
		
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = (firstName == null) ? this.firstName : firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return this.phoneNum;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNum = phoneNumber;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getStreetNumber() {
		return this.streetNum;
	}

	public void setStreetNumber(String streetNumber) {
		try{
			this.streetNum = (streetNumber == null || streetNumber.isEmpty()) ? null : Integer.parseInt(streetNumber);
		} catch(NumberFormatException e) {
			this.streetNum = null;
		}
	}

	public String getStreetName() {
		return this.streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public Integer getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(String zipCode) {
		try{
			this.zipCode = (zipCode == null || zipCode.isEmpty()) ? null : Integer.parseInt(zipCode);
		} catch(NumberFormatException e) {
			this.zipCode = null;
		}
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public boolean loadEntity() throws SQLException{
		boolean loaded = false;
		PreparedStatement  pstmt = null;
		Connection conn = null;
		String sql = null;
		
		sql = "SELECT first_name, last_name, phone_number, "
				+ "city, state, street_number, street_name, "
				+ "zip_code, email_address, location, id "
				+ "FROM " + table + " WHERE id = ?";
		
		try{
			conn = getDBConnection();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, this.id);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){
				this.firstName = rs.getString("first_name");
				this.lastName = rs.getString("last_name");
				this.phoneNum = rs.getString("phone_number");
				this.city = rs.getString("city");
				this.state = rs.getString("state");
				this.streetName = rs.getString("street_name");
				this.emailAddress = rs.getString("email_address");
				this.location = rs.getString("location");
				this.id = rs.getInt("id");
				
				//loading null Int seems to set this.zip_cod and this.streetNum to 0, thus saving incorrectly.
				//these conditionals set zip and streetNum to null instead of the meaningless (and incorrect) "0"
				if(rs.getInt("zip_code") == 0){
					this.zipCode = null;
				} else{
					this.zipCode = rs.getInt("zip_code");
				}
				if(rs.getInt("street_number") == 0){
					this.streetNum = null;
				} else {
					this.streetNum = rs.getInt("street_number");
				}
				
				loaded = true;
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

		return loaded;
	}

	public JSONArray viewAllContacts(HashMap<String, String> params) throws SQLException{
		PreparedStatement  pstmt = null;
		Connection conn = null;
		JSONArray jsonArr = new JSONArray();
		String sql = null;
		
		//if sorted keys exist, perform a sorted search
		if(params.get("sorted_by") != null && params.get("order") != null){
			sql = String.format("SELECT * FROM %s ORDER BY %s %s", table, params.get("sorted_by"), params.get("order"));
		} else{ //otherwise just return the unsorted list
			sql = "SELECT * FROM contact_info";			
		}

		try{
			//execute Select statement
			conn = getDBConnection();
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
				sb.append("\"email_address\":\"" + rs.getString("email_address") + "\",");
				sb.append("\"location\":\"" + rs.getString("location") + "\",");
				sb.append("\"id\":\"" + rs.getString("id") + "\"");
				sb.append(" }");
				JSONObject json = new JSONObject(sb.toString());
				//compar json values to search terms. If nothing matches, don't add to jsonArr
				if(params.get("search_term") != null && params.get("search_category") != null){
					String searchCategory 	= json.getString(params.get("search_category")).toLowerCase();
					String searchFilter 	= params.get("search_term").toLowerCase();
					if(searchCategory.contains(searchFilter)){
						jsonArr.put(json);
					}
				} else {
					jsonArr.put(json);
				}
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
	
	public JSONObject createNewContact() throws SQLException{
		PreparedStatement pstmt = null;
		Connection conn = null;
		JSONObject json;
		String sql = "INSERT INTO "
				+ table
				+ "(first_name, last_name, phone_number, city, state, street_number, street_name, "
				+ "zip_code, email_address, location ) VALUES"
				+ "(?,?,?,?,?,?,?,?,?,?)";
		try{
			conn = getDBConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, firstName);
			pstmt.setString(2, lastName);
			pstmt.setString(3, phoneNum);
			pstmt.setString(4, city);
			pstmt.setString(5, state);
			pstmt.setString(7, streetName);
			pstmt.setString(9, emailAddress);
			pstmt.setString(10, location);
			
			if(streetNum == null){
				pstmt.setNull (6, java.sql.Types.INTEGER);
			} else{
				pstmt.setInt(6, streetNum);
			}
			if(zipCode == null){
				pstmt.setNull (8, java.sql.Types.INTEGER);
			} else{
				pstmt.setInt(8, streetNum);
			}
			
			//perform insert
			pstmt.executeUpdate();
			json = new JSONObject("{\"success\": \"" + firstName + " " + lastName +" successfully added to address book\"}");
		} catch (SQLException e){
			e.printStackTrace();
			json = new JSONObject("{\"error\":\"SQL Error\"}");
		} finally{
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}	
		return json;
	}

	
	public JSONObject deleteEntity() throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		JSONObject json = null;

		String sql = "DELETE FROM contact_info WHERE id = ?";

		try {
			conn = getDBConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, this.id);

			pstmt.executeUpdate();
			json = new JSONObject("{\"success\":\"The contact " + this.firstName +" "+ this.lastName + " was successfully deleted\"}");
		} catch (SQLException e) {
			json = new JSONObject("{\"error\":\"There was an error deleting the record.\"}");
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return json;
	}

	public JSONObject save() throws SQLException {
		PreparedStatement pstmt = null;
		Connection conn = null;
		JSONObject json;
		String sql = "INSERT INTO "
				+ table
				+ "(first_name, last_name, phone_number, city, state, street_number, street_name, "
				+ "zip_code, email_address, location ) VALUES"
				+ "(?,?,?,?,?,?,?,?,?,?)";
		
		sql = "UPDATE contact_info SET first_name = ?, "
				+ "last_name = ?, "
				+ "phone_number = ?, "
				+ "city = ?, "
				+ "state = ?, "
				+ "street_number = ?, "
				+ "street_name = ?, "
				+ "zip_code = ?, "
				+ "email_address = ?, "
				+ "location = ? "
                + "WHERE id = ?";
		
		if(phoneNum.isEmpty() || phoneNum == null){
			json = new JSONObject("{\"failure\":\"A contact must have a phone number \"}");
			return json;
		}
		
		try{
			conn = getDBConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, firstName);
			pstmt.setString(2, lastName);
			pstmt.setString(3, phoneNum);
			pstmt.setString(4, city);
			pstmt.setString(5, state);
			pstmt.setString(7, streetName);
			pstmt.setString(9, emailAddress);
			pstmt.setString(10, location);
			pstmt.setInt(11, id);
			
			if(streetNum == null){
				pstmt.setNull (6, java.sql.Types.INTEGER);
			} else{
				pstmt.setInt(6, streetNum);
			}
			if(zipCode == null){
				pstmt.setNull (8, java.sql.Types.INTEGER);
			} else{
				pstmt.setInt(8, streetNum);
			}
			
			//perform insert
			pstmt.executeUpdate();
			json = new JSONObject("{\"success\": \"" + firstName + " " + lastName +" successfully updated in address book\"}");
		} catch (SQLException e){
			e.printStackTrace();
			json = new JSONObject("{\"error\":\"SQL Error\"}");
		} finally{
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}	
		return json;
	}
}
