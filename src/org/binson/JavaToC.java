package org.binson;

/**
 * Created by simonj on 2018-06-28.
 */
public class JavaToC {

    private static int i = 0;
    private final Binson object;

    private JavaToC(Binson b) {
        object = b;

    }

    private void go() {
        byte[] b = object.toBytes();

        System.out.println("static void test_"+i+++"(void)");
        System.out.println("{");
        System.out.println("    uint8_t expected[" + b.length +"] = \"" + byteRep(b) + "\";");
        System.out.println("    uint8_t expected[uint8_t created[" + b.length + "];");
        System.out.println("    uint8_t expected[binson_writer w;");
        System.out.println("    uint8_t expected[binson_writer_init(&w, created, sizeof(created));");
        System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
        write(object);
        System.out.println("    uint8_t expected[assert(memcmp(expected, created, sizeof(expected)) == 0);");
        System.out.println("}");

    }

    private void handleType(String field, Binson b) {

    }

    private String byteRep(byte[] value) {
        StringBuilder sb = new StringBuilder(value.length*4);
        for (byte b : value) {
            sb.append(String.format("\\x%02x", b));
        }
        return sb.toString();
    }

    private void write(Binson b) {
        System.out.println("    uint8_t expected[binson_write_object_begin(&w);");
        System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
        for (String field : b.keySet()) {
            System.out.println("    uint8_t expected[binson_write_name(&w, \"" + field + "\");");
            System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
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
                System.out.println("ERRORERRORERROR");
            }
        }

        System.out.println("    uint8_t expected[binson_write_object_end(&w);");
        System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
    }

    private void write(BinsonArray array) {
        System.out.println("    uint8_t expected[binson_write_array_begin(&w);");
        System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
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
                System.out.println("ERRORERRORERROR");
            }
        }
        System.out.println("    uint8_t expected[binson_write_array_end(&w);");
        System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
    }

    private void write(boolean value) {
        System.out.println("    uint8_t expected[binson_write_boolean(&w, " + value +");");
        System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
    }

    private void write(String value) {
        /* TODO: Escape characted " with \" */
        System.out.println("    uint8_t expected[binson_write_string(&w, \"" + value + "\");");
        System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
    }

    private void write(byte[] value) {
        System.out.println("    uint8_t expected[binson_write_bytes(&w, \"" + value + "\", " + value.length +");");
        System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
    }

    private void write(double value) {
        System.out.println("    uint8_t expected[binson_write_double(&w, " + value +");");
        System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
    }

    private void write(int value) {
        System.out.println("    uint8_t expected[binson_write_integer(&w, " + value +");");
        System.out.println("    uint8_t expected[assert(w.error_flags == BINSON_ID_OK);");
    }

    public static void generateCCode(Binson b) {
        if (b == null) {
            return;
        }
        new JavaToC(b).go();
    }

    public static void main(String[] args) {
        generateCCode(new Binson()
                .put("A", "B")
                .put("B", new BinsonArray()
                        .add("Hello world")
                        .add(new Binson().put("A", "B"))));
    }

}
