package guldilin.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import guldilin.entity.Climate;
import guldilin.errors.EnumerationConstantNotFound;
import guldilin.errors.ErrorCode;
import guldilin.errors.ErrorCodesFactory;
import guldilin.errors.ValidationException;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.HashMap;

public class GsonFactoryBuilder {
    private static Gson gson = null;

    static class IgnoreFailureTypeAdapterFactory implements TypeAdapterFactory {

        public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            return createCustomTypeAdapter(delegate);
        }

        private <T> TypeAdapter<T> createCustomTypeAdapter(TypeAdapter<T> delegate) {
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                @SneakyThrows
                @Override
                public T read(JsonReader in) {
                    try {
                        return delegate.read(in);
                    }
                    catch (JsonSyntaxException e) {
                        HashMap<String, ErrorCode> codeHashMap = ErrorCodesFactory.getErrorCodesMap();
                        HashMap<String, String> map = new HashMap<>();
                        String message = e.getCause().getMessage();
                        if (codeHashMap.containsKey(e.getCause().getClass().getName())) {
                            message = codeHashMap.get(e.getCause().getClass().getName()) + ": " + message;
                        }
                        map.put(in.getPath().replace("$.", ""), message);
                        throw new ValidationException(map);
                    }
                }
            };
        }
    }

    static class EnumAdapter<T extends Enum<T>> extends TypeAdapter<T> {
        private final Class<T> tClass;

        public EnumAdapter(Class<T> tClass) {
            super();
            this.tClass = tClass;
        }

        @Override
        public void write(JsonWriter writer, T value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(String.valueOf(value));
        }

        @SneakyThrows
        @Override
        public T read(JsonReader reader) {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String stringValue = reader.nextString();
            try {
                return T.valueOf(this.tClass, stringValue);
            } catch (IllegalArgumentException e) {
               throw new JsonSyntaxException(new EnumerationConstantNotFound());
            }
        }
    }

    public static Gson getGson() {
        if (gson != null) return gson;
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new IgnoreFailureTypeAdapterFactory())
                .registerTypeAdapter(Climate.class, new EnumAdapter<>(Climate.class))
                .serializeNulls()
                .setDateFormat(DateParserFactory.dateFormat)
                .create();
        return gson;
    }
}
