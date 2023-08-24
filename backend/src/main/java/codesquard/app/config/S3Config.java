package codesquard.app.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
public class S3Config {

	private final String accessKey;
	private final String secretKey;
	private final String region;

	public S3Config(S3Properties s3Properties) {
		this.accessKey = s3Properties.getCredentials().getAccessKey();
		this.secretKey = s3Properties.getCredentials().getSecretKey();
		this.region = s3Properties.getRegion().get("static");
	}

	@Bean
	public AmazonS3Client S3Client() {
		BasicAWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		return (AmazonS3Client) AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(this.region)
			.build();
	}
}
