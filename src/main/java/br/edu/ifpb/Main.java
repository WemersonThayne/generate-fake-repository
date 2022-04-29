package br.edu.ifpb;

import br.edu.ifpb.enums.CommitType;
import br.edu.ifpb.exceptions.GitException;
import br.edu.ifpb.git.Git;
import br.edu.ifpb.git.GitApi;
import br.edu.ifpb.model.Change;
import br.edu.ifpb.model.ConfigWrapper;
import br.edu.ifpb.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String pathConfigFile = List.of(args).get(0);
        try {
            logger.info("Loading file configure with commits");
            ConfigWrapper config =  builderConfigWrapper(pathConfigFile);

           logger.info("Loading file configure completely success");
            Path path = Path.of(config.getProject().getPath(), config.getProject().getName());

            logger.info("Initial process create fake repository....");
            initRepository(path);

            config.getCommits().forEach(c -> {
                logger.info("###################################################");
                logger.info("#### Commit message: {} - developer: {} - {} ####",
                        c.getMessage(), c.getDeveloper().getName(), c.getDeveloper().getEmail());

                List<String> files = new ArrayList<>();
                c.getChanges().forEach(cf -> commitPrepare(cf, path, files));

                try {
                    Git.gitStage(path);
                    Git.gitCommit(path,c.getMessage(),c.getDeveloper());
                    Thread.sleep(5000);
                } catch (IOException | InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new GitException(e.getMessage());
                }
              logger.info("Commit success.");

            });

            pushProcess(path, config);

        } catch (IOException | InterruptedException e){
            logger.error("Error to process config file: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static void pushProcess(Path path,ConfigWrapper config) throws IOException, InterruptedException {
        if(GitApi.createRepositoryRemote(config.getProject().getUserName(), config.getProject().getAccessToken(),
                config.getProject().getName(), config.getProject().getDescription())){
            Git.gitAddRemoteOrigin(path,config.getProject().getUserName(),config.getProject().getName());
            Git.gitPushWithTokenAuth(path,config.getProject().getName(),config.getProject().getUserName(),
                    config.getProject().getAccessToken());
        }
    }

    private static void initRepository(Path path) throws IOException, InterruptedException {
        if(!FileUtil.isFileExists(path)){
            Files.createDirectories(path);
        }
        Git.gitInit(path);
    }
    private static ConfigWrapper builderConfigWrapper(String pathConfigFile){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            return mapper.readValue(new File(pathConfigFile), ConfigWrapper.class);
        } catch (IOException e) {
            throw new GitException("Error to read config file, error: "+e.getMessage());
        }
    }

    public static void commitPrepare(Change cf, Path path, List<String> files) {
        try {

            Path pathFile = path.resolve(cf.getPath());
            files.add(cf.getPath());
            logger.info("> Commit type: {}", cf.getCommitType());
            logger.info("> File modify: {}",pathFile);

            if (cf.getCommitType().toString().equalsIgnoreCase(CommitType.ADD.toString())) {
                commitTypeAdd(cf, pathFile);
            } else if (cf.getCommitType().toString().equalsIgnoreCase(CommitType.ALTER_ALL.toString())) {
                commitTypeAlterAll(cf, pathFile);
            } else if (cf.getCommitType().toString().equalsIgnoreCase(CommitType.ALTER_ADD_NEW_LINE.toString())) {
                commitTypeAlterAddNewLine(cf, pathFile);
            } else if (cf.getCommitType().toString().equalsIgnoreCase(CommitType.ALTER_CHANGE_LINE_ACTUAL_ADD_NEW.toString())) {
                commitTypeAlterChangesLinesActualAddNewLine(cf, pathFile);
            } else if (cf.getCommitType().toString().equalsIgnoreCase(CommitType.DELETE_LINE.toString())) {
                commitTypeDeleteLine(cf, pathFile);
            } else if (cf.getCommitType().toString().equalsIgnoreCase(CommitType.DELETE_FILE.toString())) {
                commitTypeDeleteFile(pathFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void commitTypeAdd(Change cf, Path path) throws IOException {
        FileUtil.create(path);
        FileUtil.modifyFile(path, List.of(cf.getNewContent()), cf.getInitLine(), cf.getFinalLine(), CommitType.ADD);
    }
    private static void commitTypeAlterAll(Change cf, Path path) throws IOException {
        FileUtil.modifyFile(path, List.of(cf.getNewContent().split(System.lineSeparator())),
                cf.getInitLine(), cf.getFinalLine(), CommitType.ALTER_ALL);
    }
    private static void commitTypeAlterAddNewLine(Change cf, Path path) throws IOException {
        FileUtil.modifyFile(path, List.of(cf.getNewContent().split(System.lineSeparator())),
                cf.getInitLine(), cf.getFinalLine(), CommitType.ALTER_ADD_NEW_LINE);
    }
    private static void commitTypeAlterChangesLinesActualAddNewLine(Change cf, Path path) throws IOException {
        FileUtil.modifyFile(path, List.of(cf.getNewContent().split(System.lineSeparator())),
                cf.getInitLine(), cf.getFinalLine(), CommitType.ALTER_CHANGE_LINE_ACTUAL_ADD_NEW);
    }
    private static void commitTypeDeleteLine(Change cf, Path path) throws IOException {
        FileUtil.modifyFile(path, List.of(), cf.getInitLine(), cf.getFinalLine(), CommitType.DELETE_LINE);
    }
    private static void commitTypeDeleteFile(Path path) {
        FileUtil.deleteFile(path);
    }
}
