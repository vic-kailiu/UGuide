package com.kai.uguide.utils;

import android.content.Context;

import com.nuance.nmdp.speechkit.SpeechKit;
/**
 * Created by Kai on 28/12/2014.
 */
public class NuanceTTS {

    private static final String TTS_KEY = "com.nuance.nmdp.sample.tts";

    private static final String SpeechKitServer = "sandbox.nmdp.nuancemobility.net"/* Enter your server here */;
    private static final int SpeechKitPort = 443/* Enter your port here */;
    private static final boolean SpeechKitSsl = false;
    private static final String SpeechKitAppId = "NMDPTRIAL_vickailiu20141228084028"/* Enter your ID here */;
    private static final byte[] SpeechKitApplicationKey = {
            (byte)0x0c, (byte)0xd3, (byte)0xf3, (byte)0xd6, (byte)0x4e, (byte)0x32, (byte)0x66, (byte)0xae, (byte)0x26, (byte)0xa1, (byte)0x93, (byte)0x2c, (byte)0x6c, (byte)0x70, (byte)0xe0, (byte)0x97, (byte)0xf2, (byte)0xcf, (byte)0x90, (byte)0x06, (byte)0x3b, (byte)0x3f, (byte)0xdf, (byte)0x8d, (byte)0x05, (byte)0xa7, (byte)0x4e, (byte)0xd7, (byte)0x96, (byte)0xb5, (byte)0xe0, (byte)0xa2, (byte)0x88, (byte)0xd3, (byte)0x78, (byte)0xd6, (byte)0x05, (byte)0xde, (byte)0x85, (byte)0x2b, (byte)0x80, (byte)0x18, (byte)0x57, (byte)0xec, (byte)0x89, (byte)0x37, (byte)0x84, (byte)0x7b, (byte)0x07, (byte)0x14, (byte)0x81, (byte)0x42, (byte)0x53, (byte)0x61, (byte)0xcf, (byte)0x58, (byte)0x08, (byte)0x1e, (byte)0x30, (byte)0x9e, (byte)0xd3, (byte)0x10, (byte)0x40, (byte)0x5c};

    private static SpeechKit _speechKit;

    // Allow other activities to access the SpeechKit instance.
    public static SpeechKit getSpeechKit()
    {
        return _speechKit;
    }

    public static void SetUpNuanceTTS(Context context) {
        _speechKit = SpeechKit.initialize(context,
                    SpeechKitAppId,
                    SpeechKitServer,
                    SpeechKitPort,
                    SpeechKitSsl,
                    SpeechKitApplicationKey);
        _speechKit.connect();
    }

    public static void Destroy()
    {
        if (_speechKit == null)
            return;

        _speechKit.release();
        _speechKit = null;
    }

}
