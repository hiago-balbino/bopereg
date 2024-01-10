package br.com.wes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoperegApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoperegApplication.class, args);

//		generatePassword();
	}

//	private static void generatePassword() {
//		Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder("", 8, 185000, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
//
//		Map<String, PasswordEncoder> encoders = new HashMap<>();
//		encoders.put("pbkdf2", pbkdf2PasswordEncoder);
//
//		DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
//		passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2PasswordEncoder);
//
//		String result = passwordEncoder.encode("");
//		System.out.printf(result);
//	}
}
