package com.connorlinfoot.mc2fa.shared;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateHandler {
    private final String update_url = "http://api.connorlinfoot.com/v1/resource/release/mc2fa/";
    private boolean enabled = true;
    private UpdateResult updateResult = UpdateResult.CHECKING;
    private String currentVersion;
    private String newestVersion;
    private String message = null;

    public enum UpdateResult {
        NO_UPDATE,
        DISABLED,
        CHECKING,
        FAIL_HTTP,
        UPDATE_AVAILABLE
    }

    public UpdateHandler(boolean enabled, String currentVersion) {
        this.enabled = enabled;
        this.currentVersion = currentVersion;
        if (!enabled)
            this.updateResult = UpdateResult.DISABLED;
    }

    public UpdateResult getUpdateResult() {
        return updateResult;
    }

    public void checkForUpdate() {
        String data = null;
        try {
            data = doCurl(update_url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) jsonParser.parse(data);
            if (obj.get("version") != null) {
                newestVersion = obj.get("version").toString();
                if (Integer.parseInt(newestVersion.replace(".", "")) > Integer.parseInt(currentVersion.replace(".", ""))) {
                    updateResult = UpdateResult.UPDATE_AVAILABLE;
                } else {
                    updateResult = UpdateResult.NO_UPDATE;
                }

                if (obj.containsKey("message")) {
                    message = obj.get("message").toString();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String doCurl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setInstanceFollowRedirects(true);
        con.setDoOutput(true);
        con.setDoInput(true);
        DataOutputStream output = new DataOutputStream(con.getOutputStream());
        output.close();
        DataInputStream input = new DataInputStream(con.getInputStream());
        int c;
        StringBuilder resultBuf = new StringBuilder();
        while ((c = input.read()) != -1) {
            resultBuf.append((char) c);
        }
        input.close();
        return resultBuf.toString();
    }

    public String getNewestVersion() {
        return newestVersion;
    }

    public String getMessage() {
        return message;
    }

}
