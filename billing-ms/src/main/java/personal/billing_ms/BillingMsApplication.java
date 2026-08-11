package personal.billing_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BillingMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingMsApplication.class, args);
	}

}
