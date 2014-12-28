package com.kai.uguide;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.kai.uguide.utils.NuanceTTS;
import com.nuance.nmdp.speechkit.SpeechError;
import com.nuance.nmdp.speechkit.Vocalizer;

public class TextToSpeechActivity extends Activity implements Vocalizer.Listener {

    private Button mAgainButton;
    private String message;

    private Vocalizer _vocalizer;
    private Object _ttsContext = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);

        message = (String) getIntent().getStringExtra("text");


        Context context = getApplicationContext();
        //TODO:could move it to splash maybe
        NuanceTTS.SetUpNuanceTTS(context);

        // Create a single Vocalizer here.
        _vocalizer = NuanceTTS.getSpeechKit().createVocalizerWithLanguage("en_US", this, new Handler());
        _vocalizer.setVoice("Ava");

        // The button is disabled in the layout.
        // It will be enabled upon initialization of the TTS engine.
        mAgainButton = (Button) findViewById(R.id.speak);

        mAgainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                _ttsContext = new Object();
                _vocalizer.speakString(message, _ttsContext);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_vocalizer != null)
        {
            _vocalizer.cancel();
            _vocalizer = null;
        }
        NuanceTTS.Destroy();
    }

    @Override
    public void onSpeakingBegin(Vocalizer vocalizer, String text, Object context) {
        //updateCurrentText("Playing text: \"" + text + "\"", Color.GREEN, false);
        // for debugging purpose: printing out the speechkit session id
        android.util.Log.d("Nuance SampleVoiceApp", "Vocalizer.Listener.onSpeakingBegin: session id ["
                + NuanceTTS.getSpeechKit().getSessionId() + "]");
    }

    @Override
    public void onSpeakingDone(Vocalizer vocalizer,
                               String text, SpeechError error, Object context)
    {
        // Use the context to detemine if this was the final TTS phrase
        if (context != _ttsContext)
        {
            //updateCurrentText("More phrases remaining", Color.YELLOW, false);
        } else
        {
            //updateCurrentText("", Color.YELLOW, false);
        }
        // for debugging purpose: printing out the speechkit session id
        android.util.Log.d("Nuance SampleVoiceApp", "Vocalizer.Listener.onSpeakingDone: session id ["
                + NuanceTTS.getSpeechKit().getSessionId() + "]");
    }
}