package com.hellolucky;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.ssm.SsmClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Component
public class AwsCountryDirectory {

	static final String PHONE_CODES_PARAMETER = "/hello-lucky/phone-country-codes";

	static final String LUCKY_NUMBERS_SECRET = "hello-lucky/lucky-numbers";

	private static final TypeReference<Map<String, Integer>> MAP_TYPE = new TypeReference<>() {
	};

	private final SsmClient ssm;

	private final SecretsManagerClient secrets;

	private final JsonMapper json;

	public AwsCountryDirectory(SsmClient ssm, SecretsManagerClient secrets, JsonMapper json) {
		this.ssm = ssm;
		this.secrets = secrets;
		this.json = json;
	}

	public OptionalInt phoneCode(String country) {
		String raw = ssm.getParameter(request -> request.name(PHONE_CODES_PARAMETER)).parameter().value();
		return valueFor(country, raw);
	}

	public OptionalInt luckyNumber(String country) {
		String raw = secrets.getSecretValue(request -> request.secretId(LUCKY_NUMBERS_SECRET)).secretString();
		return valueFor(country, raw);
	}

	private OptionalInt valueFor(String country, String raw) {
		Integer value = parse(json, raw).get(country);
		return value == null ? OptionalInt.empty() : OptionalInt.of(value);
	}

	static Map<String, Integer> parse(JsonMapper json, String raw) {
		Map<String, Integer> parsed = json.readValue(raw, MAP_TYPE);
		Map<String, Integer> normalized = new HashMap<>();
		parsed.forEach((key, value) -> normalized.put(key.trim().toUpperCase(Locale.ROOT), value));
		return normalized;
	}

}
