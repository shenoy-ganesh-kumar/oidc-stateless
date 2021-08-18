package com.okta.developer.jugtours.context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * JDBC based repository for storing Authorization requests
 */
@Component
public class JdbcOAuth2AuthorizationRequestRepository implements 
    AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Load authorization request from database using JDBC.
	 */
	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		Assert.notNull(request, "request cannot be null");
		String stateParameter = this.getStateParameter(request);
		if (stateParameter == null) {
			return null;
		}

		List<byte[]> byteArray = jdbcTemplate.query(
				"SELECT auth_request FROM auth where state_param='" + stateParameter + "'", (rs, n) -> rs.getBytes(1));

		OAuth2AuthorizationRequest auth2AuthorizationRequest = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteArray.get(0)));
			auth2AuthorizationRequest = (OAuth2AuthorizationRequest) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return auth2AuthorizationRequest;
	}

	/**
	 * Gets the state parameter from the {@link HttpServletRequest}
	 * @param request the request to use
	 * @return the state parameter or null if not found
	 */
	private String getStateParameter(HttpServletRequest request) { // FIXME: DRY
		return request.getParameter(OAuth2ParameterNames.STATE);
	}

	/**
	 * Save authorization request in cookie
	 */
	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
			HttpServletResponse response) {
		Assert.notNull(request, "request cannot be null");
		Assert.notNull(response, "response cannot be null");

		if (authorizationRequest == null) {
			deleteCookies(getStateParameter(request)); // FIXME
			return;
		}

		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection)
					throws SQLException {
				String sql = "INSERT INTO auth (state_param, auth_request, unique_client_hash) values (?, ?, ?)";
				PreparedStatement ps = connection.prepareStatement(sql.toString(),
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, authorizationRequest.getState());
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    try {
			    	ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(authorizationRequest);
				    oos.flush();
				    oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			    byte[] request = baos.toByteArray();
			    InputStream is = new ByteArrayInputStream(request);
				new DefaultLobHandler().getLobCreator().setBlobAsBinaryStream(ps, 2, is, request.length);
				
				ps.setString(3, Long.toString(System.currentTimeMillis()));
				return ps;
			}
		});
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
		return loadAuthorizationRequest(request);
	}

	/**
	 * Utility for record from database.
	 */
	public void deleteCookies(String state) {
		jdbcTemplate.execute("DELETE FROM auth where state_param=" + state);
	}
}