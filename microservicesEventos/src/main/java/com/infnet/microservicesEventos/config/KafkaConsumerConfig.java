package com.infnet.microservicesEventos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

/**
 * Os eventos chegam de outros microsserviços (pacotes diferentes), então não
 * podemos depender do cabeçalho de tipo (__TypeId__) embutido pelo produtor —
 * a classe de origem não existe neste serviço.
 *
 * Lemos o valor como String (ver application.properties) e registramos um
 * {@link StringJsonMessageConverter}. O Spring Boot o injeta automaticamente
 * na fábrica de listeners; por padrão ele infere o tipo a partir do parâmetro
 * de cada método {@code @KafkaListener}, ignorando o pacote de origem.
 */
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public RecordMessageConverter jsonMessageConverter() {
        return new StringJsonMessageConverter();
    }
}
