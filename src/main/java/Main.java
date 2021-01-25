import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        XMLSlideShow ppt = new XMLSlideShow();

        List<String> pptFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get("C:/Users/Costel/Desktop/facultate/lectures"))) {
            pptFiles = paths
                    .map(path -> Files.isDirectory(path) ? path.toString() + '/' : path.toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Something went wrong!");
            e.printStackTrace();
        }

        for(String s : pptFiles) {
            if(s.contains(".ppt") & s.contains("lec")) {
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
        System.out.println("Merging done successfully");
        out.close();

    }
}
