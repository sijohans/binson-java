package org.binson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

    private void go() {
        byte[] b = object.toBytes();

        writerCode.append("#include <assert.h>\n");
        writerCode.append("#include <string.h>\n");
        writerCode.append("#include \"binson_writer.h\"\n\n");
        writerCode.append("/*\n");
        writerCode.append(object.toPrettyJson());
        writerCode.append("*/\n");

        writerCode.append("int main(void)\n");
        writerCode.append("{\n");
        writerCode.append("    uint8_t expected[" + b.length +"] = \"" + byteRep(b) + "\";\n");
        writerCode.append("    uint8_t created[" + b.length + "];\n");
        writerCode.append("    binson_writer w;\n");
        writerCode.append("    binson_writer_init(&w, created, sizeof(created));\n");
        writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
        write(object);
        writerCode.append("    assert(memcmp(expected, created, sizeof(expected)) == 0);\n");
        writerCode.append("    return 0;\n");
        writerCode.append("}\n");
        System.out.println(writerCode.toString());

    }

    private void handleType(String field, Binson b) {

    }

    private String byteRep(String s) {
        byte[] value = s.getBytes();
        StringBuilder sb = new StringBuilder(value.length*4);
        for (byte b : value) {
            sb.append(String.format("\\x%02x", b));
        }
        return sb.toString();
    }

    private String byteRep(byte[] value) {
        StringBuilder sb = new StringBuilder(value.length*4);
        for (byte b : value) {
            sb.append(String.format("\\x%02x", b));
        }
        return sb.toString();
    }

    private void write(Binson b) {
        writerCode.append("    binson_write_object_begin(&w);\n");
        writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
        for (String field : b.keySet()) {
            writerCode.append("    binson_write_name(&w, \"" + byteRep(field) + "\");\n");
            writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
            if (b.hasObject(field) ) {
                write(b.getObject(field));
            } else if (b.hasArray(field)) {
                write(b.getArray(field));
            } else if (b.hasBoolean(field)) {
                write(b.getBoolean(field));
            } else if (b.hasBytes(field)) {
                write(b.getBytes(field));
            } else if (b.hasDouble(field)) {
                write(b.getDouble(field));
            } else if (b.hasInteger(field)) {
                write(b.getInteger(field));
            } else if (b.hasString(field)) {
                write(b.getString(field));
            } else {
                writerCode.append("ERRORERRORERROR");
            }
        }

        writerCode.append("    binson_write_object_end(&w);\n");
        writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    private void write(BinsonArray array) {
        writerCode.append("    binson_write_array_begin(&w);\n");
        writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
        for (int i = 0; i < array.size(); i++) {
            if (array.isObject(i) ) {
                write(array.getObject(i));
            } else if (array.isArray(i)) {
                write(array.getArray(i));
            } else if (array.isBoolean(i)) {
                write(array.getBoolean(i));
            } else if (array.isBytes(i)) {
                write(array.getBytes(i));
            } else if (array.isDouble(i)) {
                write(array.getDouble(i));
            } else if (array.isInteger(i)) {
                write(array.getInteger(i));
            } else if (array.isString(i)) {
                write(array.getString(i));
            } else {
                writerCode.append("ERRORERRORERROR");
            }
        }
        writerCode.append("    binson_write_array_end(&w);\n");
        writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    private void write(boolean value) {
        writerCode.append("    binson_write_boolean(&w, " + value +");\n");
        writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    private void write(String value) {
        /* TODO: Escape characted " with \" */
        writerCode.append("    binson_write_string(&w, \"" + byteRep(value) + "\");\n");
        writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    private void write(byte[] value) {
        writerCode.append("    binson_write_bytes(&w, \"" + byteRep(value) + "\", " + value.length +");\n");
        writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    private void write(double value) {
        writerCode.append("    binson_write_double(&w, " + value +");\n");
        writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    private void write(long value) {
        writerCode.append("    binson_write_integer(&w, " + value +");\n");
        writerCode.append("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    public static void generateCCode(Binson b) {
        if (b == null) {
            return;
        }
        new JavaToCWriter(b).go();
    }

    public static void main(String[] args) {


        if (args.length == 0) {
            Binson b = new Binson()
                    .put("A", "B")
                    .put("B", new BinsonArray()
                            .add("Hello world")
                            .add(new Binson().put("A", "B")));
            generateCCode(b);
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
