import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private final static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow();

        String folderPath;
        do {
            System.out.println("Where is the folder with the presentations?");
            folderPath = sc.nextLine();

            folderPath = checkPath(folderPath);
        } while (folderPath.equals(""));

        List<String> pptFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            pptFiles = paths
                    .map(path -> Files.isDirectory(path) ? path.toString() + '/' : path.toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }

        for (String s : pptFiles) {
            String fileExt = getExtension(s);

            Path path = Paths.get("Desktop/pptxFiles");

            //java.nio.file.Files;
            Files.createDirectories(path);

            if (s.contains("lec")) {
                if (fileExt.equals("pptx")) {
                    FileInputStream inStream = new FileInputStream(s);
                    XMLSlideShow src = new XMLSlideShow(inStream);

                    for (XSLFSlide slide : src.getSlides()) {
                        ppt.createSlide().importContent(slide);
                    }
                } else if (fileExt.equals("ppt")) {
                    String file = convertFile(s);
                    FileInputStream inStream = new FileInputStream(file);
                    XMLSlideShow src = new XMLSlideShow(inStream);

                    for (XSLFSlide slide : src.getSlides()) {
                        ppt.createSlide().importContent(slide);
                    }
                }
            }
        }

        String outputLocation;
        do {
            System.out.println("Select a location to place the file");
            outputLocation = sc.nextLine();

        } while (outputLocation.equals(""));

        String allLecturesCombined = outputLocation + "/" + "allLecturesCombined.pptx";

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(allLecturesCombined);
        } catch (FileNotFoundException e) {
            System.out.println("Folder does not exist or something went wrong.");

            System.exit(0);
        }

        // saving the changes to a file
        ppt.write(out);
        out.close();

        System.out.println("Costel was here!");
    }

    private static String convertFile(String s) {
        File file = new File(s);

        // Instantiate a Presentation object that represents a PPTX file
        Presentation pres = new Presentation(file.getAbsolutePath());

        // Saving the PPTX presentation to PPTX format
        pres.save(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator)) + "/" + removeExtension(file.getName()) + ".pptx", SaveFormat.Pptx);

        return file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator)) + "/" + removeExtension(file.getName()) + ".pptx";
    }

    private static String removeExtension(String fileName) {
        String fileNameWithOutExt = fileName.replaceFirst("[.][^.]+$", "");

        return fileNameWithOutExt;
    }

    private static String checkPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("This folder does not exist");
            return "";
        }

        if (!file.isDirectory()) {
            System.out.println("This is a file, not a folder.");
            return "";
        }

        return path;
    }

    private static String getExtension(String s) {
        String extension = "";

        int i = s.lastIndexOf('.');
        if (i > 0) {
            extension = s.substring(i + 1);
        }

        return extension;
    }
}
