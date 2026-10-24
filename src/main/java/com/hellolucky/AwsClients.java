package com.hellolucky;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.ssm.SsmClient;

@Configuration
public class AwsClients {

	@Bean(destroyMethod = "close")
	SsmClient ssmClient() {
		return SsmClient.builder()
				.httpClientBuilder(UrlConnectionHttpClient.builder())
				.build();
	}

	@Bean(destroyMethod = "close")
	SecretsManagerClient secretsManagerClient() {
		return SecretsManagerClient.builder()
				.httpClientBuilder(UrlConnectionHttpClient.builder())
				.build();
	}

}
