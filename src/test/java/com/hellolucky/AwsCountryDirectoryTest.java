package com.hellolucky;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class AwsCountryDirectoryTest {

	private final JsonMapper json = JsonMapper.builder().build();

	@Test
	void parsesACountryMapAndNormalizesKeys() {
		assertThat(AwsCountryDirectory.parse(json, "{\"us\":1,\"INDIA\":91}"))
				.containsEntry("US", 1)
				.containsEntry("INDIA", 91);
	}

}
