package br.edu.ifpb.util;

import br.edu.ifpb.enums.CommitType;
import br.edu.ifpb.exceptions.FileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	private FileUtil(){}

	public static void create(Path path)  {
		try {
			if (!path.toFile().exists()) {
				Files.createFile(path);
				logger.info("File create with success.");
			}
		} catch (IOException e) {
			logger.error("Error to create file: ", e);
		}
	}

	public static void modifyFile(Path path, List<String> newContent, int initLine, int finalLine,
			CommitType typeFileModification) throws IOException {
		if (isFileExists(path)) {
			List<String> contentActual = contentFile(path);
			switch (typeFileModification){
				case ADD:
					writeFile(path, newContent, StandardOpenOption.APPEND);
					break;
				case ALTER_ALL:
					alterAllContentFile(path, contentActual, newContent, finalLine);
					break;
				case ALTER_ADD_NEW_LINE:
					alterAddNewLineContentFile(path, contentActual, newContent, initLine, finalLine);
					break;
				case ALTER_CHANGE_LINE_ACTUAL_ADD_NEW:
					alterChangeLinesAddNewLineContentFile(path, contentActual, newContent, initLine, finalLine);
					break;
				case DELETE_LINE:
					removeLineInFile(path, contentActual, initLine, finalLine);
					break;
				default:
					logger.error("Invalid type modification file");
			}
		} else {
			logger.error("File not found");
		}
	}

	public static void deleteFile(Path path){
		try {
			if (Files.deleteIfExists(path)) {
				logger.info("Deleted the file: {}",path.getFileName());
			} else {
				logger.error("Failed to delete the file.");
			}
		} catch (IOException e) {
			throw new FileException(e.getMessage());
		}


	}
	private static void removeLineInFile(Path path, List<String> contentActual, int initLine, int finalLine) {
		List<String> cloneContentActual = new ArrayList<>(contentActual);
		if(initLine < contentActual.size() && finalLine <= contentActual.size() ){
			int j = finalLine-1;
			for(int i = initLine-1; i<= finalLine-1; i++ ){
				cloneContentActual.remove(j);
				j--;
			}
		}
		contentActual.clear();
		contentActual.addAll(cloneContentActual);
		writeFileFromRemoveLines(path, contentActual);

	}
	private static void writeFileFromRemoveLines(Path path, List<String> contentActual)  {
		if (isFileExists(path)) {
			try (BufferedWriter writer =  new BufferedWriter(new FileWriter(String.valueOf(path)))) {
				for (String line: contentActual) {
						writer.write(line);
						writer.newLine();
				}
			}catch (IOException e) {
				logger.error("Error to write file: {}, error:{}", path.getFileName(),e);
			}
		}
	}
	private static void alterAllContentFile(Path path, List<String> contentActual, List<String> newContent,
											int finalLine) {
		if(contentActual.size() < finalLine && newContent.size() > contentActual.size()){
			contentActual.clear();
			contentActual.addAll(newContent);
		}
		writeFile(path, contentActual,StandardOpenOption.WRITE);
	}
	private static void alterAddNewLineContentFile(Path path, List<String> contentActual, List<String> newContent,
												   int initLine, int finalLine) {
		if(initLine < contentActual.size() && finalLine <= contentActual.size() ){
			int j = 0;
			for(int i = initLine; i<= finalLine; i++ ){
				contentActual.add(i, newContent.get(j));
				j++;
			}
		} else if(initLine > contentActual.size() && finalLine > contentActual.size()){
			contentActual.addAll(newContent);
		}
		writeFile(path, contentActual,StandardOpenOption.WRITE);
	}
	private static void alterChangeLinesAddNewLineContentFile(Path path, List<String> contentActual, List<String> newContent,
											int initLine, int finalLine) {
		if(initLine < contentActual.size() && finalLine <= contentActual.size() ){
			int j = 0;
			for(int i = initLine-1; i <= finalLine-1; i++ ){
				contentActual.set(i, newContent.get(j));
				j++;
			}
		}
		writeFile(path, contentActual,StandardOpenOption.WRITE);
	}
	public static List<String> contentFile(Path path) throws IOException {
		List<String> content = Files.readAllLines(path);
		logger.info("Content file: {}", content);
		return content;
	}
	private static void writeFile(Path path, List<String> contentFile, StandardOpenOption option) {
		if (isFileExists(path)) {
			try (BufferedWriter bw = Files.newBufferedWriter(path, option)) {
				for (String c: contentFile) {
					bw.write(c);
					bw.newLine();
					bw.flush();
				}
			} catch (IOException e) {
				logger.error("Error to read Buffered: ", e);
			}
			logger.info("Write file success");
		} else {
			logger.error("File not found");
		}
	}
	public static boolean isFileExists(Path path) {
		return path.toFile().exists();
	}

}