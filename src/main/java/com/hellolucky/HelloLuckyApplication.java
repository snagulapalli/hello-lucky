package com.hellolucky;

import java.util.Locale;
import java.util.OptionalInt;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@SpringBootApplication
@RestController
@RequestMapping("/hello-lucky")
public class HelloLuckyApplication {

	private final AwsCountryDirectory countries;

	public HelloLuckyApplication(AwsCountryDirectory countries) {
		this.countries = countries;
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloLuckyApplication.class, args);
	}

	@GetMapping("/hello")
	HelloResponse hello(@RequestParam String country) {
		String normalized = country.trim().toUpperCase(Locale.ROOT);
		OptionalInt phoneCountryCode = countries.phoneCode(normalized);
		OptionalInt luckyNumber = countries.luckyNumber(normalized);
		if (phoneCountryCode.isEmpty() || luckyNumber.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown country: " + normalized);
		}
		return new HelloResponse(normalized, phoneCountryCode.getAsInt(), luckyNumber.getAsInt());
	}

	public record HelloResponse(
			String country,
			@JsonProperty("phone-country-code") int phoneCountryCode,
			@JsonProperty("lucky-number") int luckyNumber) {
	}

}
