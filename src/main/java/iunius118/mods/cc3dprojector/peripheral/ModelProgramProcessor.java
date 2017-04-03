package iunius118.mods.cc3dprojector.peripheral;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.util.Vec3i;


public class ModelProgramProcessor {

	private List<Vec3f> cacheVec3s;

	public static final byte VERSION = 0x00;
	public static final byte MIN_SIZE = 7;
	public static final int MAX_VERTEX_INDEX = 0xFFFF;

	// Command byte codes
	public static final byte COLOR = 0x20;
	public static final byte TRANSPARENCY = 0x21;
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
	public static final int SIZE_TRANSPARENCY = 2;
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
	public static final String NAME_TRANSPARENCY = "alpha";
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

	public byte[] compile(Map modelProgram) throws LuaException {
		cacheVec3s = new ArrayList();
		ByteArrayOutputStream bufVertexIndex = new ByteArrayOutputStream();
		ByteArrayOutputStream bufCommands = new ByteArrayOutputStream();

		for (int i = 0; i < modelProgram.size(); i++ ) {
			if (!modelProgram.containsKey(Double.valueOf(i + 1))) {
				break;
			}

			Object value = modelProgram.get(Double.valueOf(i + 1));

			if (!(value instanceof Map)) {
				break;
			}

			Map map = (Map)value;
			List<Object> list = new ArrayList();

			for (int j = 0; j < map.size(); j++ ) {
				if (map.containsKey(Double.valueOf(j + 1))) {
					list.add(map.get(Double.valueOf(j + 1)));
				} else {
					break;
				}
			}

			Object[] statemant = list.toArray();
			int size = statemant.length;

			if (size < 1) {
				continue;
			}

			if ((NAME_COLOR.equals(statemant[0]) || NAME_COLOUR.equals(statemant[0])) && size >= SIZE_COLOR) {
				if (statemant[1] instanceof Double) {
					bufCommands.write(COLOR);
					bufCommands.write(((Double)statemant[1]).intValue());
				}

			} else if (NAME_TRANSPARENCY.equals(statemant[0]) && size >= SIZE_TRANSPARENCY) {
				if (statemant[1] instanceof Double) {
					byte[] bytes = toByteArray(((Double)statemant[1]).floatValue());
					bufCommands.write(TRANSPARENCY);
					bufCommands.write(bytes, 0, bytes.length);
				}

			} else if (NAME_OSCILLATE.equals(statemant[0])) {
				if (size >= SIZE_OSCILLATE) {
					if (statemant[1] instanceof Double
							&& statemant[2] instanceof Double
							&& statemant[3] instanceof Double) {
						bufCommands.write(OSCILLATE);
						bufCommands.write(((Double)statemant[1]).intValue());
						byte[] bytes = toByteArray(((Double)statemant[2]).floatValue());
						bufCommands.write(bytes, 0, bytes.length);
						bytes = toByteArray(((Double)statemant[3]).floatValue());
						bufCommands.write(bytes, 0, bytes.length);
					}
				} else if (size == 1) {
					bufCommands.write(OSCILLATE);
					bufCommands.write(-1);
				}

			} else if (writeCommandAndVertices(statemant, NAME_POINTS, POINTS, SIZE_POINTS - 1, 256, bufCommands)) {

			} else if (writeCommandAndVertices(statemant, NAME_LINE_STRIP, LINE_STRIP, SIZE_LINE_STRIP - 1, 256, bufCommands)) {

			} else if (writeCommandAndVertices(statemant, NAME_LINE_LOOP, LINE_LOOP, SIZE_LINE_LOOP - 1, 256, bufCommands)) {

			} else if (writeCommandAndVertices(statemant, NAME_FACE, FACE, SIZE_FACE - 1, SIZE_FACE - 1, bufCommands)) {

			} else if (writeCommandAndVertices(statemant, NAME_TRANSLATE, TRANSLATE, SIZE_TRANSLATE - 1, SIZE_TRANSLATE - 1, bufCommands)) {

			} else if (writeCommandAndVertices(statemant, NAME_SCALE, SCALE, SIZE_SCALE - 1, SIZE_SCALE - 1, bufCommands)) {

			} else if (NAME_ROTATE_X.equals(statemant[0]) && size >= SIZE_ROTATE_X) {
				if (statemant[1] instanceof Double) {
					byte[] bytes = toByteArray(((Double)statemant[1]).floatValue());
					bufCommands.write(ROTATE_X);
					bufCommands.write(bytes, 0, bytes.length);
				}

			} else if (NAME_ROTATE_Y.equals(statemant[0]) && size >= SIZE_ROTATE_Y) {
				if (statemant[1] instanceof Double) {
					byte[] bytes = toByteArray(((Double)statemant[1]).floatValue());
					bufCommands.write(ROTATE_Y);
					bufCommands.write(bytes, 0, bytes.length);
				}

			} else if (NAME_ROTATE_Z.equals(statemant[0]) && size >= SIZE_ROTATE_Z) {
				if (statemant[1] instanceof Double) {
					byte[] bytes = toByteArray(((Double)statemant[1]).floatValue());
					bufCommands.write(ROTATE_Z);
					bufCommands.write(bytes, 0, bytes.length);
				}

			}


		}

		bufVertexIndex.write(VERSION);

		byte[] bytes = toByteArray((short)cacheVec3s.size());
		bufVertexIndex.write(bytes, 0, bytes.length);

		for (Vec3f v : cacheVec3s) {
			byte[] bf;
			bf = toByteArray(v.xCoord);
			bufVertexIndex.write(bf, 0, bf.length);

			bf = toByteArray(v.yCoord);
			bufVertexIndex.write(bf, 0, bf.length);

			bf = toByteArray(v.zCoord);
			bufVertexIndex.write(bf, 0, bf.length);
		}

		byte[] bsize = toByteArray(bufCommands.size());
		bufVertexIndex.write(bsize, 0, bsize.length);

		byte[] byteCommands = bufCommands.toByteArray();
		bufVertexIndex.write(byteCommands, 0, byteCommands.length);

		/*
		System.out.println("buf-size: " + bufVertexIndex.size());

		System.out.print("{");
		for (byte b : bufVertexIndex.toByteArray()) {
			System.out.printf("%02X,", b);
		}
		System.out.print("}\n");
		// */

		return bufVertexIndex.toByteArray();
	}

