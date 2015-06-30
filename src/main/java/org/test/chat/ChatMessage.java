package org.test.chat;

import com.google.gson.annotations.SerializedName;
import net.jcip.annotations.ThreadSafe;

import java.util.Objects;

@ThreadSafe
public final class ChatMessage {

    @SerializedName("user")
    public final String user;

    @SerializedName("msg")
    public final String text;

    public ChatMessage(String user, String text) {
        this.user = Objects.requireNonNull(user, "user == null");
        this.text = Objects.requireNonNull(text, "text == null");
    }

    @Override
    public String toString() {
        return user + ": " + text;
    }
}
