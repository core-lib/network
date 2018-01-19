package com.qfox.network.downloader;

import java.io.Serializable;

/**
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 * 
 * @author yangchangpei 646742615@qq.com
 *
 * @date 2015年8月13日 上午10:41:46
 *
 * @version 1.0.0
 */
public class Range implements Serializable, Cloneable {
	private static final long serialVersionUID = 5017190669771558520L;

	public static final Range ZERO = new Range(0, 0, 0, 0);

	public final long start;
	public final long end;
	public final long position;
	public final long length;

	private Range(long start, long end, long position, long length) {
		super();
		this.start = start;
		this.end = end;
		this.position = position;
		this.length = length;
	}

	public static Range forEnd(long start, long end) {
		if (start < 0) {
			throw new IllegalArgumentException("start index must not lesser than zero");
		}
		if (end <= start) {
			throw new IllegalArgumentException("end index must not equal or lesser than start index");
		}
		return new Range(start, end, start, end - start + 1);
	}

	public static Range forLength(long position, long length) {
		if (position < 0) {
			throw new IllegalArgumentException("position must not lesser than zero");
		}
		if (length <= 0) {
			throw new IllegalArgumentException("length must not equal or lesser than zero");
		}
		return new Range(position, position + length - 1, position, length);
	}

	public Range copy() {
		return new Range(start, end, position, length);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (start ^ (start >>> 32));
		result = prime * result + (int) (length ^ (length >>> 32));
		result = prime * result + (int) (position ^ (position >>> 32));
		result = prime * result + (int) (end ^ (end >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Range other = (Range) obj;
		if (start != other.start)
			return false;
		if (length != other.length)
			return false;
		if (position != other.position)
			return false;
		if (end != other.end)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Range [from=" + start + ", to=" + end + ", position=" + position + ", length=" + length + "]";
	}

}