	public List<Map<Integer, Object>> decompile(byte[] compiledProgram) throws LuaException {
		cacheVec3s = new ArrayList();
		ByteBuffer buf = ByteBuffer.wrap(compiledProgram);
		List<Map<Integer, Object>> modelProgram = new ArrayList();

		chaeckBufferRemaining(buf, MIN_SIZE);

		if (buf.get() != VERSION) {
			return modelProgram;
		}

		int indexCount = buf.getShort() & MAX_VERTEX_INDEX;

		for (int i  = 0; i < indexCount; i++) {
			float x = buf.getFloat();
			float y = buf.getFloat();
			float z = buf.getFloat();
			cacheVec3s.add(new Vec3f(x, y, z));
		}

		int size = buf.getInt();
		int commandCount = 0;

		while (buf.remaining() > 0) {
			Map<Integer, Object> statement;
			byte commandCode = buf.get();

			switch(commandCode) {
			case COLOR:
				chaeckBufferRemaining(buf, 1);
				statement = new HashMap();
				statement.put(1, NAME_COLOR);
				statement.put(2, buf.get() & 0xF);
				modelProgram.add(statement);
				break;
			case TRANSPARENCY:
				chaeckBufferRemaining(buf, 4);
				statement = new HashMap();
				statement.put(1, NAME_TRANSPARENCY);
				statement.put(2, buf.getFloat());
				modelProgram.add(statement);
				break;
			case OSCILLATE:
				chaeckBufferRemaining(buf, 1);
				statement = new HashMap();
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
				chaeckBufferRemaining(buf, 4);
				statement = new HashMap();
				statement.put(1, NAME_ROTATE_X);
				statement.put(2, buf.getFloat());
				modelProgram.add(statement);
				break;
			case ROTATE_Y:
				chaeckBufferRemaining(buf, 4);
				statement = new HashMap();
				statement.put(1, NAME_ROTATE_Y);
				statement.put(2, buf.getFloat());
				modelProgram.add(statement);
				break;
			case ROTATE_Z:
				chaeckBufferRemaining(buf, 4);
				statement = new HashMap();
				statement.put(1, NAME_ROTATE_Z);
				statement.put(2, buf.getFloat());
				modelProgram.add(statement);
				break;
			}
		}

		// System.out.println("Model decompiled");

		return modelProgram;
	}

