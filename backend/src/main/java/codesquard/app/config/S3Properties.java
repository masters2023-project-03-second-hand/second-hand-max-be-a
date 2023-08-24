package codesquard.app.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@ConfigurationProperties("cloud.aws")
public class S3Properties {

	private final Credentials credentials;
	private final S3 s3;
	private final Map<String, String> region;

	@ConstructorBinding
	public S3Properties(Credentials credentials, S3 s3, Map<String, String> region) {
		this.credentials = credentials;
		this.s3 = s3;
		this.region = region;
	}

	@Getter
	@RequiredArgsConstructor
	public static class Credentials {
		private final String accessKey;
		private final String secretKey;
	}

	@Getter
	@RequiredArgsConstructor
	public static class S3 {
		private final String bucket;
	}
}
