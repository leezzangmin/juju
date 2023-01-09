package gabia.gvote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GVoteApplication {

	public static void main(String[] args) {
		SpringApplication.run(GVoteApplication.class, args);
	}

}
