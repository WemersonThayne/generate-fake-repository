package br.edu.ifpb.git;

import br.edu.ifpb.model.Developer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Git {
    static Logger logger = LoggerFactory.getLogger(Git.class);

    private Git(){}

    // example of usage
    private static void initAndAddFile() throws IOException, InterruptedException {
        Path directory = Paths.get("c:\\temp\\example");
        Files.createDirectories(directory);
        gitInit(directory);
        Files.write(directory.resolve("example.txt"), new byte[0]);
        gitStage(directory);
        gitCommit(directory, "Add example.txt");
    }

    // example of usage
    private static void cloneAndAddFile() throws IOException, InterruptedException {
        String originUrl = "https://github.com/Crydust/TokenReplacer.git";
        Path directory = Paths.get("c:\\temp\\TokenReplacer");
        gitClone(directory, originUrl);
        Files.write(directory.resolve("example.txt"), new byte[0]);
        gitStage(directory);
        gitCommit(directory, "Add example.txt");
        gitPush(directory);
    }

    public static void gitInit(Path directory) throws IOException, InterruptedException {
        runCommand(directory, "git", "init");
    }

    public static void gitStage(Path directory) throws IOException, InterruptedException {
        runCommand(directory, "git", "add", ".");
    }

    public static void gitBranch(Path directory) throws IOException, InterruptedException {
        runCommand(directory, "git", "branch");
    }

    public static void gitRemoveBranch(Path directory, String branchName) throws IOException, InterruptedException {
        runCommand(directory, "git", "branch", "-d", branchName);
    }
    public static void gitCheckoutBranch(Path directory, String branchName) throws IOException, InterruptedException {
        runCommand(directory, "git", "checkout", branchName);
    }

    public  static void gitCreateBranch(Path directory, String branchName)  throws IOException, InterruptedException {
        runCommand(directory, "git", "checkout", "-b", branchName);
    }

    public static void gitCommit(Path directory, String message) throws IOException, InterruptedException {
        runCommand(directory, "git", "commit", "-m", message);
    }

    public static void gitCommit(Path directory, String message, Developer developer) throws IOException, InterruptedException {
        runCommand(directory, "git", "commit", "--author="+"\""+developer.getName()
                +"<"+developer.getEmail()+">\"","-m", message);
    }

    public static void gitAddRemoteOrigin(Path directory, String userName, String remoteRepository) throws IOException, InterruptedException {
        runCommand(directory,"git", "remote", "add", "origin", "https://github.com/"
                +userName+"/"+remoteRepository+".git");
    }
    public static void gitPush(Path directory) throws IOException, InterruptedException {
        runCommand(directory, "git", "push");
    }

    public static void gitPushWithTokenAuth(Path directory,String remoteRepository, String userName, String accessToken)
            throws IOException, InterruptedException {
        runCommand(directory, "git", "push", "https://"+userName+":"+accessToken+"@"+"github.com/"
                        +userName+"/"+remoteRepository+".git", "--all");
    }

    public static void gitLog(Path directory) throws IOException, InterruptedException {
        runCommand(directory, "git", "log");
    }

    public static void gitClone(Path directory, String originUrl) throws IOException, InterruptedException {
        runCommand(directory.getParent(), "git", "clone", originUrl, directory.getFileName().toString());
    }

    private static void runCommand(Path directory, String... command) throws IOException, InterruptedException {
        Objects.requireNonNull(directory, "directory");
        if (!Files.exists(directory)) {
            throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
        }
        ProcessBuilder pb = new ProcessBuilder()
                .command(command)
                .directory(directory.toFile());
        Process p = pb.start();
        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
        outputGobbler.start();
        errorGobbler.start();
        int exit = p.waitFor();
        errorGobbler.join();
        outputGobbler.join();
        if (exit != 0) {
            throw new AssertionError(String.format("runCommand returned %d", exit));
        }
    }
    private static class StreamGobbler extends Thread {
        private final InputStream is;
        private final String type;
        private StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
                String line;
                while ((line = br.readLine()) != null) {
                    logger.info("{}", line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}