package guldilin.errors;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;

public class ErrorCodesFactory {
    private static HashMap<String, ErrorCode> map = null;

    public static HashMap<String, ErrorCode> getErrorCodesMap() {
        if (map != null) return map;
        map = new HashMap<>();

        map.put(UnsupportedMethod.class.getName(), ErrorCode.METHOD_NOT_SUPPORTED);
        map.put(UnsupportedContentType.class.getName(), ErrorCode.CONTENT_TYPE_NOT_SUPPORTED);
        map.put(EntryNotFound.class.getName(), ErrorCode.ENTRY_NOT_FOUND);
        map.put(EntityNotFoundException.class.getName(), ErrorCode.ENTRY_NOT_FOUND);
        map.put(ResourceNotFound.class.getName(), ErrorCode.RESOURCE_NOT_FOUND);
        map.put(FilterTypeNotFound.class.getName(), ErrorCode.FILTER_TYPE_NOT_FOUND);
        map.put(FilterTypeNotSupported.class.getName(), ErrorCode.FILTER_TYPE_NOT_SUPPORTED);
        map.put(UnknownFilterType.class.getName(), ErrorCode.FILTER_TYPE_NOT_FOUND);
        map.put(javax.persistence.NoResultException.class.getName(), ErrorCode.ENTRY_NOT_FOUND);
        map.put(NumberFormatException.class.getName(), ErrorCode.INCORRECT_NUMBER_FORMAT);
        map.put(EnumerationConstantNotFound.class.getName(), ErrorCode.ENUM_CONSTANT_NOT_FOUND);

        return map;
    }
}
