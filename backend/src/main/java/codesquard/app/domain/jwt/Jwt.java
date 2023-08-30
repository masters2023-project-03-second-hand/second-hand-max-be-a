package codesquard.app.domain.jwt;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Jwt {

	private String accessToken;
	private String refreshToken;
	private Date expireDateAccessToken;
	private Date expireDateRefreshToken;

	private Jwt() {

	}

	private Jwt(String accessToken, String refreshToken, Date expireDateAccessToken, Date expireDateRefreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expireDateAccessToken = expireDateAccessToken;
		this.expireDateRefreshToken = expireDateRefreshToken;
	}

	public static Jwt create(String accessToken, String refreshToken, Date expireDateAccessToken,
		Date expireDateRefreshToken) {
		return new Jwt(accessToken, refreshToken, expireDateAccessToken, expireDateRefreshToken);
	}

	@JsonIgnore
	public long getExpireDateAccessTokenTime() {
		return expireDateAccessToken.getTime();
	}

	@JsonIgnore
	public long getExpireDateRefreshTokenTime() {
		return expireDateRefreshToken.getTime();
	}
}
