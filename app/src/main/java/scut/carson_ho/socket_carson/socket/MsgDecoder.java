package scut.carson_ho.socket_carson.socket;


import android.util.Log;

public class MsgDecoder {

//	private static final Logger log = LoggerFactory.getLogger(MsgDecoder.class);

	private BitOperator bitOperator;
	private BCD8421Operater bcd8421Operater;

	public MsgDecoder() {
		this.bitOperator = new BitOperator();
		this.bcd8421Operater = new BCD8421Operater();
	}

	public UpPackageData bytes2PackageData(byte[] data) {
		UpPackageData ret = new UpPackageData();

		// 0. 终端套接字地址信息
		// ret.setChannel(msg.getChannel());
		// 1. 16byte 或 12byte 消息头
		UpPackageData.UpMsgHeader msgHeader = this.parseMsgHeaderFromBytes(data);
		ret.setMsgHeader(msgHeader);

		int msgBodyByteStartIndex = 23;
		Log.i("LLL",msgHeader.getMsgBodyLength()+"");
		if(data.length>23){
        	byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
    		System.arraycopy(data, msgBodyByteStartIndex, tmp, 0, tmp.length);
    		ret.setMsgBodyBytes(tmp);
        }

		// 3. 去掉分隔符之后，最后一位就是校验码
		// int checkSumInPkg =
		// this.bitOperator.oneByteToInteger(data[data.length - 1]);
		int checkSumInPkg = data[data.length - 2];
		int calculatedCheckSum = this.bitOperator.getCheckSum4JT808(data, 0, data.length - 2);
		System.out.println("检验码："+ (calculatedCheckSum & 0xff));
		ret.setCheckSum(checkSumInPkg);
		if (checkSumInPkg !=  calculatedCheckSum) {
//			log.warn("检验码不一致,msgid:{},pkg:{},calculated:{}", msgHeader.getMsgId(), checkSumInPkg, calculatedCheckSum);
		}
		return ret;
	}

	private UpPackageData.UpMsgHeader parseMsgHeaderFromBytes(byte[] data) {
		UpPackageData.UpMsgHeader msgHeader = new UpPackageData.UpMsgHeader();
		// 1. 消息ID 命令字 byte 第2字节 
		msgHeader.setMsgId( data[2]);
		// 消息长度 
		msgHeader.setMsgBodyLength(this.parseIntFromBytes(data,3, 2));
		// 终端号码
		msgHeader.setTerminalId(this.parseBcdStringFromBytes(data,5, 16));
		// 消息ID
		msgHeader.setFlowId(this.parseIntFromBytes(data, 21, 1));
		// 2. 消息体属性 word(16)=================>
//		 System.arraycopy(data, 2, tmp, 0, 2);
//		 int msgBodyProps = this.bitOperator.twoBytesToInteger(tmp);
		return msgHeader;
	}

	protected String parseStringFromBytes(byte[] data, int startIndex, int lenth) {
		return this.parseStringFromBytes(data, startIndex, lenth, null);
	}

	private String parseStringFromBytes(byte[] data, int startIndex, int lenth, String defaultVal) {
		try {
			byte[] tmp = new byte[lenth];
			System.arraycopy(data, startIndex, tmp, 0, lenth);
			return new String(tmp, TPMSConsts.string_charset);
		} catch (Exception e) {
			e.printStackTrace();
			return defaultVal;
		}
	}

	private String parseBcdStringFromBytes(byte[] data, int startIndex, int lenth) {
		return this.parseBcdStringFromBytes(data, startIndex, lenth, null);
	}

	private String parseBcdStringFromBytes(byte[] data, int startIndex, int lenth, String defaultVal) {
		try {
			byte[] tmp = new byte[lenth];
			System.arraycopy(data, startIndex, tmp, 0, lenth);
			return new String(tmp);
//			return this.bcd8421Operater.bcd2String(tmp);
		} catch (Exception e) {
			e.printStackTrace();
			return defaultVal;
		}
	}

	private int parseIntFromBytes(byte[] data, int startIndex, int length) {
		return this.parseIntFromBytes(data, startIndex, length, 0);
	}

	private int parseIntFromBytes(byte[] data, int startIndex, int length, int defaultVal) {
		try {
			// 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
			final int len = length > 4 ? 4 : length;
			byte[] tmp = new byte[len];
			System.arraycopy(data, startIndex, tmp, 0, len);
			return bitOperator.byteToInteger(tmp);
		} catch (Exception e) {
			e.printStackTrace();
			return defaultVal;
		}
	}

	private float parseFloatFromBytes(byte[] data, int startIndex, int length) {
		return this.parseFloatFromBytes(data, startIndex, length, 0f);
	}

	private float parseFloatFromBytes(byte[] data, int startIndex, int length, float defaultVal) {
		try {
			// 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
			final int len = length > 4 ? 4 : length;
			byte[] tmp = new byte[len];
			System.arraycopy(data, startIndex, tmp, 0, len);
			return bitOperator.byte2Float(tmp);
		} catch (Exception e) {
			e.printStackTrace();
			return defaultVal;
		}
	}

//
//	public HeartBeatMsg HeartBeatMsg(UpPackageData packageData) {
//		// TODO Auto-generated method stub
//		HeartBeatMsg ret = new HeartBeatMsg(packageData);
//		return ret;
//	}
}
