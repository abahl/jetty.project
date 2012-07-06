package org.eclipse.jetty.websocket.util;

import java.nio.ByteBuffer;

import org.eclipse.jetty.util.StringUtil;

public class CloseUtil
{
    public static String getReason(byte[] payload)
    {
        if (payload.length <= 2)
        {
            return null;
        }
        ByteBuffer bb = ByteBuffer.wrap(payload);
        int len = payload.length - 2;
        byte utf[] = new byte[len];
        for (int i = 0; i < len; i++)
        {
            utf[i] = bb.get(i + 2);
        }
        return StringUtil.toUTF8String(utf,0,utf.length);
    }

    public static String getReason(ByteBuffer payload)
    {
        if (payload.remaining() <= 2)
        {
            return null;
        }
        int len = payload.remaining() - 2;
        byte utf[] = new byte[len];
        for (int i = 0; i < len; i++)
        {
            utf[i] = payload.get(i + 2);
        }
        return StringUtil.toUTF8String(utf,0,utf.length);
    }

    public static int getStatusCode(byte[] payload)
    {
        if (payload.length < 2)
        {
            return 0; // no status code
        }

        int statusCode = 0;
        ByteBuffer bb = ByteBuffer.wrap(payload);
        statusCode |= (bb.get(0) & 0xFF) << 8;
        statusCode |= (bb.get(1) & 0xFF);
        return statusCode;
    }

    public static int getStatusCode(ByteBuffer payload)
    {
        if (payload.remaining() < 2)
        {
            return 0; // no status code
        }

        int statusCode = 0;
        statusCode |= (payload.get(0) & 0xFF) << 8;
        statusCode |= (payload.get(1) & 0xFF);
        return statusCode;
    }
}
