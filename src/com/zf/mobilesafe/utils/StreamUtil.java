package com.zf.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {
	public static String readFromInputStream(InputStream in) throws IOException{
		String response = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while((len = in.read(b)) != -1){
			out.write(b, 0, len);
		}
		response = out.toString();
		out.close();
		in.close();
		return response;
	}
}
