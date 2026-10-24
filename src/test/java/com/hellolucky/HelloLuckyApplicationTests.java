package com.hellolucky;

import java.util.OptionalInt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HelloLuckyApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AwsCountryDirectory countries;

	@Test
	void helloReadsPhoneCodeAndLuckyNumber() throws Exception {
		when(countries.phoneCode("US")).thenReturn(OptionalInt.of(1));
		when(countries.luckyNumber("US")).thenReturn(OptionalInt.of(11));

		mockMvc.perform(get("/hello-lucky/hello").param("country", "US"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.country").value("US"))
				.andExpect(jsonPath("$.phone-country-code").value(1))
				.andExpect(jsonPath("$.lucky-number").value(11));
	}

	@Test
	void rejectsUnknownCountry() throws Exception {
		when(countries.phoneCode("ZZ")).thenReturn(OptionalInt.empty());
		when(countries.luckyNumber("ZZ")).thenReturn(OptionalInt.empty());

		mockMvc.perform(get("/hello-lucky/hello").param("country", "zz"))
				.andExpect(status().isBadRequest());
	}

}
