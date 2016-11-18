package com.hjwylde.rivers.models;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static com.hjwylde.rivers.util.Preconditions.checkArgument;

public final class Action implements Serializable {
    public static final String ACTION_INSERT = "insert";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_REMOVE = "remove";
    public static final List<String> ACTIONS = Arrays.asList(ACTION_INSERT, ACTION_UPDATE, ACTION_REMOVE);

    public static final String COLLECTION_SECTIONS = "sections";
    public static final List<String> COLLECTIONS = Arrays.asList(COLLECTION_SECTIONS);

    private static final long serialVersionUID = 1L;

    @SerializedName("action")
    private final String mAction;
    @SerializedName("targetId")
    private final String mTargetId;
    @SerializedName("targetCollection")
    private final String mTargetCollection;
    @SerializedName("data")
    private final JsonObject mData;

    public Action(@NonNull String action, String targetId, @NonNull String targetCollection, JsonObject data) {
        checkArgument(ACTIONS.contains(action));
        checkArgument(Arrays.asList(ACTION_UPDATE, ACTION_REMOVE).contains(action) ^ targetId == null);
        checkArgument(Arrays.asList(ACTION_INSERT, ACTION_UPDATE).contains(action) ^ data == null);
        checkArgument(COLLECTIONS.contains(targetCollection));

        mAction = action;
        mTargetId = targetId;
        mTargetCollection = targetCollection;
        mData = data;
    }

    public String getAction() {
        return mAction;
    }

    public String getTargetId() {
        return mTargetId;
    }

    public String getTargetCollection() {
        return mTargetCollection;
    }

    public JsonObject getData() {
        return mData;
    }

    public static final class Builder {
        private String mAction;
        private String mTargetId;
        private String mTargetCollection;
        private JsonObject mData;

        public Builder() {
        }

        public Action build() {
            return new Action(mAction, mTargetId, mTargetCollection, mData);
        }

        public void action(String action) {
            mAction = action;
        }

        public void targetId(String targetId) {
            mTargetId = targetId;
        }

        public void targetCollection(String targetCollection) {
            mTargetCollection = targetCollection;
        }

        public void data(JsonObject data) {
            mData = data;
        }

        public void datum(String key, Boolean value) {
            if (mData == null) {
                mData = new JsonObject();
            }

            mData.addProperty(key, value);
        }

        public void datum(String key, Character value) {
            if (mData == null) {
                mData = new JsonObject();
            }

            mData.addProperty(key, value);
        }

        public void datum(String key, Number value) {
            if (mData == null) {
                mData = new JsonObject();
            }

            mData.addProperty(key, value);
        }

        public void datum(String key, String value) {
            if (mData == null) {
                mData = new JsonObject();
            }

            mData.addProperty(key, value);
        }
    }
}
