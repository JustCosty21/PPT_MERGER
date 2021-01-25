import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
        } while(folderPath.equals(""));

        List<String> pptFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            pptFiles = paths
                    .map(path -> Files.isDirectory(path) ? path.toString() + '/' : path.toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }

        for(String s : pptFiles) {
            String fileExt = getExtension(s);
            if(fileExt.equals("pptx") & s.contains("lec")) {
                FileInputStream inStream = new FileInputStream(s);
                XMLSlideShow src = new XMLSlideShow(inStream);

                for(XSLFSlide slide : src.getSlides()) {
                    ppt.createSlide().importContent(slide);
                }
            }
        }

        String allLecturesCombined = "allLecturesCombined.pptx";

        //creating the file object
        FileOutputStream out = new FileOutputStream(allLecturesCombined);

        // saving the changes to a file
        ppt.write(out);
        out.close();
    }

    private static String checkPath(String path) {
        File file = new File(path);
        if(!file.exists()) {
            System.out.println("This folder does not exist");
            return "";
        }

        if(!file.isDirectory()) {
            System.out.println("This is a file, not a folder.");
            return "";
        }

        return path;
    }

    private static String getExtension(String s) {
        String extension = "";

        int i = s.lastIndexOf('.');
        if (i > 0) {
            extension = s.substring(i+1);
        }

        return extension;
    }
}
