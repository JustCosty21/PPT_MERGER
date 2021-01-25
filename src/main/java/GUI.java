import javax.swing.*;

import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;

public class GUI extends Shell {
    private final Text text;
    private final Text text_1;
    private final Text text_2;
    private final Scanner sc = new Scanner(System.in);

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String args[]) {
        try {
            Display display = Display.getDefault();
            GUI shell = new GUI(display);
            shell.open();
            shell.layout();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the shell.
     * @param display
     */
    public GUI(Display display) {
        super(display, SWT.SHELL_TRIM);

        Label lblPptxFolder = new Label(this, SWT.NONE);
        lblPptxFolder.setBounds(10, 44, 90, 20);
        lblPptxFolder.setText("PPT(X) folder:");

        text = new Text(this, SWT.BORDER);
        text.setBounds(120, 41, 199, 26);

        Button btnBrowse = new Button(this, SWT.NONE);
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Browse the folder to process");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    text.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        btnBrowse.setBounds(337, 39, 85, 30);
        btnBrowse.setText("Browse");

        Label lblPdfFolder = new Label(this, SWT.NONE);
        lblPdfFolder.setBounds(10, 105, 90, 20);
        lblPdfFolder.setText("PDF folder:");

        text_1 = new Text(this, SWT.BORDER);
        text_1.setBounds(120, 102, 199, 26);

        Button btnBrowse_1 = new Button(this, SWT.NONE);
        btnBrowse_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Browse the folder to process");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    text_1.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        btnBrowse_1.setBounds(337, 100, 90, 30);
        btnBrowse_1.setText("Browse");

        Button btnMergePptx = new Button(this, SWT.NONE);
        btnMergePptx.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if(text.getText().isBlank() || text_2.getText().isBlank()) {
                    JOptionPane.showMessageDialog(null, "Please select a folder first!",
                            "Hey!", JOptionPane.ERROR_MESSAGE);
                    return ;
                }
                createPPTX(text.getText());
            }
        });

        btnMergePptx.setBounds(47, 213, 102, 30);
        btnMergePptx.setText("Merge PPT(X)");

        Button btnMergePdf = new Button(this, SWT.NONE);
        btnMergePdf.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                if(text_1.getText().isBlank() || text_2.getText().isBlank()) {
                    JOptionPane.showMessageDialog(null, "Please select a folder first!",
                            "Hey!", JOptionPane.ERROR_MESSAGE);
                    return ;
                }
                try {
                    mergePDF(text_1.getText(), text_2.getText());
                } catch (DocumentException | IOException documentException) {
                    documentException.printStackTrace();
                }
            }
        });
        btnMergePdf.setText("Merge PDF");
        btnMergePdf.setBounds(283, 213, 102, 30);

        text_2 = new Text(this, SWT.BORDER);
        text_2.setBounds(120, 148, 199, 26);

        Label lblOutputFolder = new Label(this, SWT.NONE);
        lblOutputFolder.setBounds(10, 151, 104, 20);
        lblOutputFolder.setText("Output folder:");

        Button btnBrowse_1_1 = new Button(this, SWT.NONE);
        btnBrowse_1_1.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setDialogTitle("Browse the folder to process");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    text_2.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        btnBrowse_1_1.setText("Browse");
        btnBrowse_1_1.setBounds(337, 144, 90, 30);
        createContents();
    }

    /**
     * Create contents of the shell.
     */
    protected void createContents() {
        setText("SWT Application");
        setSize(450, 300);

    }

    private void createPPTX(String folderPath) {
        XMLSlideShow ppt = new XMLSlideShow();

        List<String> pptFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            pptFiles = paths
                    .map(path -> Files.isDirectory(path) ? path.toString() + '/' : path.toString())
                    .collect(Collectors.toList());
        } catch (IOException e1) {
            System.out.println("Something went wrong!");
            e1.printStackTrace();
        }

        for (String s : pptFiles) {
            String fileExt = getExtension(s);

            if (s.contains("lec")) {
                if (fileExt.equals("pptx")) {
                    try {
                        ppt = appendPPT(s, ppt);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } else if (fileExt.equals("ppt")) {
                    String file = convertFile(s);
                    try {
                        ppt = appendPPT(file, ppt);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }

        String allLecturesCombined = text_2.getText() + "/" + "allLecturesCombined.pptx";

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(allLecturesCombined);
        } catch (FileNotFoundException e2) {
            System.out.println("Folder does not exist or something went wrong.");

            System.exit(0);
        }

        // saving the changes to a file
        try {
            ppt.write(out);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        System.out.println("Costel was here!");

        text_1.setText("");
        text_2.setText("");
        text.setText("");
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    private static XMLSlideShow appendPPT(String file, XMLSlideShow ppt) throws IOException {
        FileInputStream inStream = new FileInputStream(file);
        XMLSlideShow src = new XMLSlideShow(inStream);

        for (XSLFSlide slide : src.getSlides()) {
            ppt.createSlide().importContent(slide);
        }

        return ppt;
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

        return fileName.replaceFirst("[.][^.]+$", "");
    }

    private static String getExtension(String s) {
        String extension = "";

        int i = s.lastIndexOf('.');
        if (i > 0) {
            extension = s.substring(i + 1);
        }

        return extension;
    }

    private void mergePDF(String dirLoc, String outputLoc) throws DocumentException, IOException {
        File dir = new File(dirLoc);
        File[] filesToMerge = dir.listFiles((file, fileName) -> {
            //System.out.println(fileName);
            return fileName.endsWith(".pdf");
        });
        Document document = new Document();
        FileOutputStream outputStream = new FileOutputStream(outputLoc + "/pdfCombined.pdf");
        PdfCopy copy = new PdfSmartCopy(document, outputStream);
        document.open();

        assert filesToMerge != null;
        for (File inFile : filesToMerge) {
            System.out.println(inFile.getCanonicalPath());
            PdfReader reader = new PdfReader(inFile.getCanonicalPath());
            copy.addDocument(reader);
            reader.close();
        }
        document.close();
    }
}
