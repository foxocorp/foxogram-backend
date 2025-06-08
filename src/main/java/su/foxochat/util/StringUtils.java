package su.foxochat.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class StringUtils {

	public static String inputStreamToString(InputStream inputStream, Charset charsetName) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];

		for (int length; (length = inputStream.read(buffer)) != -1; ) {
			result.write(buffer, 0, length);
		}

		return result.toString(charsetName);
	}
}
