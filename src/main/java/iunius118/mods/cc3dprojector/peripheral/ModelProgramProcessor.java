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

	public static final byte COLOR = 0x20;

	public static final byte POINTS = 0x30;
	public static final byte LINES = 0x38;
	public static final byte LOOPS = 0x40;
	public static final byte FACE = 0x48;

	public static final byte TRANSLATE = 0x50;
	public static final byte ROTATE_X = 0x60;
	public static final byte ROTATE_Y = 0x61;
	public static final byte ROTATE_Z = 0x62;
	public static final byte SCALE = 0x70;

	public static final String NAME_COLOR = "color";

	public static final String NAME_POINTS = "point";
	public static final String NAME_LINES = "line";
	public static final String NAME_LOOPS = "loop";
	public static final String NAME_FACE = "face";

	public static final String NAME_TRANSLATE = "translate";
	public static final String NAME_ROTATE_X = "rotateX";
	public static final String NAME_ROTATE_Y = "rotateY";
	public static final String NAME_ROTATE_Z = "rotateZ";
	public static final String NAME_SCALE = "scale";

	public byte[] compile(Map<Object, Object> modelProgram) throws LuaException {
		cacheVec3s = new ArrayList<Vec3f>();
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

			Map<Object, Object> map = (Map<Object, Object>)value;
			List<Object> list = new ArrayList<Object>();

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

			if ((NAME_COLOR.equals(statemant[0]) || "colour".equals(statemant[0])) && size >= 2) {
				if (statemant[1] instanceof Double) {
					bufCommands.write(COLOR);
					bufCommands.write(((Double)statemant[1]).intValue());
				}

			} else if (writeCommandAndVertices(statemant, NAME_POINTS, POINTS, 1, 256, bufCommands)) {

			} else if (writeCommandAndVertices(statemant, NAME_LINES, LINES, 2, 256, bufCommands)) {

			} else if (writeCommandAndVertices(statemant, NAME_LOOPS, LOOPS, 3, 256, bufCommands)) {

			} else if (writeCommandAndVertices(statemant, NAME_FACE, FACE, 4, 4, bufCommands)) {

			} else if (writeCommandAndVertices(statemant, NAME_TRANSLATE, TRANSLATE, 1, 1, bufCommands)) {

			} else if (writeCommandAndVertices(statemant, NAME_SCALE, SCALE, 1, 1, bufCommands)) {

			} else if (NAME_ROTATE_X.equals(statemant[0]) && size >= 2) {
				if (statemant[1] instanceof Double) {
					byte[] bytes = toByteArray(((Double)statemant[1]).floatValue());
					bufCommands.write(ROTATE_X);
					bufCommands.write(bytes, 0, bytes.length);
				}

			} else if (NAME_ROTATE_Y.equals(statemant[0]) && size >= 2) {
				if (statemant[1] instanceof Double) {
					byte[] bytes = toByteArray(((Double)statemant[1]).floatValue());
					bufCommands.write(ROTATE_Y);
					bufCommands.write(bytes, 0, bytes.length);
				}

			} else if (NAME_ROTATE_Z.equals(statemant[0]) && size >= 2) {
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

	public Map decompile(byte[] compiledProgram) throws LuaException {
		cacheVec3s = new ArrayList<Vec3f>();
		ByteBuffer buf = ByteBuffer.wrap(compiledProgram);
		Map<Object, Object> modelProgram = new HashMap<Object, Object>();

		chaeckBufferRemaining(buf, 7);

		if (buf.get() != VERSION) {
			return modelProgram;
		}

		int indexCount = buf.getShort() & 0xFFFF;

		for (int i  = 0; i < indexCount; i++) {
			float x = buf.getFloat();
			float y = buf.getFloat();
			float z = buf.getFloat();
			cacheVec3s.add(new Vec3f(x, y, z));
		}

		int size = buf.getInt();
		int commandCount = 0;

		while (buf.remaining() > 0) {
			Map<Object, Object> statement;
			byte commandCode = buf.get();
			commandCount++;

			switch(commandCode) {
			case COLOR:
				chaeckBufferRemaining(buf, 1);
				statement = new HashMap<Object, Object>();
				statement.put(1, NAME_COLOR);
				statement.put(2, buf.get() & 0xF);
				modelProgram.put(commandCount, statement);
				break;
			case POINTS:
				modelProgram.put(commandCount, readCommandAndVertices(buf, NAME_POINTS));
				break;
			case LINES:
				modelProgram.put(commandCount, readCommandAndVertices(buf, NAME_LINES));
				break;
			case LOOPS:
				modelProgram.put(commandCount, readCommandAndVertices(buf, NAME_LOOPS));
				break;
			case FACE:
				modelProgram.put(commandCount, readCommandAndVertices(buf, NAME_FACE));
				break;
			case TRANSLATE:
				modelProgram.put(commandCount, readCommandAndVertices(buf, NAME_TRANSLATE));
				break;
			case SCALE:
				modelProgram.put(commandCount, readCommandAndVertices(buf, NAME_SCALE));
				break;
			case ROTATE_X:
				chaeckBufferRemaining(buf, 4);
				statement = new HashMap<Object, Object>();
				statement.put(1, NAME_ROTATE_X);
				statement.put(2, buf.getFloat());
				modelProgram.put(commandCount, statement);
				break;
			case ROTATE_Y:
				chaeckBufferRemaining(buf, 4);
				statement = new HashMap<Object, Object>();
				statement.put(1, NAME_ROTATE_Y);
				statement.put(2, buf.getFloat());
				modelProgram.put(commandCount, statement);
				break;
			case ROTATE_Z:
				chaeckBufferRemaining(buf, 4);
				statement = new HashMap<Object, Object>();
				statement.put(1, NAME_ROTATE_Z);
				statement.put(2, buf.getFloat());
				modelProgram.put(commandCount, statement);
				break;
			}
		}

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
			List<Integer> vertexIndices = new ArrayList<Integer>();

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

	private Map readCommandAndVertices(ByteBuffer inputBuf, String command) throws LuaException {
		chaeckBufferRemaining(inputBuf, 1);

		int vertexCount = inputBuf.get() & 0xFF;
		Map<Object, Object> statement = new HashMap<Object, Object>();
		statement.put(1, command);

		chaeckBufferRemaining(inputBuf, vertexCount * 2);

		for (int i = 0; i < vertexCount; i++) {
			Map<Object, Object> vertex = new HashMap<Object, Object>();
			int index = inputBuf.getShort() & 0xFFFF;
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
		} else if (cacheVec3s.size() < 0x10000) {
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
