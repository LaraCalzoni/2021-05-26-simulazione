package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Adiacenza;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public void getAllBusiness(Map<String, Business> idMap){
		String sql = "SELECT * FROM Business";
		//List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!idMap.containsKey(res.getString("business_id"))) {
				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				//result.add(business);
				idMap.put(business.getBusinessId(), business);
			}
			}
			res.close();
			st.close();
			conn.close();
			//return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			//return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List <Business> getVertici(int anno, String citta, Map <String, Business> idMap){
		
	String sql = "SELECT DISTINCT(b.business_id) "
			+ "FROM business b, reviews r "
			+ "WHERE b.business_id= r.business_id AND b.city= ? AND YEAR(r.review_date) = ? ";	
		
	List<Business> result = new ArrayList<>();
	Connection conn = DBConnect.getConnection();

	try {
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, anno);
		st.setString(2, citta);
		ResultSet res = st.executeQuery();
		while (res.next()) {

			if(idMap.containsKey(res.getString("b.business_id"))) {
				result.add(idMap.get(res.getString("b.business_id")));
			}
		}
		res.close();
		st.close();
		conn.close();
		return result;
		
	} catch (SQLException e) {
		e.printStackTrace();
		return null;
	}
}

	public List <Adiacenza> getAdiacenze(int anno, String citta, Map <String, Business> idMap ){
		
		String sql= "SELECT r1.business_id, r2.business_id, r1.review_id, r2.review_id, AVG(r1.stars) AS media1, AVG(r2.stars) AS media2, AVG(r1.stars)-AVG(r2.stars) AS differenza "
				+ "FROM business b1, business b2, reviews r1, reviews r2 "
				+ "WHERE b1.business_id=r1.business_id AND b2.business_id=r2.business_id AND r1.business_id> r2.business_id "
				+ "AND r1.review_id!= r2.review_id AND b1.city=? AND b2.city=b1.city AND YEAR(r1.review_date) = ? AND YEAR(r1.review_date) = YEAR(r2.review_date) "
				+ "GROUP BY r1.business_id, r2.business_id "
				+ "HAVING differenza!=0";
		
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(2, anno);
			st.setString(1, citta);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(idMap.containsKey(res.getString("r1.business_id")) && idMap.containsKey(res.getString("r2.business_id"))) {
					Business b1 = idMap.get(res.getString("r1.business_id"));
					b1.setMedia(res.getDouble("media1"));
					Business b2 = idMap.get(res.getString("r2.business_id"));
					b2.setMedia(res.getDouble("media2"));
					double differenza = res.getDouble("differenza");
					Adiacenza a = new Adiacenza(b1,b2,differenza);
					result.add(a);
					
					
				}
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List <String> getCitta(){
		String sql = "SELECT DISTINCT b.city "
				+ "FROM business b";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

			
				result.add(res.getString("b.city"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
