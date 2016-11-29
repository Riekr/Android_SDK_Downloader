package com.riekr.android.sdk.downloader.serve;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

import io.netty.buffer.*;

@SuppressWarnings("FieldNotUsedInToString")
public class LocalFileByteBuf extends ByteBuf {

	private final File							_file;
	private final int								_size;
	private final RandomAccessFile	_raf;

	private int											_mark	= 0;

	public LocalFileByteBuf(File file) throws FileNotFoundException {
		_file = file;
		_size = (int)file.length();
		_raf = new RandomAccessFile(file, "r");
	}

	@Override
	public int capacity() {
		return _size;
	}

	@Override
	public ByteBuf capacity(int newCapacity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int maxCapacity() {
		return _size;
	}

	@Override
	public ByteBufAllocator alloc() {
		return UnpooledByteBufAllocator.DEFAULT;
	}

	@Override
	public ByteOrder order() {
		return ByteOrder.nativeOrder();
	}

	@Override
	public ByteBuf order(ByteOrder endianness) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf unwrap() {
		return null;
	}

	@Override
	public boolean isDirect() {
		return true;
	}

	@Override
	public int readerIndex() {
		try {
			return (int)_raf.getFilePointer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf readerIndex(int readerIndex) {
		try {
			_raf.seek(readerIndex);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int writerIndex() {
		return _size;
	}

	@Override
	public ByteBuf writerIndex(int writerIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setIndex(int readerIndex, int writerIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int readableBytes() {
		return _size - readerIndex();
	}

	@Override
	public int writableBytes() {
		return 0;
	}

	@Override
	public int maxWritableBytes() {
		return 0;
	}

	@Override
	public boolean isReadable() {
		return isReadable(1);
	}

	@Override
	public boolean isReadable(int size) {
		return readableBytes() >= size;
	}

	@Override
	public boolean isWritable() {
		return false;
	}

	@Override
	public boolean isWritable(int size) {
		return false;
	}

	@Override
	public ByteBuf clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf markReaderIndex() {
		_mark = readerIndex();
		return this;
	}

	@Override
	public ByteBuf resetReaderIndex() {
		return readerIndex(_mark);
	}

	@Override
	public ByteBuf markWriterIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf resetWriterIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf discardReadBytes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf discardSomeReadBytes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf ensureWritable(int minWritableBytes) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int ensureWritable(int minWritableBytes, boolean force) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getBoolean(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				return _raf.readBoolean();
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte getByte(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				return _raf.readByte();
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public short getUnsignedByte(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				return (short)_raf.readUnsignedByte();
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public short getShort(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				return _raf.readShort();
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getUnsignedShort(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				return _raf.readUnsignedShort();
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getMedium(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getUnsignedMedium(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				final byte[] buf = new byte[3];
				final int len = _raf.read(buf);
				int sum = 0;
				for (byte b : buf)
					sum += b;
				return sum / len;
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getInt(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				return _raf.readInt();
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long getUnsignedInt(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getLong(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				return _raf.readLong();
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public char getChar(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				return _raf.readChar();
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public float getFloat(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				return _raf.readFloat();
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double getDouble(int index) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				return _raf.readDouble();
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst) {
		return getBytes(index, dst, dst.writableBytes());
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst, int length) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				final byte[] buf = new byte[length];
				final int len = _raf.read(buf);
				dst.writeBytes(buf, 0, len);
				return this;
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				final byte[] buf = new byte[length];
				final int len = _raf.read(buf);
				dst.writerIndex(dstIndex);
				dst.writeBytes(buf, 0, len);
				return this;
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf getBytes(int index, byte[] dst) {
		return getBytes(index, dst, 0, dst.length);
	}

	@Override
	public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				_raf.read(dst, dstIndex, length);
				return this;
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf getBytes(int index, ByteBuffer dst) {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				final byte[] buf = new byte[dst.remaining()];
				final int len = _raf.read(buf);
				dst.put(buf, 0, len);
				return this;
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				final byte[] buf = new byte[length];
				final int len = _raf.read(buf);
				out.write(buf, 0, len);
				return this;
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
		try {
			final long fp = _raf.getFilePointer();
			try {
				_raf.seek(index);
				final byte[] buf = new byte[length];
				final int len = _raf.read(buf);
				out.write(ByteBuffer.wrap(buf, 0, len));
				return len;
			} finally {
				_raf.seek(fp);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf setBoolean(int index, boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setByte(int index, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setShort(int index, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setMedium(int index, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setInt(int index, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setLong(int index, long value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setChar(int index, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setFloat(int index, float value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setDouble(int index, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setBytes(int index, byte[] src) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setBytes(int index, ByteBuffer src) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int setBytes(int index, InputStream in, int length) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf setZero(int index, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean readBoolean() {
		try {
			return _raf.readBoolean();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte readByte() {
		try {
			return _raf.readByte();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public short readUnsignedByte() {
		try {
			return (short)_raf.readUnsignedByte();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public short readShort() {
		try {
			return _raf.readShort();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int readUnsignedShort() {
		try {
			return _raf.readUnsignedShort();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int readMedium() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int readUnsignedMedium() {
		try {
			final byte[] buf = new byte[3];
			final int len = _raf.read(buf);
			int sum = 0;
			for (byte b : buf)
				sum += b;
			return sum / len;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int readInt() {
		try {
			return _raf.readInt();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long readUnsignedInt() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long readLong() {
		try {
			return _raf.readLong();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public char readChar() {
		try {
			return _raf.readChar();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public float readFloat() {
		try {
			return _raf.readFloat();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double readDouble() {
		try {
			return _raf.readDouble();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf readBytes(int length) {
		try {
			final byte[] buf = new byte[length];
			final int len = _raf.read(buf);
			return Unpooled.wrappedBuffer(buf, 0, len);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf readSlice(int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst) {
		return readBytes(dst, dst.writableBytes());
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst, int length) {
		try {
			final byte[] buf = new byte[length];
			final int len = _raf.read(buf);
			dst.writeBytes(buf, 0, len);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
		try {
			final byte[] buf = new byte[length];
			final int len = _raf.read(buf);
			dst.writerIndex(dstIndex);
			dst.writeBytes(buf, 0, len);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf readBytes(byte[] dst) {
		return readBytes(dst, 0, dst.length);
	}

	@Override
	public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
		try {
			_raf.read(dst, dstIndex, length);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf readBytes(ByteBuffer dst) {
		try {
			final byte[] buf = new byte[dst.remaining()];
			final int len = _raf.read(buf);
			dst.put(buf, 0, len);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf readBytes(OutputStream out, int length) throws IOException {
		try {
			final byte[] buf = new byte[length];
			final int len = _raf.read(buf);
			out.write(buf, 0, len);
			return this;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int readBytes(GatheringByteChannel out, int length) throws IOException {
		try {
			final byte[] buf = new byte[length];
			final int len = _raf.read(buf);
			out.write(ByteBuffer.wrap(buf, 0, len));
			return len;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuf skipBytes(int length) {
		writerIndex(writerIndex() + length);
		return this;
	}

	@Override
	public ByteBuf writeBoolean(boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeByte(int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeShort(int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeMedium(int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeInt(int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeLong(long value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeChar(int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeFloat(float value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeDouble(double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeBytes(byte[] src) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeBytes(ByteBuffer src) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int writeBytes(InputStream in, int length) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf writeZero(int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(int fromIndex, int toIndex, byte value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int bytesBefore(byte value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int bytesBefore(int length, byte value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int bytesBefore(int index, int length, byte value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int forEachByte(ByteBufProcessor processor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int forEachByte(int index, int length, ByteBufProcessor processor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int forEachByteDesc(ByteBufProcessor processor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf copy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf copy(int index, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf slice() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf slice(int index, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf duplicate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int nioBufferCount() {
		return 1; // TODO
	}

	@Override
	public ByteBuffer nioBuffer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuffer nioBuffer(int index, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuffer internalNioBuffer(int index, int length) {
		try {
			FileChannel inChannel = _raf.getChannel();
			MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, index, length);
			buffer.load();
			return buffer;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ByteBuffer[] nioBuffers() {
		return new ByteBuffer[]{internalNioBuffer(0, _size)};
	}

	@Override
	public ByteBuffer[] nioBuffers(int index, int length) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasArray() {
		return false;
	}

	@Override
	public byte[] array() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int arrayOffset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasMemoryAddress() {
		return false;
	}

	@Override
	public long memoryAddress() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString(Charset charset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString(int index, int length, Charset charset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int compareTo(ByteBuf buffer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ByteBuf retain(int increment) {
		return this;
	}

	@Override
	public boolean release() {
		return false;
	}

	@Override
	public boolean release(int decrement) {
		return false;
	}

	@Override
	public int refCnt() {
		return 0;
	}

	@Override
	public ByteBuf retain() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LocalFileByteBuf that = (LocalFileByteBuf)o;
		return _mark == that._mark && _file.equals(that._file);
	}

	@Override
	public int hashCode() {
		int result = _file.hashCode();
		result = 31 * result + _mark;
		return result;
	}

	@Override
	public String toString() {
		return "LocalFileByteBuf{" +
				"_file=" + _file +
				", _mark=" + _mark +
				'}';
	}
}
