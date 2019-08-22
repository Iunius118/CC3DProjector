package net.github.iunius118.cc3dprojector.peripheral;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.util.math.Vec3i;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ModelProgramProcessor {

    private List<Vec3f> cacheVec3s;

    public static final byte VERSION = 0x00;
    public static final byte MIN_SIZE = 7;
    public static final int MAX_VERTEX_INDEX = 0xFFFF;

    // Command byte codes
    public static final byte COLOR = 0x20;
    public static final byte OPACITY = 0x21;
    public static final byte OSCILLATE = 0x28;

    public static final byte POINTS = 0x30;
    public static final byte LINE_STRIP = 0x38;
    public static final byte LINE_LOOP = 0x40;
    public static final byte FACE = 0x48;

    public static final byte TRANSLATE = 0x50;
    public static final byte ROTATE_X = 0x60;
    public static final byte ROTATE_Y = 0x61;
    public static final byte ROTATE_Z = 0x62;
    public static final byte SCALE = 0x70;

    // Minimum statement size
    public static final int SIZE_COLOR = 2;
    public static final int SIZE_OPACITY = 2;
    public static final int SIZE_OSCILLATE = 4;

    public static final int SIZE_POINTS = 2;
    public static final int SIZE_LINE_STRIP = 3;
    public static final int SIZE_LINE_LOOP = 4;
    public static final int SIZE_FACE = 5;

    public static final int SIZE_TRANSLATE = 2;
    public static final int SIZE_ROTATE_X = 2;
    public static final int SIZE_ROTATE_Y = 2;
    public static final int SIZE_ROTATE_Z = 2;
    public static final int SIZE_SCALE = 2;

    // Command names
    public static final String NAME_COLOR = "color";
    public static final String NAME_COLOUR = "colour";
    public static final String NAME_OPACITY = "alpha";
    public static final String NAME_OSCILLATE = "oscillate";

    public static final String NAME_POINTS = "point";
    public static final String NAME_LINE_STRIP = "line";
    public static final String NAME_LINE_LOOP = "loop";
    public static final String NAME_FACE = "face";

    public static final String NAME_TRANSLATE = "translate";
    public static final String NAME_ROTATE_X = "rotateX";
    public static final String NAME_ROTATE_Y = "rotateY";
    public static final String NAME_ROTATE_Z = "rotateZ";
    public static final String NAME_SCALE = "scale";

    /**
     * Compile HashMap (from LuaTable) to byte array
     *  */
    public byte[] compile(Map modelProgram) throws LuaException {
        cacheVec3s = new ArrayList<>();
        ByteArrayOutputStream bufVertexIndex = new ByteArrayOutputStream();
        ByteArrayOutputStream bufCommands = new ByteArrayOutputStream();

        for (int i = 0; i < modelProgram.size(); i++ ) {
            if (!modelProgram.containsKey((double) (i + 1))) {
                break;
            }

            Object value = modelProgram.get((double) (i + 1));	// get Map of statement

            if (!(value instanceof Map)) {
                break;
            }

            Map map = (Map)value;
            List<Object> list = new ArrayList<>();

            for (int j = 0; j < map.size(); j++ ) {	// Convert statement map to List
                if (map.containsKey((double) (j + 1))) {
                    list.add(map.get((double) (j + 1)));
                } else {
                    break;
                }
            }

            Object[] statement = list.toArray();	// Convert statement list to array
            int size = statement.length;

            if (size < 1) {
                continue;
            }

            // Convert statement to binary code
            if ((NAME_COLOR.equals(statement[0]) || NAME_COLOUR.equals(statement[0])) && size >= SIZE_COLOR) {
                if (statement[1] instanceof Double) {
                    bufCommands.write(COLOR);
                    bufCommands.write(((Double)statement[1]).intValue());
                }

            } else if (NAME_OPACITY.equals(statement[0]) && size >= SIZE_OPACITY) {
                if (statement[1] instanceof Double) {
                    byte[] bytes = toByteArray(((Double)statement[1]).floatValue());
                    bufCommands.write(OPACITY);
                    bufCommands.write(bytes, 0, bytes.length);
                }

            } else if (NAME_OSCILLATE.equals(statement[0])) {
                if (size >= SIZE_OSCILLATE) {
                    if (statement[1] instanceof Double
                            && statement[2] instanceof Double
                            && statement[3] instanceof Double) {
                        bufCommands.write(OSCILLATE);
                        bufCommands.write(((Double)statement[1]).intValue());
                        byte[] bytes = toByteArray(((Double)statement[2]).floatValue());
                        bufCommands.write(bytes, 0, bytes.length);
                        bytes = toByteArray(((Double)statement[3]).floatValue());
                        bufCommands.write(bytes, 0, bytes.length);
                    }
                } else if (size == 1) {
                    bufCommands.write(OSCILLATE);
                    bufCommands.write(-1);
                }

            } else if (writeCommandAndVertices(statement, NAME_POINTS, POINTS, SIZE_POINTS - 1, 256, bufCommands)) {

            } else if (writeCommandAndVertices(statement, NAME_LINE_STRIP, LINE_STRIP, SIZE_LINE_STRIP - 1, 256, bufCommands)) {

            } else if (writeCommandAndVertices(statement, NAME_LINE_LOOP, LINE_LOOP, SIZE_LINE_LOOP - 1, 256, bufCommands)) {

            } else if (writeCommandAndVertices(statement, NAME_FACE, FACE, SIZE_FACE - 1, SIZE_FACE - 1, bufCommands)) {

            } else if (writeCommandAndVertices(statement, NAME_TRANSLATE, TRANSLATE, SIZE_TRANSLATE - 1, SIZE_TRANSLATE - 1, bufCommands)) {

            } else if (writeCommandAndVertices(statement, NAME_SCALE, SCALE, SIZE_SCALE - 1, SIZE_SCALE - 1, bufCommands)) {

            } else if (NAME_ROTATE_X.equals(statement[0]) && size >= SIZE_ROTATE_X) {
                if (statement[1] instanceof Double) {
                    byte[] bytes = toByteArray(((Double)statement[1]).floatValue());
                    bufCommands.write(ROTATE_X);
                    bufCommands.write(bytes, 0, bytes.length);
                }

            } else if (NAME_ROTATE_Y.equals(statement[0]) && size >= SIZE_ROTATE_Y) {
                if (statement[1] instanceof Double) {
                    byte[] bytes = toByteArray(((Double)statement[1]).floatValue());
                    bufCommands.write(ROTATE_Y);
                    bufCommands.write(bytes, 0, bytes.length);
                }

            } else if (NAME_ROTATE_Z.equals(statement[0]) && size >= SIZE_ROTATE_Z) {
                if (statement[1] instanceof Double) {
                    byte[] bytes = toByteArray(((Double)statement[1]).floatValue());
                    bufCommands.write(ROTATE_Z);
                    bufCommands.write(bytes, 0, bytes.length);
                }

            }


        }

        // Begin to write binary codes to byte buffer
        bufVertexIndex.write(VERSION);

        // Write vertex list
        byte[] bytes = toByteArray((short)cacheVec3s.size());
        bufVertexIndex.write(bytes, 0, bytes.length);

        for (Vec3f v : cacheVec3s) {
            byte[] bf;
            bf = toByteArray(v.x);
            bufVertexIndex.write(bf, 0, bf.length);

            bf = toByteArray(v.y);
            bufVertexIndex.write(bf, 0, bf.length);

            bf = toByteArray(v.z);
            bufVertexIndex.write(bf, 0, bf.length);
        }

        // Write command codes
        byte[] bSize = toByteArray(bufCommands.size());
        bufVertexIndex.write(bSize, 0, bSize.length);

        byte[] byteCommands = bufCommands.toByteArray();
        bufVertexIndex.write(byteCommands, 0, byteCommands.length);

        // CC3DProjector.logger.info("buf-size: " + bufVertexIndex.size());

        return bufVertexIndex.toByteArray();
    }

    /**
     * Decompile byte array to HashMap (for LuaTable)
     *  */
    public List<Map<Integer, Object>> decompile(byte[] compiledProgram) throws LuaException {
        cacheVec3s = new ArrayList<>();
        ByteBuffer buf = ByteBuffer.wrap(compiledProgram);
        List<Map<Integer, Object>> modelProgram = new ArrayList<>();

        checkBufferRemaining(buf, MIN_SIZE);

        if (buf.get() != VERSION) {
            return modelProgram;
        }

        // Read vertex list
        int indexCount = buf.getShort() & MAX_VERTEX_INDEX;

        for (int i  = 0; i < indexCount; i++) {
            float x = buf.getFloat();
            float y = buf.getFloat();
            float z = buf.getFloat();
            cacheVec3s.add(new Vec3f(x, y, z));
        }

        // Convert binary code to statement
        while (buf.remaining() > 0) {
            Map<Integer, Object> statement;
            byte commandCode = buf.get();

            switch(commandCode) {
                case COLOR:
                    checkBufferRemaining(buf, 1);
                    statement = new HashMap<>();
                    statement.put(1, NAME_COLOR);
                    statement.put(2, buf.get() & 0xF);
                    modelProgram.add(statement);
                    break;
                case OPACITY:
                    checkBufferRemaining(buf, 4);
                    statement = new HashMap<>();
                    statement.put(1, NAME_OPACITY);
                    statement.put(2, buf.getFloat());
                    modelProgram.add(statement);
                    break;
                case OSCILLATE:
                    checkBufferRemaining(buf, 1);
                    statement = new HashMap<>();
                    statement.put(1, NAME_OSCILLATE);
                    byte type = buf.get();

                    if (type >= 0) {
                        statement.put(2, (int)type);
                        statement.put(3, buf.getFloat());
                        statement.put(4, buf.getFloat());
                    }

                    modelProgram.add(statement);
                    break;
                case POINTS:
                    modelProgram.add(readCommandAndVertices(buf, NAME_POINTS));
                    break;
                case LINE_STRIP:
                    modelProgram.add(readCommandAndVertices(buf, NAME_LINE_STRIP));
                    break;
                case LINE_LOOP:
                    modelProgram.add(readCommandAndVertices(buf, NAME_LINE_LOOP));
                    break;
                case FACE:
                    modelProgram.add(readCommandAndVertices(buf, NAME_FACE));
                    break;
                case TRANSLATE:
                    modelProgram.add(readCommandAndVertices(buf, NAME_TRANSLATE));
                    break;
                case SCALE:
                    modelProgram.add(readCommandAndVertices(buf, NAME_SCALE));
                    break;
                case ROTATE_X:
                    checkBufferRemaining(buf, 4);
                    statement = new HashMap<>();
                    statement.put(1, NAME_ROTATE_X);
                    statement.put(2, buf.getFloat());
                    modelProgram.add(statement);
                    break;
                case ROTATE_Y:
                    checkBufferRemaining(buf, 4);
                    statement = new HashMap<>();
                    statement.put(1, NAME_ROTATE_Y);
                    statement.put(2, buf.getFloat());
                    modelProgram.add(statement);
                    break;
                case ROTATE_Z:
                    checkBufferRemaining(buf, 4);
                    statement = new HashMap<>();
                    statement.put(1, NAME_ROTATE_Z);
                    statement.put(2, buf.getFloat());
                    modelProgram.add(statement);
                    break;
            }
        }

        // CC3DProjector.logger.info("Model decompiled");

        return modelProgram;
    }

    public static byte[] deflate(byte[] buffer){
        ByteArrayOutputStream ret = new ByteArrayOutputStream();
        Deflater compressor = new Deflater();
        byte[] bufTmp = new byte[1024];

        compressor.setInput(buffer);
        compressor.finish();

        while (!compressor.finished()) {
            int size = compressor.deflate(bufTmp);
            ret.write(bufTmp, 0, size);
        }

        compressor.end();
        // CC3DProjector.logger.info("deflated-size: " + ret.size());
        return ret.toByteArray();
    }

    public static byte[] inflate(byte[] buffer){
        ByteArrayOutputStream ret = new ByteArrayOutputStream();
        Inflater decompressor = new Inflater();
        byte[] bufTmp = new byte[1024];

        decompressor.setInput(buffer);

        try {
            while (!decompressor.finished()) {
                int size = decompressor.inflate(bufTmp);
                ret.write(bufTmp, 0, size);
            }

            decompressor.end();
        } catch (DataFormatException e) {
            e.printStackTrace();
            return new byte[0];
        }

        return ret.toByteArray();
    }

    /**
     * Convert statement to binary code and Index vertices to list and Write byte buffer
     *  */
    private boolean writeCommandAndVertices(Object[] statement, String commandName, byte commandCode, int minVertexCount, int maxVertexCount, ByteArrayOutputStream bufCommands) throws LuaException {
        int size = statement.length;

        if (commandName.equals(statement[0]) && size >= 2) {
            List<Integer> vertexIndices = new ArrayList<>();

            for (int j = 1; j < size && (j - 1) < maxVertexCount; j++) {	// Get vertices up to maxVertexCount from statement
                if (statement[j] instanceof Map) {
                    Map m = (Map)statement[j];

                    // Index vertices to list
                    if (m.size() >= 3
                            && m.get(1d) instanceof Double
                            && m.get(2d) instanceof Double
                            && m.get(3d) instanceof Double) {
                        float x = ((Double)m.get(1d)).floatValue();
                        float y = ((Double)m.get(2d)).floatValue();
                        float z = ((Double)m.get(3d)).floatValue();
                        vertexIndices.add(setVec3ToCache(new Vec3f(x, y, z)));
                    }
                }
            }

            // Convert statement to binary code
            if (vertexIndices.size() >= minVertexCount) {
                bufCommands.write(commandCode);
                bufCommands.write(vertexIndices.size());

                for (int index : vertexIndices) {
                    if (index < 0) {
                        throw new LuaException("CC3DProjector: Vertex buffer overflow");
                    }

                    // Write byte buffer
                    byte[] bytes = toByteArray((short)index);
                    bufCommands.write(bytes, 0, bytes.length);
                }

            }

            return true;
        }

        return false;
    }

    /**
     * Convert binary code from byte buffer to Map of statement
     *  */
    private Map<Integer, Object> readCommandAndVertices(ByteBuffer inputBuf, String command) throws LuaException {
        checkBufferRemaining(inputBuf, 1);

        int vertexCount = inputBuf.get() & 0xFF;
        Map<Integer, Object> statement = new HashMap<>();
        statement.put(1, command);

        checkBufferRemaining(inputBuf, vertexCount * 2);

        // Get vertex elements from vertex list and Add vertices to statement
        for (int i = 0; i < vertexCount; i++) {
            Map<Integer, Object> vertex = new HashMap<>();
            int index = inputBuf.getShort() & MAX_VERTEX_INDEX;
            Vec3f v = getVec3FromCache(index);

            if (v == null) {
                throw new LuaException("CC3DProjector: Vertex buffer index out of bounds : " + inputBuf.position());
            }

            vertex.put(1, v.x);
            vertex.put(2, v.y);
            vertex.put(3, v.z);

            statement.put(i + 2, vertex);
        }

        return statement;
    }

    private void checkBufferRemaining(ByteBuffer buf, int size) throws LuaException {
        if (buf.remaining() < size) {
            throw new LuaException("CC3DProjector: Compiled program buffer index out of bounds");
        }
    }

    private int setVec3ToCache(Vec3f v) {
        if (cacheVec3s.contains(v)) {
            return cacheVec3s.indexOf(v);
        } else if (cacheVec3s.size() < MAX_VERTEX_INDEX + 1) {
            cacheVec3s.add(v);
            return cacheVec3s.size() - 1;
        }

        return -1;
    }

    private Vec3f getVec3FromCache(int index) {
        if (index >= 0 && index < cacheVec3s.size()) {
            return cacheVec3s.get(index);
        }

        return null;
    }

    private byte[] toByteArray(int value) {
        int arraySize = Integer.SIZE / Byte.SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(arraySize);
        return buffer.putInt(value).array();
    }

    private byte[] toByteArray(short value) {
        int arraySize = Short.SIZE / Byte.SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(arraySize);
        return buffer.putShort(value).array();
    }

    private byte[] toByteArray(float value) {
        int arraySize = Float.SIZE / Byte.SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(arraySize);
        return buffer.putFloat(value).array();
    }

    public static class Vec3f {

        public static final Vec3f ZERO = new Vec3f(0.0F, 0.0F, 0.0F);
        public final float x;
        public final float y;
        public final float z;

        public Vec3f(float x, float y, float z) {
            if (x == -0.0F) {
                x = 0.0F;
            }

            if (y == -0.0F) {
                y = 0.0F;
            }

            if (z == -0.0F) {
                z = 0.0F;
            }

            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3f(Vec3i vector) {
            this((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
        }

        public boolean equals(Object p_equals_1_) {
            if (this == p_equals_1_) {
                return true;
            } else if (!(p_equals_1_ instanceof Vec3f)) {
                return false;
            } else {
                Vec3f vec3f = (Vec3f) p_equals_1_;
                return Double.compare(vec3f.x, this.x) == 0 && (Double.compare(vec3f.y, this.y) == 0 && Double.compare(vec3f.z, this.z) == 0);
            }
        }

        public int hashCode() {
            long j = Double.doubleToLongBits(this.x);
            int i = (int) (j ^ j >>> 32);
            j = Double.doubleToLongBits(this.y);
            i = 31 * i + (int) (j ^ j >>> 32);
            j = Double.doubleToLongBits(this.z);
            i = 31 * i + (int) (j ^ j >>> 32);
            return i;
        }

    }
}
