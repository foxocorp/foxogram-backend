package su.foxogram.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("rabbit")
@Getter
@Setter
public class RabbitConfig {

	private String queue;

	@Bean
	public Queue queue() {
		return new Queue(queue);
	}
}
