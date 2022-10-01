package com.overonix.test.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static java.lang.Boolean.FALSE;
import static java.lang.String.format;

public class TestUtils {

  private static final String JSON_EXCEPTION = "Can not create class %s from %s.";
  private static final String SET_EXCEPTION = "Can not create set of class %s from %s.";

  public static <T> T getTestFixtureAsObject(String fixtureFileUrl, Class<T> clazz) {
    try {
      return prepareObjectMapper().readValue(getInputStream(fixtureFileUrl), clazz);
    } catch (IOException e) {
      throw new RuntimeException(format(JSON_EXCEPTION, clazz.getName(), fixtureFileUrl), e);
    }
  }

  public static <T> Set<T> getTestFixtureAsSetOfObjects(String fixtureFileUrl, Class<T> clazz) {
    try {
      return prepareObjectReaderForCollection(Set.class, clazz)
          .readValue(getInputStream(fixtureFileUrl));
    } catch (IOException e) {
      throw new RuntimeException(format(SET_EXCEPTION, clazz.getName(), fixtureFileUrl), e);
    }
  }

  private static ObjectReader prepareObjectReaderForCollection(
      Class<? extends Collection> collectionClass, Class<?> clazz) {

    ObjectMapper objectMapper = prepareObjectMapper();
    return objectMapper.readerFor(
        objectMapper.getTypeFactory().constructCollectionType(collectionClass, clazz));
  }

  private static ObjectMapper prepareObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, FALSE);
    return objectMapper;
  }

  private static InputStream getInputStream(String fixtureFileUrl) throws IOException {
    return new ClassPathResource(fixtureFileUrl).getInputStream();
  }
}
