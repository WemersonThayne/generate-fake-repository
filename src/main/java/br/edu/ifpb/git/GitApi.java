package br.edu.ifpb.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GitApi {
    static Logger logger = LoggerFactory.getLogger(GitApi.class);
    private  GitApi(){}
    public static boolean createRepositoryRemote(String userName, String accessToken, String nameRepository, String description) {
        try {
          logger.info("Creating repository in github.com");

            URL url = new URL("https://api.github.com/user/repos");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/vnd.github.v3+json");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String encoding = Base64.getEncoder().encodeToString((userName.concat(":").concat(accessToken)).getBytes());
            http.setRequestProperty("Authorization", "Basic ".concat(encoding));

            String data = "{\"name\":\"" + nameRepository + "\", \"description\":\"" + description + "\"}";

            byte[] out = data.getBytes(StandardCharsets.UTF_8);

           logger.info("Prepare to send to github...");

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            logger.info("http status code: {}, http message: {}", http.getResponseCode(), http.getResponseMessage());
            http.disconnect();

            if (http.getResponseCode() == 201) {
                logger.info("Done. Go to https://github.com/{}/{}.git to see.",userName,nameRepository);
            }

            return true;
        } catch (IOException e) {
            logger.error("Error to create repository remote, error: {} ", e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}