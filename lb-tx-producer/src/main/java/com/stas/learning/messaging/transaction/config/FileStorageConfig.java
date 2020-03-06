package com.stas.learning.messaging.transaction.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.stas.learning.messaging.transaction.config.FileStorageConfig.FileStorageProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile(TxKafkaConfig.NOT_DISABLE_TRANSACTION_PROFILE)
@Slf4j
@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileStorageConfig {

  @Data
  @ConfigurationProperties(prefix = "app.file.storage")
  public static class FileStorageProperties {

    private String endpoint;
    private String namespace;
    // TODO: need to get from secrets
    private String secretKey;
    private String accessKey;
  }

  @Bean
  public AmazonS3 s3Client(FileStorageProperties fileStorageProperties) {
    /*
     * Set up the client configuration to allow for 200 max HTTP
     * connections, as this is an HCP best practice.
     */
    ClientConfiguration clientConfiguration = new ClientConfiguration();
    clientConfiguration.setMaxConnections(200);
    /*
     *    By default, AWS SDK uses the HTTPS protocol and validates
     * certificates    with a certificate authority. The default
     * certificates installed in    HCP are self-signed. If these
     * self-signed certificates are used,    certificate validation
     * will need to be disabled.
     */
    clientConfiguration.setProtocol(Protocol.HTTPS);
    System.setProperty("com.amazonaws.sdk.disableCertChecking", "true");

    final BasicAWSCredentials credentials = new BasicAWSCredentials(
        fileStorageProperties.getAccessKey(),
        fileStorageProperties.getSecretKey()
    );
    return AmazonS3ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withClientConfiguration(clientConfiguration)
        .withEndpointConfiguration(new EndpointConfiguration(fileStorageProperties.getEndpoint(), ""))
        .build();
  }

}
