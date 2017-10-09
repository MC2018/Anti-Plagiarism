import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.swing.JFileChooser;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * 
 * @author Maikelele
 */
public class AntiPlagiarism extends javax.swing.JFrame {

    File[] fileDirectories;
    String[][] fileLines;
    String[][] fileLinesPrimaryFunction;
    int[] fileLengths;
    String[] fileFunctions;
    int[][] fileFunctionsCounter;
    int[] fileFunctionsTotal;
    String[][] functions = new String[0][2];
    
    String[][] text = new String[1][6];
    String[] textNames = {"Summary"};
    int textIndex = -1;
    int currentTextIndex = 1;
    
    boolean inComment = false;
    boolean inQuotations = false;
    
    String updateLink = "https://pastebin.com/raw/1eAUcaQb";
    String downloadURL;
    String version = "1.0_0";
    
    public void saveUrl(final String filename, final String urlString) throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }
    
    public String[] readDocument(String directory) {
        String[] document = new String[10000];

        for (int i = 0; i < document.length; i++) {
            document[i] = "";
        }

        try {
            FileReader reader;
            String wordLoader = "";
            int counter = 0;
            int character = ' ';
            int lastCharacter = ' ';
            
            reader = new FileReader(directory);

            while ((character = reader.read()) != -1) {
                if ((char) lastCharacter == '\n') {
                    counter++;
                }

                if ((char) character != 10 && character != 13) {
                    wordLoader += (char) character;
                    document[counter] = wordLoader;
                } else {
                    wordLoader = "";
                }

                lastCharacter = character;
            }
            reader.close();
            return document;
        } catch (Exception e) {
            System.out.println("Problem in readDocument\n" + e);
            return null;
        }
    }
    
    public AntiPlagiarism() {
        initComponents();
        //resultsTextArea.setEditable(false);
        resultsTextArea.setVisible(false);
        updateButton.setVisible(false);
        directoryButton.requestFocus();
        
        
        try {
            saveUrl("latest_version.txt", "https://pastebin.com/raw/1eAUcaQb");
            String[] version = readDocument("latest_version.txt");
            
            if (version != null && !version[0].equals(this.version)) {
                versionLabel.setText("<html>Outdated client<br>Version " + this.version + "</html>");
                updateButton.setVisible(true);
            }
            
            if (version != null && version.length > 1) {
                downloadURL = version[1].replaceAll("dropbox.com", "dl.dropboxusercontent.com");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        
        
        
        try {
            File functions = new File("functions.txt");
            
            if (!functions.exists()) {
                functions.createNewFile();
                
                FileWriter functionWriter = new FileWriter(functions);
                functionWriter.write(
                        "break ~break\n"
                        + "break;~break\n"
                        + "continue ~continue\n"
                        + "continue;~continue\n"
                        + "return ~return\n"
                        + "return;~return\n"
                        + "do ~do\n"
                        + "do{~do\n"
                        + "for ~for\n"
                        + "for{~for\n"
                        + "if ~if\n"
                        + "if{~if\n"
                        + "else if ~else if\n"
                        + "else if{~else if\n"
                        + "else ~else\n"
                        + "else{~else\n"
                        + "try ~try\n"
                        + "try{~try\n"
                        + "catch ~catch\n"
                        + "catch{~catch\n"
                        + "finally ~finally\n"
                        + "finally{~finally\n"
                        + "while ~while\n"
                        + "while{~while\n"
                        + "switch ~switch\n"
                        + "switch(~switch\n"
                        + "case ~switch case\n"
                        + "#System.out.println~print line\n"
                        + "#System.out.print~print\n"
                        + "import ~import\n"
                        + "public ~public class/method definition\n"
                        + "private ~private class/method definition\n"
                        + "//~\n"
                        + "+~concatenation");
                functionWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void reset() {
        fileDirectories = null;
        fileLines = null;
        fileLinesPrimaryFunction = null;
        fileLengths = null;
        fileFunctions = null;
        fileFunctionsCounter = null;
        fileFunctionsTotal = null;
        functions = new String[0][2];
        text = new String[1][6];
        textNames = new String[1];
        textNames[0] = "Summary";
        
        try {
            text[0][0] = "Work in Progress";
            text[0][1] = "";
            text[0][2] = "";
            text[0][3] = "";
            text[0][4] = "";
            text[0][5] = "";
            
        } catch (Exception e) {
            // this try catch shows something that is temporary; anything in the try catch is not needed
        }
        
        textIndex = 0;
        currentTextIndex = 1;
        resultsTextArea.setText("");
    }
    
    /*
    Class name
    Object names
    Method names
    Method parameters
    Comments
    Strings - for printing, just remove everything in the parentheses
     */
    
    //Make things infinitely expandable and not just breaking at line 10000
    
    //Potentially not have fileLines be a set index
    //In the future, make the character splitting the fileLines with their classifying name be defined somewhere in the document
    //Create a functions document with the default functions inside of it if the file does not exist
    //Potentially count number of important characters used in programming files ie semicolons, brackets, braces, parentheses. Only one of each pair needs to be counted
    //Create summary page that is the first page, giving an analysis of who the system believes plagiarized
    //Potentially change the project to read until semicolons or brackets are given in the case that multiple functions are on one line
    
    public String removeSpaces(String fileLine) {
        while (fileLine.length() > 0 && fileLine.charAt(0) == ' ') {
            fileLine = fileLine.replaceFirst(" ", "");
        }
        
        return fileLine;
    }
    
    public void readFunctionsFile() {
        String[][] document = new String[10000][2];
        String directory = "functions.txt";
        
        for (int i = 0; i < document.length; i++) {
            for (int j = 0; j < document[i].length; j++) {
                document[i][j] = "";
            }
        }

        try {
            FileReader reader;
            String wordLoader = "";
            int counter = 0;
            int character = ' ';
            int lastCharacter = ' ';
            int index = 0;

            reader = new FileReader(directory);

            while ((character = reader.read()) != -1) {
                if ((char) lastCharacter == '\n') {
                    counter++;
                    index = 0;
                }

                if ((char) character != '\n' && character != 10 && character != 13) {
                    if ((char) character != '~' || index == 1) {
                        wordLoader += (char) character;
                        document[counter][index] = wordLoader;
                    } else {
                        index = 1;
                        wordLoader = "";
                    }
                } else {
                    wordLoader = "";
                }

                lastCharacter = character;
            }
            reader.close();
            
            for (int i = 0; i < document.length; i++) {
                boolean hasEnded = false;
                
                for (int j = 0; j < document[i].length; j++) {
                    if (document[i][j].equals("")) {
                        hasEnded = true;
                        break;
                    } else {
                        if (j == 0) {
                            //System.out.print(document[i][j]);
                        } else {
                            //System.out.println(":" + document[i][j]);
                        }
                    }
                }
                
                if (hasEnded) {
                    break;
                }
            }
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(this, "Problem while reading documents", "Error", JOptionPane.ERROR);
            e.printStackTrace();
        }
        
        int totalCount = 0;
        
        for (int i = 0; i < document.length; i++) {
            if (!document[i][0].equals("")) {
                totalCount++;
            }
        }
        
        functions = new String[totalCount][2];
        int counter = 0;
        
        for (int i = 0; i < document.length && counter < totalCount; i++) {
            if (!document.equals("")) {
                functions[counter][0] = document[i][0];
                functions[counter][1] = document[i][1];
                counter++;
            }
        }
        
    }
    
    public File[] appendFile(File[] files, File file) {
        File[] newFiles = new File[files.length + 1];
        
        for (int i = 0; i < files.length; i++) {
            newFiles[i] = files[i];
        }
        
        newFiles[files.length] = file;
        return newFiles;
    }
    
    public void readFiles() {
        try {
            File directory = new File(directoryTextField.getText());
            fileDirectories = directory.listFiles();
            
            if (fileExtensionCheckBox.isSelected()) {
                File[] tempFileDirectories = new File[0];

                for (File fileDirectory : fileDirectories) {
                    if (fileDirectory.getAbsolutePath().endsWith(fileExtensionTextField.getText())) {
                        tempFileDirectories = appendFile(tempFileDirectories, fileDirectory);
                    }
                }

                fileDirectories = tempFileDirectories;
            }
            
            fileLines = new String[fileDirectories.length][10000];
            fileLengths = new int[fileDirectories.length];

            for (int i = 0; i < fileLengths.length; i++) {
                fileLengths[i] = 0;
            }

            for (int i = 0; i < fileDirectories.length; i++) {
                try {
                    FileReader reader;
                    String wordLoader = "";
                    int counter = 0;
                    int character = ' ';
                    int lastCharacter = ' ';

                    reader = new FileReader(fileDirectories[i]);
                    fileLines[i][0] = "";

                    while ((character = reader.read()) != -1) {
                        if ((char) lastCharacter == '\n') {
                            fileLengths[i]++;
                            counter++;
                            fileLines[i][counter] = "";
                        }

                        if ((char) character != '\n' && character != 13) {
                            wordLoader += (char) character;
                            fileLines[i][counter] = wordLoader;
                        } else {
                            wordLoader = "";
                        }

                        lastCharacter = character;
                    }
                    reader.close();
                } catch (Exception e) {

                }
            }
            
            for (int i = 0; i < fileLines.length; i++) {
                for (int j = 0; j < fileLengths[i]; j++) {
                    fileLines[i][j] = fileLines[i][j].replaceAll(Character.toString((char) 9), "");
                    fileLines[i][j] = removeSpaces(fileLines[i][j]);
                }
            }

            for (int i = 0; i < fileLines.length; i++) {
                for (int j = 0; j < fileLengths[i]; j++) {
                    //System.out.println(files[i][j]);
                }
            }
        } catch (NullPointerException e) {
            resultsTextArea.setVisible(false);
            JOptionPane.showMessageDialog(this, "Invalid directory or no files could be found", "Error", 0);
        }
    }
    
    public boolean fileLineMassStartsWith(String fileLine, String chars) {
        for (char character : chars.toCharArray()) {
            if (fileLine.startsWith(Character.toString(character))) {
                return true;
            }
        }
        
        return false;
    }
    
    public String removeSpacing(String fileLine) {
        while (fileLineMassStartsWith(fileLine, "	 {}();")) {
            //for (char character : (new String("	 {}();").toCharArray())) {
                //if (fileLine.startsWith(Character.toString(character))) {
                    
                    //fileLine = fileLine.replaceFirst(Character.toString(character), "");
                    
                //}
            //}
            switch (fileLine.charAt(0)) {
                case '	':
                    fileLine = fileLine.replaceFirst("	", "");
                    break;
                case ' ':
                    fileLine = fileLine.replaceFirst(" ", "");
                    break;
                case '{':
                    fileLine = fileLine.replaceFirst("{", "");
                    break;
                case '}':
                    fileLine = fileLine.replaceFirst("}", "");
                    break;
                case '(':
                    fileLine = fileLine.replaceFirst("\\(", "");
                    break;
                case ')':
                    fileLine = fileLine.replaceFirst("\\)", "");
                    break;
                case ';':
                    fileLine = fileLine.replaceFirst(";", "");
                    break;
                
            }
            
        }
        
        return fileLine;
    }
    
    public String setPrimaryFunction(String fileLine) {
        boolean containsSpace = false;
        fileLine = removeSpacing(fileLine);
        
        for (int i = 0; i < fileLine.length(); i++) {
            try {
                if (i != 0 && fileLine.charAt(i - 1) != '\\' && fileLine.charAt(i - 1) != '"') {
                    inQuotations = !inQuotations;
                } else if (i != 0 && fileLine.charAt(i - 1) == '/' && fileLine.charAt(i) == '*') {
                    //inComment = true;
                } else if (i != 0 && fileLine.charAt(i - 1) == '*' && fileLine.charAt(i) == '/') {
                    //inComment = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (inComment) {
            return "in comment";
        }
        
        for (int i = 0; i < functions.length; i++) {
            if (fileLine.startsWith(functions[i][0])) {
                if (functions[i][0].startsWith("for") && fileLine.contains(":")) {
                    return "advanced " + functions[i][1];
                } else {
                    return functions[i][1];
                }
            } else if (functions[i][0].startsWith("#") && fileLine.replaceAll(" ", "").startsWith(functions[i][0].replaceFirst("$", ""))) {
                return functions[i][1];
            }
        }
        
        for (int i = 0; i < fileLine.length(); i++) {
            if (fileLine.charAt(i) == '=' || (i != 0 && fileLine.charAt(i - 1) == '+' && fileLine.charAt(i) == '+') || (i != 0 && fileLine.charAt(i - 1) == '-' && fileLine.charAt(i) == '-')) {
                return "object/data type definition";
            } else if (fileLine.charAt(i) == '(') {
                return "method call";
            } else if (fileLine.charAt(i) == ' ') {
                containsSpace = true;
            } else if (containsSpace && fileLine.charAt(i) == ';') {
                return "object/data type creation";
            }
        }
        
        if (fileLine.equals("")) {
            return "";
        }
        
        return "other: " + fileLine;
    }
    
    public String[] appendArray(String[] array, String value) {
        String[] newArray = new String[array.length + 1];
        
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        
        newArray[array.length] = value;
        return newArray;
    }
    
    public void increaseIndex() {
        String[][] newText = new String[text.length + 1][6];
        String[] newTextNames = new String[text.length + 1];
        
        for (int i = 0; i < text.length; i++) {
            for (int j = 0; j < newText[i].length; j++) {
                newText[i][j] = text[i][j];
                newTextNames[i] = textNames[i];
            }
        }
        
        for (int i = 0; i < newText[text.length].length; i++) {
            newText[text.length][i] = "";
        }
        
        newTextNames[text.length] = "";
        text = newText;
        textNames = newTextNames;
        textIndex++;
    }
    
    public void decreaseIndex() {
        String[][] newText = new String[text.length - 1][text[text.length - 1].length];
        String[] newTextNames = new String[text.length - 1];
        
        for (int i = 0; i < newText.length; i++) {
            for (int j = 0; j < newText[i].length; j++) {
                newText[i][j] = text[i][j];
                newTextNames[i] = textNames[i];
            }
        }
        
        text = newText;
        textNames = newTextNames;
        textIndex--;
    }
    
    public String[] getRelativeCharacteristics(String[] characteristics) {
        String[] newCharacteristics = {};
        
        //try {
            if (characteristics[0] == null) {
                
            }
        //} catch (Exception e) {
            
        //}
        
        for (int i = 0; i < characteristics.length; i++) {
            if (characteristics[i] == null) {
                characteristics[i] = "";
            }
            
            if (!characteristics[i].startsWith("other") && !characteristics[i].equals("")) {
                newCharacteristics = appendArray(newCharacteristics, characteristics[i]);
            }
        }
        
        return newCharacteristics;
    }
    
    public void calculateFileFunctions() {
        fileFunctions = new String[0];
        boolean newCharacteristicFound = true;
        
        for (int i = 0; i < fileLengths.length; i++) {
            for (int j = 0; j < fileLengths[i]; j++) {
                newCharacteristicFound = true;
                for (String characteristic : fileFunctions) {
                    if (fileLinesPrimaryFunction[i][j].equals(characteristic)) {
                        newCharacteristicFound = false;
                        break;
                    }
                }
                //System.out.println(primaryUse[i][j]);
                if (newCharacteristicFound && !fileLinesPrimaryFunction[i][j].equals("") && !fileLinesPrimaryFunction[i][j].startsWith("other")) {
                    fileFunctions = appendArray(fileFunctions, fileLinesPrimaryFunction[i][j]);
                }
            }
        }
        
        Arrays.sort(fileFunctions);
        
        fileFunctionsCounter = new int[fileLinesPrimaryFunction.length][fileFunctions.length];
        fileFunctionsTotal = new int[fileLinesPrimaryFunction.length];
        
        for (int i = 0; i < fileFunctionsCounter.length; i++) {
            fileFunctionsTotal[i] = 0;
            
            for (int j = 0; j < fileFunctionsCounter[i].length; j++) {
                fileFunctionsCounter[i][j] = 0;
            }
        }
        
        for (int i = 0; i < fileLengths.length; i++) {
            for (int j = 0; j < fileLengths[i]; j++) {
                for (int k = 0; k < fileFunctions.length; k++) {
                    if (fileLinesPrimaryFunction[i][j].equals(fileFunctions[k])) {
                        fileFunctionsCounter[i][k]++;
                        fileFunctionsTotal[i]++;
                    }
                }
            }
        }
    }
    
    public void calculateStatistics() {
        for (int i = 0; i < fileLengths.length; i++) {
            for (int j = i + 1; j < fileLengths.length; j++) {
                double relativeLengthPercentage;
                int[] functionSimilarity = {0, 0};
                
                increaseIndex();
                textNames[textIndex] = fileDirectories[i].getAbsolutePath().replace(directoryTextField.getText() + "\\", "").replace(directoryTextField.getText() + "/", "")
                            + " and " + fileDirectories[j].getAbsolutePath().replace(directoryTextField.getText() + "\\", "").replace(directoryTextField.getText() + "/", "");
                
                
                
                if (fileFunctionsTotal[i] < fileFunctionsTotal[j]) {
                    relativeLengthPercentage = (double) fileFunctionsTotal[i] / fileFunctionsTotal[j];
                } else {
                    relativeLengthPercentage = (double) fileFunctionsTotal[j] / fileFunctionsTotal[j];
                }

                text[textIndex][0] += "Relative length similarity: " + ((int) (relativeLengthPercentage * 100)) + "." + ((int) (relativeLengthPercentage * 1000 % 10)) + "%\n";
                
                
                for (int k = 0; k < fileFunctions.length; k++) {

                    if (fileFunctionsCounter[i][k] == 0 && fileFunctionsCounter[j][k] == 0) {
                    } else if ((fileFunctionsCounter[i][k] == 0 && fileFunctionsCounter[j][k] != 0) || (fileFunctionsCounter[j][k] == 0 && fileFunctionsCounter[i][k] != 0)) {
                        text[textIndex][4] += (fileFunctionsCounter[i][k] + "\t"
                                + fileFunctionsCounter[j][k] + "\t"
                                + "One uses " + fileFunctions[k] + " while the other does not\n");

                        if (fileFunctionsCounter[i][k] == 0) {
                            functionSimilarity[1] += fileFunctionsCounter[j][k]; // times 2???
                        } else {
                            functionSimilarity[1] += fileFunctionsCounter[i][k]; // times 2???
                        }
                    } else {
                        
                        double percentSimilar;
                        
                        if (fileFunctionsCounter[i][k] < fileFunctionsCounter[j][k]) {
                            percentSimilar = (double) fileFunctionsCounter[i][k] / fileFunctionsCounter[j][k];
                            functionSimilarity[0] += fileFunctionsCounter[i][k];
                            functionSimilarity[1] += fileFunctionsCounter[j][k];
                        } else {
                            percentSimilar = (double) fileFunctionsCounter[j][k] / fileFunctionsCounter[i][k];
                            functionSimilarity[0] += fileFunctionsCounter[j][k];
                            functionSimilarity[1] += fileFunctionsCounter[i][k];
                        }

                        text[textIndex][4] += (fileFunctionsCounter[i][k] + "\t"
                                + fileFunctionsCounter[j][k] + "\tThey use "
                                + ((int) (percentSimilar * 100)) + "." + ((int) (percentSimilar * 1000 % 10)) + "% of the same amount of " + fileFunctions[k] + "\n");
                    }
                }
                
                int temp1 = 0;
                
                for (int k = 0; k < fileFunctionsCounter[i].length; k++) {
                    temp1 += fileFunctionsCounter[i][k];
                }
                
                int temp2 = 0;
                
                for (int k = 0; k < fileFunctionsCounter[j].length; k++) {
                    temp2 += fileFunctionsCounter[j][k];
                }
                
                text[textIndex][4] += (temp1 + "\t" + temp2 + "\tTotal amount");
                
                
                
                try {
                    text[textIndex][1] += ("Function similarity percentage: " + (int) ((double) functionSimilarity[0] / functionSimilarity[1] * 100)
                        + "." + (int) ((double) (functionSimilarity[0] / functionSimilarity[1] * 1000) % 10) + "%\n");
                } catch (ArithmeticException e) {
                    text[textIndex][1] += ("Function similarity percentage: 0.0%\n");
                }
                
                String[] iRelativeCharacteristics = getRelativeCharacteristics(fileLinesPrimaryFunction[i]);
                String[] jRelativeCharacteristics = getRelativeCharacteristics(fileLinesPrimaryFunction[j]);

                int maxSimilarities = 0;
                int maxSimilaritiesStartingPosition = -1;
                int currentSimilarities = 0;
                int currentOffset = 0;

                for (int k = 0; k < iRelativeCharacteristics.length; k++) {
                    currentSimilarities = 0;
                    currentOffset = -1;

                    for (int l = 0; l < jRelativeCharacteristics.length; l++) {
                        currentOffset++;

                        if (k + currentOffset < iRelativeCharacteristics.length && iRelativeCharacteristics[k + currentOffset].equals(jRelativeCharacteristics[l])) {
                            currentSimilarities++;
                            //System.out.println(k + " " + l + " " + currentSimilarities + " " + jRelativeCharacteristics.length);

                            if (currentSimilarities > maxSimilarities) {
                                maxSimilarities = currentSimilarities;
                                maxSimilaritiesStartingPosition = k;
                            }
                        } else {
                            currentSimilarities = 0;
                        }
                    }
                }

                // max similarities 
                double systemGuess = ((double) maxSimilarities * 2 / ((double) (iRelativeCharacteristics.length + jRelativeCharacteristics.length) / 2));

                if (systemGuess > 1) {
                    systemGuess = 1;
                }

                systemGuess /= 5;
                systemGuess += (double) functionSimilarity[0] / functionSimilarity[1];
                systemGuess += relativeLengthPercentage;

                text[textIndex][2] += ("Max line of similarities between characteristics is " + maxSimilarities + " starting at relative position " + maxSimilaritiesStartingPosition) + "\n";

                text[textIndex][3] += ("System-calculated guess for file similarity: " + ((int) ((double) systemGuess / 0.022)) + "." + ((int) (systemGuess / 0.0022 % 10)) + "%\n\n");
                
                if (relativeLengthPercentage * 100 < Double.parseDouble(relativeLengthLabel.getText().replaceAll("%", ""))
                        || (double) functionSimilarity[0] / functionSimilarity[1] * 100 < Double.parseDouble(functionSimilarityLabel.getText().replaceAll("%", ""))
                        || ((int) ((double) systemGuess / 0.022)) < Double.parseDouble(systemGuessLabel.getText().replaceAll("%", ""))) {
                    decreaseIndex();
                }
            }
        }

        
        //find the percentage of same amount of characteristics used
        //potentially have all percentages run regardless of initial file similarity
        
        //System.out.println(files[0].replaceAll(" ", "").replaceAll("\n", "").replaceAll(Character.toString((char) 13), "").replaceAll(Character.toString((char) 9), "").replaceAll(";", " "));
        currentTextIndex = 0;
        resultsTextArea.setText(text[currentTextIndex][0]+ text[currentTextIndex][1] + text[currentTextIndex][2] + text[currentTextIndex][3]
                + text[currentTextIndex][4] + text[currentTextIndex][5]);
        filesLabel.setText(textNames[currentTextIndex]);
    }
    
    public void updatePageLabel() {
        pageLabel.setText((currentTextIndex + 1) + "/" + text.length);
    }
    
    //http://www.dummies.com/programming/java/common-java-statements/
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        filePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultsTextArea = new javax.swing.JTextArea();
        previousButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        filesLabel = new javax.swing.JLabel();
        pageLabel = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        titleSeparator = new javax.swing.JSeparator();
        directoryButton = new javax.swing.JButton();
        readButton = new javax.swing.JButton();
        directoryTextField = new javax.swing.JTextField();
        infoButton = new javax.swing.JButton();
        relativeLengthSlider = new javax.swing.JSlider();
        relativeLengthLabel = new javax.swing.JLabel();
        functionSimilaritySlider = new javax.swing.JSlider();
        functionSimilarityLabel = new javax.swing.JLabel();
        systemGuessSlider = new javax.swing.JSlider();
        systemGuessLabel = new javax.swing.JLabel();
        fileExtensionTextField = new javax.swing.JTextField();
        fileExtensionCheckBox = new javax.swing.JCheckBox();
        updateButton = new javax.swing.JButton();
        versionLabel = new javax.swing.JLabel();
        panelSeparator = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MC Anti Plagiarism");
        setIconImage((new ImageIcon(getClass().getResource("icon.png"))).getImage());

        resultsTextArea.setEditable(false);
        resultsTextArea.setColumns(20);
        resultsTextArea.setRows(5);
        jScrollPane1.setViewportView(resultsTextArea);

        previousButton.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        previousButton.setText("Previous");
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });

        nextButton.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        filesLabel.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        filesLabel.setText("Files Being Compared");

        pageLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        pageLabel.setText("0/0");

        javax.swing.GroupLayout filePanelLayout = new javax.swing.GroupLayout(filePanel);
        filePanel.setLayout(filePanelLayout);
        filePanelLayout.setHorizontalGroup(
            filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(filePanelLayout.createSequentialGroup()
                        .addComponent(previousButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 270, Short.MAX_VALUE)
                        .addComponent(pageLabel))
                    .addComponent(filesLabel))
                .addContainerGap())
        );

        filePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nextButton, previousButton});

        filePanelLayout.setVerticalGroup(
            filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(previousButton)
                        .addComponent(nextButton))
                    .addComponent(pageLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        titleLabel.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("MC Anti Plagiarism");

        directoryButton.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        directoryButton.setText("Select Directory");
        directoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directoryButtonActionPerformed(evt);
            }
        });

        readButton.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        readButton.setText("Read");
        readButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readButtonActionPerformed(evt);
            }
        });

        infoButton.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        infoButton.setText("Info");
        infoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonActionPerformed(evt);
            }
        });

        relativeLengthSlider.setValue(60);
        relativeLengthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                relativeLengthSliderStateChanged(evt);
            }
        });

        relativeLengthLabel.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        relativeLengthLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        relativeLengthLabel.setText("60%");
        relativeLengthLabel.setToolTipText("Relative length minimum percentage to be shown");

        functionSimilaritySlider.setToolTipText("");
        functionSimilaritySlider.setValue(80);
        functionSimilaritySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                functionSimilaritySliderStateChanged(evt);
            }
        });

        functionSimilarityLabel.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        functionSimilarityLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        functionSimilarityLabel.setText("80%");
        functionSimilarityLabel.setToolTipText("Function similarity minimum percentage to be shown");

        systemGuessSlider.setToolTipText("");
        systemGuessSlider.setValue(65);
        systemGuessSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                systemGuessSliderStateChanged(evt);
            }
        });

        systemGuessLabel.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        systemGuessLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        systemGuessLabel.setText("65%");
        systemGuessLabel.setToolTipText("System guess minimum percentage to be shown");

        fileExtensionTextField.setText(".java");

        fileExtensionCheckBox.setSelected(true);
        fileExtensionCheckBox.setText("Specific File Extension");
        fileExtensionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileExtensionCheckBoxActionPerformed(evt);
            }
        });

        updateButton.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        versionLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        versionLabel.setText("Version 1.0_0");
        versionLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout titlePanelLayout = new javax.swing.GroupLayout(titlePanel);
        titlePanel.setLayout(titlePanelLayout);
        titlePanelLayout.setHorizontalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(titlePanelLayout.createSequentialGroup()
                        .addComponent(directoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(titleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(titleSeparator)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titlePanelLayout.createSequentialGroup()
                        .addComponent(directoryButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(readButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titlePanelLayout.createSequentialGroup()
                        .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(systemGuessSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(functionSimilaritySlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(relativeLengthSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(relativeLengthLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(functionSimilarityLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(systemGuessLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(titlePanelLayout.createSequentialGroup()
                        .addComponent(fileExtensionCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fileExtensionTextField))
                    .addGroup(titlePanelLayout.createSequentialGroup()
                        .addComponent(versionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(updateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(infoButton)))
                .addContainerGap())
        );
        titlePanelLayout.setVerticalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titlePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(titlePanelLayout.createSequentialGroup()
                        .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(titlePanelLayout.createSequentialGroup()
                                .addComponent(titleLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(titleSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(directoryButton)
                                    .addComponent(readButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(directoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(fileExtensionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fileExtensionCheckBox))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(relativeLengthLabel))
                            .addComponent(relativeLengthSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(functionSimilarityLabel))
                    .addComponent(functionSimilaritySlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(systemGuessLabel)
                    .addComponent(systemGuessSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(infoButton)
                        .addComponent(updateButton))
                    .addComponent(versionLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        titlePanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {fileExtensionCheckBox, fileExtensionTextField});

        panelSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelSeparator)
            .addComponent(filePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(titlePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void directoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directoryButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            file = new File(file.getAbsolutePath());

            try {
                directoryTextField.setText(file.getAbsolutePath());
            } catch (Exception e) {
                System.err.println();
            }
        }
    }//GEN-LAST:event_directoryButtonActionPerformed

    private void readButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readButtonActionPerformed
        reset();
        resultsTextArea.setVisible(true);
        readFiles();
        readFunctionsFile();

        try {
            fileLinesPrimaryFunction = new String[fileLines.length][10000];

            for (int i = 0; i < fileLines.length; i++) {
                for (int j = 0; j < fileLengths[i]; j++) {
                    fileLinesPrimaryFunction[i][j] = setPrimaryFunction(fileLines[i][j]);
                    inQuotations = false;
                }

                inComment = false;
            }

            calculateFileFunctions();
            calculateStatistics();
            updatePageLabel();
        } catch (NullPointerException e) {
            // Likely a NullPointerException if the directory is not selected
        } catch (Exception e) {
            e.printStackTrace();
            resultsTextArea.setVisible(false);
        }
    }//GEN-LAST:event_readButtonActionPerformed

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
        if (currentTextIndex > 0) {
            currentTextIndex--;
        } else {
            currentTextIndex = text.length - 1;
        }
        
        updatePageLabel();
        
        resultsTextArea.setText(text[currentTextIndex][0] + text[currentTextIndex][1] + text[currentTextIndex][2]
        + text[currentTextIndex][3] + text[currentTextIndex][4] + text[currentTextIndex][5]);
        filesLabel.setText(textNames[currentTextIndex]);
    }//GEN-LAST:event_previousButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        if (currentTextIndex < text.length - 1) {
            currentTextIndex++;
        } else {
            currentTextIndex = 0;
        }
        
        updatePageLabel();
        
        resultsTextArea.setText(text[currentTextIndex][0] + text[currentTextIndex][1] + text[currentTextIndex][2]
        + text[currentTextIndex][3] + text[currentTextIndex][4] + text[currentTextIndex][5] + "\n");
        filesLabel.setText(textNames[currentTextIndex]);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        JOptionPane.showMessageDialog(this, "Developed by Max Clausius to catch plagiarisers in Java programming."
                + "\nAlthough it was made for Java projects, editing the functions.txt file used for this project allows"
                + "\nfor it to be compatible with any programming language."
                + "\n\nRelative length is the amount of lines in a file that has more than just formatting characters."
                + "\nA line that only has spaces, tabs, braces, brackets, etc. is not considered as an important line."
                + "\n\n");
    }//GEN-LAST:event_infoButtonActionPerformed

    private void relativeLengthSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_relativeLengthSliderStateChanged
        relativeLengthLabel.setText(relativeLengthSlider.getValue() + "%");
    }//GEN-LAST:event_relativeLengthSliderStateChanged

    private void functionSimilaritySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_functionSimilaritySliderStateChanged
        functionSimilarityLabel.setText(functionSimilaritySlider.getValue() + "%");
    }//GEN-LAST:event_functionSimilaritySliderStateChanged

    private void systemGuessSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_systemGuessSliderStateChanged
        systemGuessLabel.setText(systemGuessSlider.getValue() + "%");
    }//GEN-LAST:event_systemGuessSliderStateChanged

    private void fileExtensionCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileExtensionCheckBoxActionPerformed
        if (fileExtensionCheckBox.isSelected()) {
            fileExtensionTextField.setEditable(true);
            fileExtensionTextField.setVisible(true);
        } else {
            fileExtensionTextField.setEditable(false);
            fileExtensionTextField.setVisible(false);
        }
    }//GEN-LAST:event_fileExtensionCheckBoxActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        try {
            Desktop desktop = Desktop.getDesktop();
            URI url = new URI(downloadURL);
            desktop.browse(url);
        } catch (Exception e) {
            
        }
    }//GEN-LAST:event_updateButtonActionPerformed
    
    
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AntiPlagiarism.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AntiPlagiarism.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AntiPlagiarism.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AntiPlagiarism.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AntiPlagiarism().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton directoryButton;
    private javax.swing.JTextField directoryTextField;
    private javax.swing.JCheckBox fileExtensionCheckBox;
    private javax.swing.JTextField fileExtensionTextField;
    private javax.swing.JPanel filePanel;
    private javax.swing.JLabel filesLabel;
    private javax.swing.JLabel functionSimilarityLabel;
    private javax.swing.JSlider functionSimilaritySlider;
    private javax.swing.JButton infoButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton nextButton;
    private javax.swing.JLabel pageLabel;
    private javax.swing.JPanel panel;
    private javax.swing.JSeparator panelSeparator;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton readButton;
    private javax.swing.JLabel relativeLengthLabel;
    private javax.swing.JSlider relativeLengthSlider;
    private javax.swing.JTextArea resultsTextArea;
    private javax.swing.JLabel systemGuessLabel;
    private javax.swing.JSlider systemGuessSlider;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JSeparator titleSeparator;
    private javax.swing.JButton updateButton;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
}
