package com.turkraft.springfilter.jpa;

import java.io.IOException;
import java.util.Date;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.turkraft.springfilter.SpringFilterParameters;

@Configuration
public class MapperConfiguration {

  @Bean
  @Primary
  public ObjectMapper objectMapper() {

    ObjectMapper mapper = new ObjectMapper();

    // Java 8 date formatting -->
    SimpleModule simpleModule = new SimpleModule();
    simpleModule.addSerializer(Date.class, new JsonSerializer<Date>() {
      @Override
      public void serialize(
          Date date,
          JsonGenerator jsonGenerator,
          SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(SpringFilterParameters.DATE_FORMATTER.format(date));
      }
    });
    mapper.registerModule(simpleModule);

    return mapper;

  }

}
