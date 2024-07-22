package com.hjq.demo.util;


import androidx.annotation.NonNull;

/**
 * 格式工具
 * https://gitee.com/clg-123/bluetooth-mesh-communication
 * 基于安卓的蓝牙Mesh上位机应用----格式工具
 */
public class FormatUtil {
    /**
     * 字节数组转16进制字符串
     * @param bArr
     * @return
     */
    public static String bytesToHexString(byte[] bArr) {
        if (bArr == null || bArr.length==0)
            return "";
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;
        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase() + " ");
        }
        return sb.toString();
    }

    /**
     * 字节数组转16进制字符串 去掉空格
     * @param bArr
     * @return
     */
    public static String bytesToHexStringNoSpace(byte[] bArr) {
        if (bArr == null || bArr.length==0)
            return "";
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;
        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase() );
        }
        return sb.toString();
    }

    /**
     * 字节数组转16进制字符串
     * @param bArr
     * @param len
     * @return
     */
    public static String bytesToHexString(byte[] bArr,int len) {
        if (bArr == null || bArr.length==0 || len>bArr.length)
            return "";
        StringBuffer sb = new StringBuffer(len);
        String sTmp;
        for (int i = 0; i < len; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase() + " ");
        }
        return sb.toString();
    }

    /**
     * 16进制字符串转字节数组
     * @param arg
     * @return
     */
    public static byte[] hexStringToBytes(@NonNull final String arg) {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
            /* 将char数组中的值转成一个实际的十进制数组 */
            int EvenLength = (length % 2 == 0) ? length : length + 1;


            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                /* 将 每个char的值每两个组成一个16进制数据 */
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[] {};
    }


    /**
     * 16进制字符串转字节数组 带16位CRC
     * @param arg
     * @return
     */
    public static byte[] BytesAddCrc(@NonNull final String arg) {
        byte[] sourceBytes = hexStringToBytes(arg);
        int crcResult = RecodeUtil.getCrc16(sourceBytes);
        String newstr =  arg + RecodeUtil.byte4HexString(((crcResult & 0xff) << 8) | (crcResult >> 8));
        return hexStringToBytes(newstr);
    }

    public static String string2HexString(String hex) {
        String str = "";
        for (int i = 0; i < hex.length(); i++)
            str += Integer.toHexString((int) hex.charAt(i));
        return str;
    }

    public static String hexString2String(String hex) {
        if (hex == null || hex.equals(""))
            return "";
        hex = hex.replace(" ", "");
        byte[] hexValue = new byte[hex.length() / 2];
        for (int i = 0; i < hexValue.length; i++) {
            try {
                hexValue[i] = (byte) (0xff & Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            hex = new String(hexValue, "gbk");
            //new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return hex;
    }

    public static byte[] hexString2Bytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }
    /**字符到字节*/
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //https://zhuanlan.zhihu.com/p/580220308
    ////
    // 大端模式，是指数据的高字节保存在内存的低地址中，而数据的低字节保存在内存的高地址中，这样的存储模式有点儿类似于把数据当作字符串顺序处理：地址由小向大增加，数据从高位往低位放；这和我们的阅读习惯一致。
    //小端模式，是指数据的高字节保存在内存的高地址中，而数据的低字节保存在内存的低地址中，这种存储模式将地址的高低和数据位权有效地结合起来，高地址部分权值高，低地址部分权值低。
    ///
    /**
     * 字节数组转int 大端模式
     */
    public static int byteArrayToIntLittleEndian(byte[] bytes, int byteOffset, int byteCount) {
        int intValue = 0;
        for (int i = byteOffset; i < (byteOffset + byteCount); i++) {
            intValue |= (bytes[i] & 0xFF) << (8 * (i - byteOffset));
        }
        return intValue;
    }

    /**
     * 字节数组转int 小端模式
     */
    public static int byteArrayToIntBigEndian(byte[] bytes, int byteOffset, int byteCount) {
        int intValue = 0;
        for (int i = byteOffset; i < (byteOffset + byteCount); i++) {
            intValue <<= 8;
            int b = bytes[i] & 0xFF;
            intValue |= b;
        }
        return intValue;
    }
}
