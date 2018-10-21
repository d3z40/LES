package br.com.les.backend.security;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import br.com.les.backend.entity.User;
import br.com.les.backend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenAuthenticationService {
	
	@Autowired
	@Qualifier("userRepository")
	private UserRepository userRepository;
	
	// EXPIRATION_TIME = 10 dias
	static final long EXPIRATION_TIME = 860_000_000;
	static final String SECRET = "MySecret";
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";
	static final String EXPOSE_HEADER = "Access-Control-Expose-Headers";
	
	public void addAuthentication(HttpServletResponse response, String email) {
		String JWT = Jwts.builder()
				.setSubject(email)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();
		
		response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);		
		response.addHeader(EXPOSE_HEADER, HEADER_STRING);
		
	}
	
public Authentication getAuthentication(HttpServletRequest request) {
				
		String token = request.getHeader(HEADER_STRING);
		
		if (token != null) {
			String user = Jwts.parser()
					.setSigningKey(SECRET)
					.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
					.getBody()
					.getSubject();
			User load = (User) userRepository.findByEmail(user);
			if (load != null && load.getId() > 0) {
				return new UsernamePasswordAuthenticationToken(load, null, load.getRoleList());
			}
		}
		 
		
		return null;
	}


}