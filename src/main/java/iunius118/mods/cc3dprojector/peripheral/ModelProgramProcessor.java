package iunius118.mods.cc3dprojector.peripheral;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dan200.computercraft.api.lua.LuaException;

public class ModelProgramProcessor {

	private List<Float> cacheFloats;

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
		cacheFloats = new ArrayList<Float>();
		ByteArrayOutputStream bufFloatIndex = new ByteArrayOutputStream();
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
					short index = (short)cacheNumber(((Double)statemant[1]).floatValue());

					if (index < 0) {
						throw new LuaException("CC3DProjector: Float index buffer overflow");
					}

					byte[] bytes = toByteArray(index);
					bufCommands.write(ROTATE_X);
					bufCommands.write(bytes, 0, bytes.length);
				}

			} else if (NAME_ROTATE_Y.equals(statemant[0]) && size >= 2) {
				if (statemant[1] instanceof Double) {
					short index = (short)cacheNumber(((Double)statemant[1]).floatValue());

					if (index < 0) {
						throw new LuaException("CC3DProjector: Float index buffer overflow");
					}

					byte[] bytes = toByteArray(index);
					bufCommands.write(ROTATE_Y);
					bufCommands.write(bytes, 0, bytes.length);
				}

			} else if (NAME_ROTATE_Z.equals(statemant[0]) && size >= 2) {
				if (statemant[1] instanceof Double) {
					short index = (short)cacheNumber(((Double)statemant[1]).floatValue());

					if (index < 0) {
						throw new LuaException("CC3DProjector: Float index buffer overflow");
					}

					byte[] bytes = toByteArray(index);
					bufCommands.write(ROTATE_Z);
					bufCommands.write(bytes, 0, bytes.length);
				}

			}


		}

		bufFloatIndex.write(VERSION);

		byte[] bytes = toByteArray((short)cacheFloats.size());
		bufFloatIndex.write(bytes, 0, bytes.length);

		for (Float f : cacheFloats) {
			byte[] bf = toByteArray(f);
			bufFloatIndex.write(bf, 0, bf.length);
		}

		byte[] bsize = toByteArray(bufCommands.size());
		bufFloatIndex.write(bsize, 0, bsize.length);

		byte[] byteCommands = bufCommands.toByteArray();
		bufFloatIndex.write(byteCommands, 0, byteCommands.length);

		System.out.println("buf-size: " + bufFloatIndex.size());

		return bufFloatIndex.toByteArray();
	}

	public Map decompile(byte[] compiledProgram) throws LuaException {
		cacheFloats = new ArrayList<Float>();
		ByteBuffer buf = ByteBuffer.wrap(compiledProgram);
		Map<Object, Object> modelProgram = new HashMap<Object, Object>();

		if (buf.get() != VERSION) {
			return modelProgram;
		}

		int indexCount = buf.getShort() & 0xFFFF;

		for (int i  = 0; i < indexCount; i++) {
			cacheFloats.add(buf.getFloat());
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
			case ROTATE_Y:
				chaeckBufferRemaining(buf, 4);
				statement = new HashMap<Object, Object>();
				statement.put(1, NAME_ROTATE_Y);
				statement.put(2, buf.getFloat());
				modelProgram.put(commandCount, statement);
			case ROTATE_Z:
				chaeckBufferRemaining(buf, 4);
				statement = new HashMap<Object, Object>();
				statement.put(1, NAME_ROTATE_Z);
				statement.put(2, buf.getFloat());
				modelProgram.put(commandCount, statement);
			}
		}

		return modelProgram;
	}

	public byte[] deflate(byte[] buffer){
		return null;
	}

	public byte[] inflate(byte[] buffer){
		return null;
	}

	private boolean writeCommandAndVertices(Object[] statemant, String command, byte commandCode, int minVertexCount, int maxVertexCount, ByteArrayOutputStream bufCommands) throws LuaException {
		int size = statemant.length;

		if (command.equals(statemant[0]) && size >= 2) {
			List<Float> vertices = new ArrayList<Float>();

			for (int j = 1; j < size && (j - 1) < maxVertexCount; j++) {
				if (statemant[j] instanceof Map) {
					Map m = (Map)statemant[j];

					if (m.size() >= 3
							&& m.get(Double.valueOf(1)) instanceof Double
							&& m.get(Double.valueOf(2)) instanceof Double
							&& m.get(Double.valueOf(3)) instanceof Double) {
						vertices.add(((Double)m.get(Double.valueOf(1))).floatValue());
						vertices.add(((Double)m.get(Double.valueOf(2))).floatValue());
						vertices.add(((Double)m.get(Double.valueOf(3))).floatValue());
					}
				}
			}

			if (vertices.size() >= minVertexCount * 3) {
				bufCommands.write(commandCode);
				bufCommands.write(vertices.size() / 3);

				for (Float vertex : vertices) {
					short index = (short)cacheNumber(vertex);

					if (index < 0) {
						throw new LuaException("CC3DProjector: Float index buffer overflow");
					}

					byte[] bytes = toByteArray(index);
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

		chaeckBufferRemaining(inputBuf, vertexCount * 6);

		for (int i = 0; i < vertexCount; i++) {
			Map<Object, Object> vertex = new HashMap<Object, Object>();

			for (int j = 0; j < 3; j++) {
				int index = inputBuf.getShort() & 0xFFFF;
				float f = 0;

				if (cacheFloats.size() > index) {
					f = cacheFloats.get(index);
				} else {
					throw new LuaException("CC3DProjector: Float index buffer index out of bounds");
				}

				vertex.put(j + 1, f);
			}

			statement.put(i + 2, vertex);
		}

		return statement;
	}

	private void chaeckBufferRemaining(ByteBuffer buf, int size) throws LuaException {
		if (buf.remaining() < size) {
			throw new LuaException("CC3DProjector: Compiled program buffer index out of bounds");
		}
	}

	private int cacheNumber(float f) {
		if (cacheFloats.contains(f)) {
			return cacheFloats.indexOf(f);
		} else if (cacheFloats.size() < 0x10000) {
			cacheFloats.add(f);
			return cacheFloats.size() - 1;
		}

		return -1;
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

}
