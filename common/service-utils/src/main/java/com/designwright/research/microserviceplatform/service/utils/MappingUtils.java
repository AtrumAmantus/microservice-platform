package com.designwright.research.microserviceplatform.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/** Utilities for object transformations and JSON serialization/deserialization */
@Component
public class MappingUtils {

    /** Single instance of transformation class for efficiency */
    private ObjectMapper objectMapper;

    /**
     * Construct an instance of the {@link ObjectMapper} to be used by every service using this
     * component.
     */
    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Converts a single object to a specified type.
     * <p></p>
     *
     * @param object The object to convert
     * @param clazz  The destination type to convert to
     * @param <D>    The destination type
     * @param <S>    The source object type
     *
     * @return a new instance of the object, converted to the specified type
     */
    public <D, S> D convertToType(S object, Class<D> clazz) {
        return objectMapper
                .convertValue(object, clazz);
    }

    /**
     * Converts a list of objects to a specified type.
     * <p></p>
     *
     * @param objectList The list of objects to convert
     * @param clazz      The destination type to convert to
     * @param <D>        The destination type
     * @param <S>        The source object type
     *
     * @return a new instance of the object list, with every element converted to the specified type
     */
    public <D, S> List<D> convertToType(List<S> objectList, Class<D> clazz) {
        return objectMapper
                .convertValue(objectList, objectMapper.getTypeFactory().constructCollectionLikeType(List.class, clazz));
    }

    /**
     * Converts a set of objects to a specified type.
     * <p></p>
     *
     * @param objectList The set of objects to convert
     * @param clazz      The destination type to convert to
     * @param <D>        The destination type
     * @param <S>        The source object type
     *
     * @return a new instance of the object set, with every element converted to the specified type
     */
    public <D, S> Set<D> convertToType(Set<S> objectList, Class<D> clazz) {
        return objectMapper
                .convertValue(objectList, objectMapper.getTypeFactory().constructCollectionLikeType(Set.class, clazz));
    }

    /**
     * Converts a given json string to a provided type
     * <p></p>
     *
     * @param jsonString The json string to convert
     * @param clazz      The destination type to convert to
     * @param <D>        The destination type
     *
     * @return a new instance of the deserialized object as the provided type
     *
     * @throws IOException when the json object can not be deserialized
     */
    public <D extends Serializable> D convertFromJson(String jsonString, Class<D> clazz) throws IOException {
        return objectMapper
                .readValue(jsonString, clazz);
    }

    /**
     * Converts a given json string list to a provided type
     * <p></p>
     *
     * @param jsonString The json string to convert
     * @param clazz      The destination type to convert to
     * @param <D>        The destination type
     *
     * @return a new instance of the deserialized object list as the provided type
     *
     * @throws IOException when the json object can not be deserialized
     */
    public <D extends Serializable> List<D> convertFromJsonList(String jsonString, Class<D> clazz) throws IOException {
        return objectMapper
                .readValue(jsonString, objectMapper.getTypeFactory().constructCollectionLikeType(List.class, clazz));
    }

    /**
     * Converts a given json string list to a provided type
     * <p></p>
     *
     * @param inputStream The {@link InputStream} to read from and convert
     * @param clazz       The destination type to convert to
     * @param <D>         The destination type
     *
     * @return a new instance of the deserialized object list as the provided type
     *
     * @throws IOException when the json object can not be deserialized
     */
    public <D extends Serializable> List<D> convertFromJsonList(InputStream inputStream, Class<D> clazz) throws IOException {
        return objectMapper
                .readValue(inputStream, objectMapper.getTypeFactory().constructCollectionLikeType(List.class, clazz));
    }

    /**
     * Writes out the object as a json string
     *
     * @param object The object to write as a json String
     *
     * @return The json string
     *
     * @throws JsonProcessingException when the object couldn't be serialized
     */
    public String convertToJson(Object object) throws JsonProcessingException {
        return objectMapper
                .writeValueAsString(object);
    }
}
