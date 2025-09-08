package com.josegc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonReader {
  public static final ObjectMapper MAPPER =
      new ObjectMapper()
          .findAndRegisterModules()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  public static <T> T readObject(Class<T> clazz, String dir) throws IOException {
    return MAPPER.readValue(new File(dir), clazz);
  }
}
