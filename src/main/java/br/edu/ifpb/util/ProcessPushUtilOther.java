package br.edu.ifpb.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Deprecated
public class ProcessPushUtilOther {

    private String getPathBash() {
        File file = new File("src/main/resources/git-commands.sh");
        return file.getAbsolutePath();
    }

    private String builder(String pathRepositoryLocal, String nameRepository,
                                  String branch, String userName, String accessToken) {
        return getPathBash().concat(" ").concat(pathRepositoryLocal).concat(" ").concat(nameRepository).concat(" ")
                .concat(" ").concat(branch).concat(" ").concat(userName).concat(" ").concat(accessToken);
    }

    public void run(String pathRepositoryLocal, String nameRepository,
                               String branch, String description, String userName, String accessToken) {

        //boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

//        if(createRepositoryRemote(userName, accessToken,nameRepository,description)){
//            try {
//                Thread.sleep(2000);
//            } catch (Exception e) {
//            }
//            runCommand(builder(pathRepositoryLocal, nameRepository,  branch, userName, accessToken));
//        }


//
//        // -- Windows --
//
//        // Run a command
//        //processBuilder.command("cmd.exe", "/c", "dir C:\\Users\\mkyong");
//
//        // Run a bat file
//        //processBuilder.command("C:\\Users\\mkyong\\hello.bat");
    }


    private void runCommand(String command) {
        try {

            Process process = Runtime.getRuntime().exec(command);

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println(output);
                System.exit(0);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean createRepositoryRemote(String userName, String accessToken, String nameRepository, String description) {

        try {

            System.out.println("Creating repository in github.com");

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

            System.out.println("Prepare to send to github...");

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
            http.disconnect();

            if (http.getResponseCode() == 201) {
                System.out.println("Done. Go to https://github.com/"+userName+"/"+nameRepository+".git to see.");
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}