package com.davidruffner.inventorytrackercontroller.controller.requests;

import com.davidruffner.inventorytrackercontroller.annotations.OneOf;
import com.davidruffner.inventorytrackercontroller.annotations.RequiredParam;
import com.davidruffner.inventorytrackercontroller.exceptions.BadRequestException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseRequest {
    private Map<String, List<OneOfField>> oneOfGroupMap;
    private Map<String, List<OneOfField>> missingOneOfFieldsMap;
    private List<String> missingRequiredFieldsList;
    private List<OneOfField> oneOfFields;

    public static class OneOfField {
        private String groupName;
        private String fieldName;
        private Boolean isPresent;

        public OneOfField(String groupName, String fieldName, Boolean isPresent) {
            this.groupName = groupName;
            this.fieldName = fieldName;
            this.isPresent = isPresent;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public Boolean getPresent() {
            return isPresent;
        }
    }

    private Object getFieldValue(Field field, Object requestObj) throws RuntimeException {
        field.setAccessible(true);
        Object val;

        try {
            val = field.get(requestObj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        field.setAccessible(false);
        return val;
    }

    private boolean validateValue(Object val, Class<?> classType) {
        if (null == val)
            return false;

        switch (classType.getSimpleName()) {
            case "String":
                String valStr = (String) val;
                return !(null == valStr || valStr.isEmpty());

            case "List":
                ArrayList list = (ArrayList) val;
                return !(list.isEmpty());

            default:
                return true;
        }
    }

    public void validate(BaseRequest request) throws Exception {
        Class<?> callingClass = request.getClass();
        oneOfGroupMap = new HashMap<>();
        missingOneOfFieldsMap = new HashMap<>();
        missingRequiredFieldsList = new ArrayList<>();
        oneOfFields = new ArrayList<>();

        for (Field field : callingClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(RequiredParam.class)) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object val;

                try {
                    val = field.get(request);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                field.setAccessible(false);
                if (!validateValue(val, field.getType())) {
                    throw new BadRequestException.Builder(request.getClass(), String.format(
                            "Client must provide '%s' parameter", fieldName)).build();
                }
            } else if (field.isAnnotationPresent(OneOf.class)) {
//                field.setAccessible(true);
                String fieldName = field.getName();
                String groupName = field.getAnnotation(OneOf.class).group();

                Object val = getFieldValue(field, request);
                OneOfField oneOfField = new OneOfField(groupName, fieldName,
                        validateValue(val, field.getType()));
                oneOfFields.add(oneOfField);
                if (oneOfGroupMap.containsKey(groupName)) {
                    oneOfGroupMap.get(groupName).add(oneOfField);
                } else {
                    oneOfGroupMap.put(groupName, new ArrayList<>(Arrays.asList(oneOfField)));
                }
            }
        }

        // Filter by group names
        AtomicBoolean isAllOneOfValidated = new AtomicBoolean(false);
        oneOfGroupMap.forEach((groupName, oneOfList) -> {
            AtomicBoolean isGroupValidated = new AtomicBoolean(false);
            oneOfList.forEach(oneOfField -> {
                if (!isGroupValidated.get()) {
                    if (!oneOfField.getPresent()) {
                        if (!missingOneOfFieldsMap.containsKey(groupName)) {
                            missingOneOfFieldsMap.put(groupName, new ArrayList<>(Arrays.asList(oneOfField)));
                        } else {
                            missingOneOfFieldsMap.get(groupName).add(oneOfField);
                        }
                    }
                    isGroupValidated.set(oneOfField.getPresent());
                }
            });
            if (!isAllOneOfValidated.get()) {
                isAllOneOfValidated.set(isGroupValidated.get());
            }
        });

        if (!missingOneOfFieldsMap.isEmpty()) {
            throw new BadRequestException.Builder(callingClass,
                    "Some required parameters were not given by the client")
                    .setMissingOneOfFieldsMap(this.missingOneOfFieldsMap)
                    .build();
        }



//        oneOfFields.stream().collect(Collectors.toMap(OneOfField::getGroupName,
//                Function.identity())).forEach((groupName, b) -> {
//                    AtomicBoolean isGroupValidated = new AtomicBoolean(false);
//
//                    oneOfFields.stream().collect(Collectors.toMap(OneOfField::getFieldName,
//                            OneOfField::getPresent)).forEach((fieldName, isPresent) -> {
//                                if (!isGroupValidated.get()) {
//                                    isGroupValidated.set(isPresent);
//                                }
//                    });
//
//                    if (!isAllOneOfValidated.get()) {
//                        isAllOneOfValidated.set(isGroupValidated.get());
//                    }
//        });

//        missingOneOfFieldsMap.forEach((groupName, field) -> {
//            System.out.printf("\nGroup: %s\n", groupName);
//            field.forEach(name -> System.out.printf("\nName: %s", name.getFieldName()));
//        });
//        System.out.printf("\nIs One Of Validated: %s", isAllOneOfValidated.get());

//        oneOfGroupMap.forEach((groupName, isPresent) -> {
//            if (isPresent)
//        });
    }
}
