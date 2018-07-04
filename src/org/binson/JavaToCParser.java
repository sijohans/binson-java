package org.binson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.exit;

/**
 * Created by simonj on 2018-06-28.
 */
public class JavaToCParser {

    private final Binson object;
    StringBuilder writerCode;

    private JavaToCParser(Binson b) {
        object = b;
        writerCode = new StringBuilder();
    }

    private void go() {
        byte[] b = object.toBytes();

        writerCode.append("#include <assert.h>\n");
        writerCode.append("#include \"binson_parser.h\"\n\n");
        writerCode.append("/*\n");
        writerCode.append(object.toPrettyJson());
        writerCode.append("*/\n");

        writerCode.append("int main(void)\n");
        writerCode.append("{\n");
        writerCode.append("    uint8_t binson_bytes[" + b.length +"] = \"" + byteRep(b) + "\";\n");
        writerCode.append("    binson_parser p;\n");
        writerCode.append("    double dval; (void) dval;\n");
        writerCode.append("    int64_t intval; (void) intval;\n");
        writerCode.append("    bool boolval; (void) boolval;\n");
        writerCode.append("    bbuf *rawval; (void) rawval;\n");
        writerCode.append("    binson_parser_init(&p, binson_bytes, sizeof(binson_bytes));\n");
        writerCode.append("    assert(binson_parser_verify(&p));\n");
        writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
        parse(object);
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

    private void parse(Binson b) {
        writerCode.append("    binson_parser_go_into_object(&p);\n");
        writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
        for (String field : b.keySet()) {
            if (b.hasObject(field) ) {
                writerCode.append("    binson_parser_field_ensure(&p, \"" + byteRep(field) + "\", BINSON_TYPE_OBJECT);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(b.getObject(field));
            } else if (b.hasArray(field)) {
                writerCode.append("    binson_parser_field_ensure(&p, \"" + byteRep(field) + "\", BINSON_TYPE_ARRAY);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(b.getArray(field));
            } else if (b.hasBoolean(field)) {
                writerCode.append("    binson_parser_field_ensure(&p, \"" + byteRep(field) + "\", BINSON_TYPE_BOOLEAN);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(b.getBoolean(field));
            } else if (b.hasBytes(field)) {
                writerCode.append("    binson_parser_field_ensure(&p, \"" + byteRep(field) + "\", BINSON_TYPE_BYTES);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(b.getBytes(field));
            } else if (b.hasDouble(field)) {
                writerCode.append("    binson_parser_field_ensure(&p, \"" + byteRep(field) + "\", BINSON_TYPE_DOUBLE);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(b.getDouble(field));
            } else if (b.hasInteger(field)) {
                writerCode.append("    binson_parser_field_ensure(&p, \"" + byteRep(field) + "\", BINSON_TYPE_INTEGER);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(b.getInteger(field));
            } else if (b.hasString(field)) {
                writerCode.append("    binson_parser_field_ensure(&p, \"" + byteRep(field) + "\", BINSON_TYPE_STRING);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(b.getString(field));
            } else {
                writerCode.append("ERRORERRORERROR");
            }
        }

        writerCode.append("    binson_parser_leave_object(&p);\n");
        writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
    }

    private void parse(BinsonArray array) {
        writerCode.append("    binson_parser_go_into_array(&p);\n");
        writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
        for (int i = 0; i < array.size(); i++) {
            if (array.isObject(i) ) {
                writerCode.append("    binson_parser_next_ensure(&p, BINSON_TYPE_OBJECT);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(array.getObject(i));
            } else if (array.isArray(i)) {
                writerCode.append("    binson_parser_next_ensure(&p, BINSON_TYPE_ARRAY);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(array.getArray(i));
            } else if (array.isBoolean(i)) {
                writerCode.append("    binson_parser_next_ensure(&p, BINSON_TYPE_BOOLEAN);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(array.getBoolean(i));
            } else if (array.isBytes(i)) {
                writerCode.append("    binson_parser_next_ensure(&p, BINSON_TYPE_BYTES);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(array.getBytes(i));
            } else if (array.isDouble(i)) {
                writerCode.append("    binson_parser_next_ensure(&p, BINSON_TYPE_DOUBLE);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(array.getDouble(i));
            } else if (array.isInteger(i)) {
                writerCode.append("    binson_parser_next_ensure(&p, BINSON_TYPE_INTEGER);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(array.getInteger(i));
            } else if (array.isString(i)) {
                writerCode.append("    binson_parser_next_ensure(&p, BINSON_TYPE_STRING);\n");
                writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
                parse(array.getString(i));
            } else {
                writerCode.append("ERRORERRORERROR");
            }
        }
        writerCode.append("    binson_parser_leave_array(&p);\n");
        writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
    }

    private void parse(boolean value) {
        writerCode.append("    boolval = binson_parser_get_boolean(&p);\n");
        writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
        writerCode.append("    assert(boolval == " + value +");\n");
    }

    private void parse(String value) {
        /* TODO: Escape characted " with \" */
        writerCode.append("    rawval = binson_parser_get_string_bbuf(&p);\n");
        writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
        writerCode.append("    assert(memcmp(rawval->bptr, \"" + byteRep(value) + "\", rawval->bsize) == 0);\n");
    }

    private void parse(byte[] value) {
        writerCode.append("    rawval = binson_parser_get_bytes_bbuf(&p);\n");
        writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
        writerCode.append("    assert(memcmp(rawval->bptr, \"" + byteRep(value) + "\", rawval->bsize) == 0);\n");
    }

    private void parse(double value) {
        writerCode.append("    dval = binson_parser_get_double(&p);\n");
        writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
        writerCode.append("    assert(dval == " + value +");\n");

    }

    private void parse(long value) {
        writerCode.append("    intval = binson_parser_get_integer(&p);\n");
        writerCode.append("    assert(p.error_flags == BINSON_ID_OK);\n");
        writerCode.append("    assert(intval == " + value +");\n");    }

    public static void generateCCode(Binson b) {
        if (b == null) {
            return;
        }
        new JavaToCParser(b).go();
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            Binson b = new Binson()
                    .put("A", "B")
                    .put("B", new Binson().put("A", "B"))
                    .put("C", new BinsonArray()
                            .add("A")
                            .add("A")
                            .add(new Binson()
                                    .put("A", "B").put("B", new BinsonArray()
                                            .add("A")
                                            .add("A")
                                            .add(new Binson().put("A", "B"))
                                            .add(new BinsonArray()
                                                    .add(new BinsonArray()
                                                            .add(new BinsonArray()
                                                                    .add(new BinsonArray()
                                                                            .add(new Binson().put("A", "B"))))))))
                            .add("A"))
                    .put("D", Math.PI)
                    .put("E", false)
                    .put("F", 127)
                    .put("G", new byte[]{0x02, 0x02});
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
