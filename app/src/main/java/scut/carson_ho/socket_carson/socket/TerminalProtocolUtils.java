package scut.carson_ho.socket_carson.socket;

import java.io.ByteArrayOutputStream;


/**
 * 可视对讲协议转义工具类
 * 
 * <pre>
 * 0x7d01 <====> 0x7d
 * 0x7d02 <====> 0x7e
 * </pre>
 * 
 * @author 李春青
 *
 */
public class TerminalProtocolUtils {
//	private final Logger log = LoggerFactory.getLogger(getClass());
	private BitOperator bitOperator;
	private BCD8421Operater bcd8421Operater;

	public TerminalProtocolUtils() {
		this.bitOperator = new BitOperator();
		this.bcd8421Operater = new BCD8421Operater();
	}

	/**
	 * 接收消息时转义<br>
	 * 
	 * <pre>
	 * 0x7d01 <====> 0x7d
	 * 0x7d02 <====> 0x7e
	 * </pre>
	 * 
	 * @param bs
	 *            要转义的字节数组
	 * @param start
	 *            起始索引
	 * @param end
	 *            结束索引
	 * @return 转义后的字节数组
	 * @throws Exception
	 */
	public byte[] doEscape4Receive(byte[] bs, int start, int end) throws Exception {
		if (start < 0 || end > bs.length)
			throw new ArrayIndexOutOfBoundsException("doEscape4Receive error : index out of bounds(start=" + start
					+ ",end=" + end + ",bytes length=" + bs.length + ")");
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			for (int i = 0; i < start; i++) {
				baos.write(bs[i]);
			}
			for (int i = start; i < end - 1; i++) {
				if (bs[i] == 0x7d && bs[i + 1] == 0x01) {
					baos.write(0x7d);
					i++;
				} else if (bs[i] == 0x7d && bs[i + 1] == 0x02) {
					baos.write(0x7e);
					i++;
				} else {
					baos.write(bs[i]);
				}
			}
			for (int i = end - 1; i < bs.length; i++) {
				baos.write(bs[i]);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if (baos != null) {
				baos.close();
				baos = null;
			}
		}
	}

	/**
	 * 
	 * 发送消息时转义<br>
	 * 
	 * <pre>
	 *  0x7e <====> 0x7d02
	 * </pre>
	 * 
	 * @param bs
	 *            要转义的字节数组
	 * @param start
	 *            起始索引
	 * @param end
	 *            结束索引
	 * @return 转义后的字节数组
	 * @throws Exception
	 */
	public byte[] doEscape4Send(byte[] bs, int start, int end) throws Exception {
		if (start < 0 || end > bs.length)
			throw new ArrayIndexOutOfBoundsException("doEscape4Send error : index out of bounds(start=" + start
					+ ",end=" + end + ",bytes length=" + bs.length + ")");
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			for (int i = 0; i < start; i++) {
				baos.write(bs[i]);
			}
			for (int i = start; i < end; i++) {
				if (bs[i] == 0x7e) {
					baos.write(0x7d);
					baos.write(0x02);
				} else {
					baos.write(bs[i]);
				}
			}
			for (int i = end; i < bs.length; i++) {
				baos.write(bs[i]);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if (baos != null) {
				baos.close();
				baos = null;
			}
		}
	}

	public int generateMsgBodyProps(int msgLen, int enctyptionType, boolean isSubPackage, int reversed_14_15) {
		// [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
		// [10-12] 0001,1100,0000,0000(1C00)(加密类型)
		// [ 13_ ] 0010,0000,0000,0000(2000)(是否有子包)
		// [14-15] 1100,0000,0000,0000(C000)(保留位)
//		if (msgLen >= 1024)
//			log.warn("The max value of msgLen is 1023, but {} .", msgLen);
		int subPkg = isSubPackage ? 1 : 0;
		int ret = (msgLen & 0x3FF) | ((enctyptionType << 10) & 0x1C00) | ((subPkg << 13) & 0x2000)
				| ((reversed_14_15 << 14) & 0xC000);
		return ret & 0xffff;
	}

	public byte[] generateMsgHeader(int msgType, String terminalPhone, int msgLength, int flowId)
			throws Exception {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			// 1. 消息ID byte(8)
			baos.write(bitOperator.integerTo1Bytes(msgType));
			// 2. 消息长度 word(16)
			baos.write(bitOperator.integerTo2Bytes(msgLength));
			// 3. 终端手机号 bcd[16]
//			baos.write(bcd8421Operater.string2Bcd(terminalPhone));
			baos.write(terminalPhone.getBytes());
			// 4. 消息流水号 byte(8),按发送顺序从 0 开始循环累加
			baos.write(bitOperator.integerTo1Bytes(flowId));
			// 消息包封装项 此处不予考虑
			return baos.toByteArray();
		} finally {
			if (baos != null) {
				baos.close();
			}
		}
	}
}
