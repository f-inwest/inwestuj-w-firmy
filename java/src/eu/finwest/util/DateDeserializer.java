package eu.finwest.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

/**
 * Jackson's JSON date deserializer.
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class DateDeserializer extends JsonDeserializer<Date> {
	@Override
	public Date deserialize(JsonParser jsonparser,
			DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = jsonparser.getText();
		try {
			return format.parse(date);
		} catch (ParseException e) {
			try {
				format = new SimpleDateFormat("yyyyMMddHHmm");
				return format.parse(date);
			} catch (ParseException ex) {
				try {
					format = new SimpleDateFormat("yyyyMMdd");
					return format.parse(date);
				} catch (ParseException exx) {
					throw new RuntimeException(exx);
				}
			}
		}
	}
}
