package eu.finwest.util;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * Created by grzegorznittner on 31.07.2015.
 */
public class NullKeySerializer extends JsonSerializer<Object>
{
    public void serialize(Object value, JsonGenerator jgen,
                          SerializerProvider provider)
            throws IOException, JsonProcessingException
    {
        jgen.writeFieldName("");
    }
}