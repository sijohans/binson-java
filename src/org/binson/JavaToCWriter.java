package org.binson;

import org.binson.lowlevel.*;

import java.io.*;

import static java.lang.System.exit;

/**
 * Created by simonj on 2018-06-28.
 */
public class JavaToCWriter {

    private final Binson object;
    StringBuilder writerCode;
    StringBuilder parserCode;

    private JavaToCWriter(Binson b) {
        object = b;
        writerCode = new StringBuilder();
        parserCode = new StringBuilder();
    }

    private void go() throws IOException {
        Writer writer = new StringWriter();
        CWriterTestOutput output = new CWriterTestOutput(object, writer);
        output.begin();
        OutputWriter.writeToOutput(object, output);
        output.end();
        System.out.println(writer.toString());

    }

    public static void generateCCode(Binson b) throws IOException {
        if (b == null) {
            return;
        }
        new JavaToCWriter(b).go();
    }

    public static void main(String[] args) {


        if (args.length == 0) {
            Binson b = new Binson()
                    .put("c", "A")
                    .put("i", 20)
                    .put("o", "s")
                    .put("z", new Binson().put("A", "B").put("ch", new byte[]{0x01,0x02}));
            try {
                generateCCode(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        String inputFile = args[0];
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            exit(-1);
        }

        try {
            Binson b = Binson.fromBytes(inputStream);
            generateCCode(b);
        } catch (IOException e) {
            e.printStackTrace();
            exit(-1);
        } catch (BinsonFormatException e) {
            e.printStackTrace();
            exit(-1);
        }

    }

}
