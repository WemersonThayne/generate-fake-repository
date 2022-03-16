package br.edu.ifpb;

import br.edu.ifpb.enums.TypeFileModification;
import br.edu.ifpb.git.Branch;
import br.edu.ifpb.git.Commit;
import br.edu.ifpb.git.Repository;
import br.edu.ifpb.mapper.DeveloperMapper;
import br.edu.ifpb.model.ConfigWrapper;
import br.edu.ifpb.model.Change;
import br.edu.ifpb.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Main {

    public static void progressPercentage(int remain, int total) {
        if (remain > total) {
            throw new IllegalArgumentException();
        }
        int maxBareSize = 10; // 10unit for 100%
        int remainProcent = ((100 * remain) / total) / maxBareSize;
        char defaultChar = '-';
        String icon = "*********";
        String bare = new String(new char[maxBareSize]).replace('\0', defaultChar) + "]";
        StringBuilder bareDone = new StringBuilder();
        bareDone.append("[");
        for (int i = 0; i < remainProcent; i++) {
            bareDone.append(icon);
        }
        String bareRemain = bare.substring(remainProcent, bare.length());
        System.out.print("\r" + bareDone + bareRemain + " " + remainProcent * 10 + "%");
        if (remain == total) {
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        mapper.findAndRegisterModules();

        try {
            System.out.println("Loading file configure with commits");
            for (int i = 0; i <= 100; i = i + 20) {
                progressPercentage(i, 100);
                Thread.sleep(500);
            }

            String pathConfigFile = List.of(args).get(0);
            //String pathConfigFile = "src/main/resources/config.yaml";

            ConfigWrapper config = mapper.readValue(new File(pathConfigFile), ConfigWrapper.class);

            Path path = Path.of(config.getProject().getPath(), config.getProject().getName());
            Repository.init(path);
            System.out.println("Initial process create fake repository....");
            var local = Repository.getRepository(config.getProject().getPath().concat("/").concat(config.getProject().getName()));
            Branch.createBranch(local, config.getProject().getBranch());
            Branch.checkoutBranch(local, config.getProject().getBranch());

            config.getCommits().forEach(c -> {

                System.out.printf("#### Commit message: %s - developer: %s - %s #### %n",
                        c.getMessage(), c.getDeveloper().getName(), c.getDeveloper().getEmail());

                List<String> files = new ArrayList<>();
                c.getChanges().forEach(cf -> commitPrepare(cf, path, files));

                Commit.gitAdd(local, files);
                System.out.println(files);
                Commit.gitCommit(local, c.getMessage(), DeveloperMapper.mapper(c.getDeveloper()));

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Commit success...\n");

            });

            ProcessPushUtil push = new ProcessPushUtil();
            push.run(config.getProject().getPath().concat("/").concat(config.getProject().getName().concat("/")),
                    config.getProject().getName(),
                    config.getProject().getBranch(),
                    config.getProject().getDescription(),
                    config.getProject().getUserName(),
                    config.getProject().getAccessToken());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void commitPrepare(Change cf, Path path, List<String> files) {

        try {
            Path pathFile = Path.of(path.toString().concat("/").concat(cf.getPath()));
            files.add(cf.getPath());
            System.out.println("> Commit type: ".concat(cf.getCommitType().toString()));
            System.out.println("> File modify: ".concat(pathFile.toString()));

            if (cf.getCommitType().toString().equalsIgnoreCase(TypeFileModification.ADD.toString())) {
                commitTypeAdd(cf, pathFile);

            } else if (cf.getCommitType().toString().equalsIgnoreCase(TypeFileModification.ALTER.toString())) {
                commitTypeAlter(cf, pathFile);

            } else if (cf.getCommitType().toString().equalsIgnoreCase(TypeFileModification.DELETE.toString())) {
                commitTypeDeleteLine(cf, pathFile);

            } else if (cf.getCommitType().toString().equalsIgnoreCase(TypeFileModification.DELETE_FILE.toString())) {
                commitTypeDeleteFile(pathFile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void commitTypeAdd(Change cf, Path path) throws IOException {
        FileUtil.create(path);
        FileUtil.modifyFile(path, List.of(cf.getNewContent()), cf.getInitLine(), cf.getFinalLine(), TypeFileModification.ADD);
    }

    private static void commitTypeAlter(Change cf, Path path) throws IOException {
        FileUtil.modifyFile(path, List.of(cf.getNewContent().split(System.lineSeparator())),
                cf.getInitLine(), cf.getFinalLine(), TypeFileModification.ALTER);
    }

    private static void commitTypeDeleteLine(Change cf, Path path) throws IOException {
        FileUtil.modifyFile(path, List.of(), cf.getInitLine(), cf.getFinalLine(), TypeFileModification.DELETE);
    }

    private static void commitTypeDeleteFile(Path path) {
        FileUtil.deleteFile(String.valueOf(path));
    }
}

//FIXME: remove this class intern
class ProcessPushUtil {

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

        if(createRepositoryRemote(userName, accessToken,nameRepository,description)){
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }
            runCommand(builder(pathRepositoryLocal, nameRepository,  branch, userName, accessToken));
        }
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