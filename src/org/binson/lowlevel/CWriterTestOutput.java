package org.binson.lowlevel;

import org.binson.Binson;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by simonj on 2018-07-05.
 */
public class CWriterTestOutput implements Output {

    private Writer writer;
    private Binson object;

    public CWriterTestOutput(Binson object, Writer writer) throws IOException {

        if (writer == null) {
            throw new IllegalArgumentException("writer == null not allowed");
        }

        if (object == null) {
            throw new IllegalArgumentException("object == null not allowed");
        }

        this.writer = writer;
        this.object = object;

    }

    public void begin() throws IOException {
        byte[] b = object.toBytes();
        writer.write("#include <assert.h>\n");
        writer.write("#include <string.h>\n");
        writer.write("#include \"binson_writer.h\"\n\n");
        writer.write("/*\n");
        writer.write(object.toPrettyJson());
        writer.write("*/\n");
        writer.write("int main(void)\n");
        writer.write("{\n");
        writer.write("    uint8_t expected[" + b.length +"] = \"" + byteRep(b) + "\";\n");
        writer.write("    uint8_t created[" + b.length + "];\n");
        writer.write("    binson_writer w;\n");
        writer.write("    binson_writer_init(&w, created, sizeof(created));\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    public void end() throws IOException {
        writer.write("    assert(memcmp(expected, created, sizeof(expected)) == 0);\n");
        writer.write("    return 0;\n");
        writer.write("}\n");
    }

    private String byteRep(String s) {
        byte[] value = Bytes.stringToUtf8(s);
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

    @Override
    public void writeBegin() throws IOException {
        writer.write("    binson_write_object_begin(&w);\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    @Override
    public void writeEnd() throws IOException {
        writer.write("    binson_write_object_end(&w);\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    @Override
    public void writeBeginArray() throws IOException {
        writer.write("    binson_write_array_begin(&w);\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    @Override
    public void writeEndArray() throws IOException {
        writer.write("    binson_write_array_end(&w);\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        writer.write("    binson_write_boolean(&w, " + value +");\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    @Override
    public void writeInteger(long value) throws IOException {
        writer.write("    binson_write_integer(&w, " + value +");\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    @Override
    public void writeDouble(double value) throws IOException {
        writer.write("    binson_write_double(&w, " + value +");\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    @Override
    public void writeString(String string) throws IOException {
        writer.write("    binson_write_string_with_len(&w, \"" + byteRep(string) + "\", "+ byteRep(string).length()/4 +");\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    @Override
    public void writeBytes(byte[] value) throws IOException {
        writer.write("    binson_write_bytes(&w, (uint8_t *)\"" + byteRep(value) + "\", " + value.length +");\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    @Override
    public void writeName(String name) throws IOException {
        writer.write("    binson_write_name_with_len(&w, \"" + byteRep(name) + "\", "+ byteRep(name).length()/4 +");\n");
        writer.write("    assert(w.error_flags == BINSON_ID_OK);\n");
    }

    @Override
    public void writeArrayValueSeparator() throws IOException {

    }

    @Override
    public void writeNameValueSeparator() throws IOException {

    }

    @Override
    public void writePairSeparator() throws IOException {

    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }
}
