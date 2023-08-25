package com.designwright.research.microserviceplatform.service.restapi.conversion;

//import com.changehealthcare.mpbs.enterprise.common.messaging.message.model.EventMessage;
//import com.changehealthcare.mpbs.enterprise.common.messaging.message.model.Page;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.PropertyMap;
//import org.modelmapper.convention.MatchingStrategies;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class MessageConverter {

//    private MessageConverter() {
//
//    }
//
//    public static <T extends Serializable> EventMessage convertPayloadToType(EventMessage response, Class<T> destinationType, PropertyMap<?,?> propertyMap) {
//        if (response.getPayload() == null || response.getPayload().isEmpty() || response.getPayload().getContents().isEmpty()) {
//            return response;
//        }
//        return convertPayloadToType(response, null, response.getPayload(), destinationType, propertyMap);
//    }
//
//    // Just only have one use case so far
//    @SuppressWarnings("SameParameterValue")
//    public static <T extends Serializable> EventMessage convertPayloadToType(EventMessage response, List<?> payloadList, Class<T> destinationType, PropertyMap<?,?> propertyMap){
//        return convertPayloadToType(response, payloadList, new Page<>(), destinationType, propertyMap);
//    }
//
//    private static <T extends Serializable> EventMessage convertPayloadToType(EventMessage response, List<?> payloadList, Page<?> payload, Class<T> destinationType, PropertyMap<?,?> propertyMap){
//        List<?> sourceList;
//        if( payloadList != null ) {
//            sourceList = payloadList;
//        } else {
//            sourceList = payload.getContents();
//        }
//        List<T> destinationList = remapList(sourceList, propertyMap, destinationType);
//        response.setPayload(new Page<>(destinationList, payload.getTotalElements(), payload.getPageSize(), payload.getPageNumber()));
//        return response;
//    }
//
//    private static <T> List<T> remapList(List<?> sourceList, PropertyMap<?,?> propertyMap, Class<T> destinationType){
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        if( propertyMap != null ) modelMapper.addMappings(propertyMap);
//        return sourceList.stream().map(entity -> modelMapper.map(entity, destinationType)).collect(Collectors.toList());
//    }
}