	public byte[] deflate(byte[] buffer){
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
		return ret.toByteArray();
	}

	public byte[] inflate(byte[] buffer){
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

	private boolean writeCommandAndVertices(Object[] statemant, String command, byte commandCode, int minVertexCount, int maxVertexCount, ByteArrayOutputStream bufCommands) throws LuaException {
		int size = statemant.length;

		if (command.equals(statemant[0]) && size >= 2) {
			List<Integer> vertexIndices = new ArrayList();

			for (int j = 1; j < size && (j - 1) < maxVertexCount; j++) {
				if (statemant[j] instanceof Map) {
					Map m = (Map)statemant[j];

					if (m.size() >= 3
							&& m.get(Double.valueOf(1)) instanceof Double
							&& m.get(Double.valueOf(2)) instanceof Double
							&& m.get(Double.valueOf(3)) instanceof Double) {
						float x = ((Double)m.get(Double.valueOf(1))).floatValue();
						float y = ((Double)m.get(Double.valueOf(2))).floatValue();
						float z = ((Double)m.get(Double.valueOf(3))).floatValue();
						vertexIndices.add(setVec3ToCache(new Vec3f(x, y, z)));
					}
				}
			}

			if (vertexIndices.size() >= minVertexCount) {
				bufCommands.write(commandCode);
				bufCommands.write(vertexIndices.size());

				for (int index : vertexIndices) {
					if (index < 0) {
						throw new LuaException("CC3DProjector: Vertex buffer overflow");
					}

					byte[] bytes = toByteArray((short)index);
					bufCommands.write(bytes, 0, bytes.length);
				}

			}

			return true;
		}

		return false;
	}

	private Map<Integer, Object> readCommandAndVertices(ByteBuffer inputBuf, String command) throws LuaException {
		chaeckBufferRemaining(inputBuf, 1);

		int vertexCount = inputBuf.get() & 0xFF;
		Map<Integer, Object> statement = new HashMap();
		statement.put(1, command);

		chaeckBufferRemaining(inputBuf, vertexCount * 2);

		for (int i = 0; i < vertexCount; i++) {
			Map<Integer, Object> vertex = new HashMap();
			int index = inputBuf.getShort() & MAX_VERTEX_INDEX;
			Vec3f v = getVec3FromCache(index);

			if (v == null) {
				throw new LuaException("CC3DProjector: Vertex buffer index out of bounds : " + inputBuf.position());
			}

			vertex.put(1, v.xCoord);
			vertex.put(2, v.yCoord);
			vertex.put(3, v.zCoord);

			statement.put(i + 2, vertex);
		}

		return statement;
	}

	private void chaeckBufferRemaining(ByteBuffer buf, int size) throws LuaException {
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
		public final float xCoord;
		public final float yCoord;
		public final float zCoord;

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

			this.xCoord = x;
			this.yCoord = y;
			this.zCoord = z;
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
				return Double.compare(vec3f.xCoord, this.xCoord) != 0 ? false
						: (Double.compare(vec3f.yCoord, this.yCoord) != 0 ? false
								: Double.compare(vec3f.zCoord, this.zCoord) == 0);
			}
		}

		public int hashCode() {
			long j = Double.doubleToLongBits(this.xCoord);
			int i = (int) (j ^ j >>> 32);
			j = Double.doubleToLongBits(this.yCoord);
			i = 31 * i + (int) (j ^ j >>> 32);
			j = Double.doubleToLongBits(this.zCoord);
			i = 31 * i + (int) (j ^ j >>> 32);
			return i;
		}

	}

}
