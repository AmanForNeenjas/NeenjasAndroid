package com.hjq.demo.util;

import com.hjq.http.EasyLog;

import java.util.ArrayList;
import java.util.List;

public class RecodeUtil
{

    /** 基础地址定义  */
    /** 从机地址*/
    public final static String   STANDBYADDR= "02"; //从机地址
    /** 功能参数 */
    public final  static String   FUNCTIONPARAM= "03";
    //final  static String   registerAddressHigh= "33";
    public final  static String   REGISTERCONTENTHIGH= "00";
    public final  static String   REGISTERCONTENTLOW= "01";
    public final  static String   REGISTERCONTENTALL= "7D";
    /**
     * 计算寄存器地址
     * @param standByAddr 从机地址
     * @param functionParam 功能参数
     * @param registerContentHigh
     * @param registerContentLow
     * @param registerAddressLow
     * @param registerAddressLow
     * @param  registerAddressLow
     * @param  registerAddressLow
     * @return
     */
    public static String getRegisterAddr(String standByAddr, String functionParam, String registerAddressHigh, String registerAddressLow, String registerContentHigh,String registerContentLow){
        return standByAddr + functionParam + registerAddressHigh + registerAddressLow +  registerContentHigh + registerContentLow;
    }

    /**
     * 预充电量值设置为xy
     * @return
     */
    public static String getRegisterAddrSetMaxImplus(String setval){
        return "01"+  "41" + "32" + "01" +  "00" + setval  ;
    }

    public static String getRegisterAddrSetMinDisCharging(String setval){
        return "01"+  "41" + "32" + "02" +  "00" + setval  ;
    }

    /**
     *
     * @return
     */
    public static String getRegisterAddrAll(){
        return STANDBYADDR + FUNCTIONPARAM + "33" + "01" +  REGISTERCONTENTHIGH + REGISTERCONTENTALL  ;
    }


    /**
     * 读取的数据解码具体意义  一次读125位
     * @param readBytes 从蓝牙读取的数据
     * @return
     */
    public static List<Integer> readDecodeAll(byte[] readBytes){
        List<Integer> resultList= new ArrayList<>();
        if(CRCCheck(readBytes)){
            int len = (readBytes.length-5)/2;
            for(int index =1; index<= len; index++){
                int val=(readBytes[index*2+1]  & 0xff ) * 256  + (readBytes[index*2+2] & 0xff);

                //int val= readBytes[index*2+1]  * 256 + readBytes[index*2+2];
                if(val> 32768){
                    val = -(65536 - val);
                }
                resultList.add(val);
            }
        }
        return resultList;
    }

    public static String readDecodAllToHexStr(byte[] readBytes){
        StringBuffer stringBuffer = new StringBuffer();
        int len = (readBytes.length-5)/2;
        for(int index =1; index<= len; index++){
            byte [] temp = new byte[]{ readBytes[index*2+1], readBytes[index*2+2]};
            stringBuffer.append(FormatUtil.bytesToHexString(temp));
            if(index != len){
                stringBuffer.append(",");
            }

        }
         return stringBuffer.toString();
    }

    /**
     * 读出当前设备状态
     * @param readBytes
     * @return
     */
    public static int readStatusarray(int[] readBytes){
        if(readBytes.length==0){
            return 99;
        }else{
            if(readBytes[0] == 1){
                return 0;
            }else if(readBytes[1] == 1){
                return 1;
            }else if(readBytes[2] == 1){
                return 2;
            }else if(readBytes[3] == 1){
                return 3;
            }else if(readBytes[4] == 1){
                return 4;
            }else if(readBytes[5] == 1){
                return 5;
            }else if(readBytes[6] == 1){
                return 6;
            }else if(readBytes[7] == 1){
                return 7;
            }else if(readBytes[8] == 1){
                return 8;
            }else if(readBytes[9] == 1){
                return 9;
            }else if(readBytes[10] == 0){
                return 10;
            }else if(readBytes[10] == 1){
                return 11;
            }else if(readBytes[10] == 2){
                return 12;
            }else if(readBytes[10] == 3){
                return 13;
            }else if(readBytes[11] == 1){
                return 14;
            }else if(readBytes[12] == 1){
                return 15;
            }
            else {
                return 99;
            }
        }

    }
    /**
     * 系统状态标志变量	"    // bit0: 1: 太阳能充电（MPPT工作中）
     *     // bit1: 1:  AC输出中
     *     // bit2: 1:  充电中
     *     // bit3: 1:  直流输出中，
     *     // bit4: 1:  风扇故障
     *     // bit5: 1:  低温保护中
     *     // bit6: 1:  过温保护中
     *     // bit7: 1:  APP、上位机链接上
     *     // bit8: 1:  快充显示标志
     *     // bit9: 1:  过载显示标志
     *     // bit11-10: 1:  照明灯亮，2， 照明灯强，3：SOS，0：关闭
     *     // bit12: 1:  氛围灯Open
     *     // bit13: 1:  驱蚊灯Open"
     * @param
     * @return
     */
    public static int [] readDecodeStatus(byte readByteHight, byte readByteLow){
        int[] resultList= new int[16];
        int result = readByteLow & 0xFF;
        int val = result & 0x01;
        resultList[0]=(val);
        val = (result & 0x02) >>1;
        resultList[1]=(val);
        val = (result & 0x04) >>2;
        resultList[2]=(val);
        val = (result & 0x08) >>3;
        resultList[3]=(val);

        // bit4: 1:  风扇故障
        val = (result & 0x10) >>4;
        resultList[4]=(val);

        val = (result & 0x20) >>5;
        resultList[5]=(val);
        val = (result & 0x40) >>6;
        resultList[6]=(val);
        // bit7: 1:  APP、上位机链接上
        val = (result & 0x80) >>7;
        resultList[7]=(val);

        result = readByteHight & 0xFF;
        // bit8: 1:  快充显示标志
        val = result & 0x01 ;
        resultList[8]=(val);
        val = (result & 0x02) >>1;
        resultList[9]=(val);
        val = (result & 0x0C) >>2;
        resultList[10]=(val);
        val = (result & 0x10) >>4;
        resultList[11]=(val);
        val = (result & 0x20) >>5;
        resultList[12]=(val);
        val = (result & 0x40) >>6;
        resultList[13]=(val);
        val = (result & 0x80) >>7;
        resultList[14]=(val);

        return resultList;
    }

    public static int [] readDecodeStatusTwo(byte readByteHight, byte readByteLow){
        int[] resultList= new int[16];
        int result = readByteLow & 0xFF;
        int val = result & 0x01;
        resultList[0]=(val);
        val = (result & 0x02) >>1;
        resultList[1]=(val);
        val = (result & 0x04) >>2;
        resultList[2]=(val);
        val = (result & 0x08) >>3;
        resultList[3]=(val);

        // bit4: 1:
        val = (result & 0x10) >>4;
        resultList[4]=(val);

        val = (result & 0x20) >>5;
        resultList[5]=(val);
        val = (result & 0x40) >>6;
        resultList[6]=(val);
        // bit7: 1:  APP、上位机链接上
        val = (result & 0x80) >>7;
        resultList[7]=(val);

        result = readByteHight & 0xFF;
        // bit8: 1:  快充显示标志
        val = result & 0x01 ;
        resultList[8]=(val);
        val = (result & 0x02) >>1;
        resultList[9]=(val);
        val = (result & 0x04) >>2;
        resultList[10]=(val);
        val = (result & 0x08) >>3;
        resultList[11]=(val);
        val = (result & 0x10) >>4;
        resultList[12]=(val);
        val = (result & 0x20) >>5;
        resultList[13]=(val);
        val = (result & 0x40) >>6;
        resultList[14]=(val);
        val = (result & 0x80) >>7;
        resultList[15]=(val);

        return resultList;
    }

    /**
     * 读取的数据解码具体意义
     * @param readBytes 从蓝牙读取的数据
     * @return
     */
    public static int readDecode(byte[] readBytes){

        if(CRCCheck(readBytes)){

            int val=(readBytes[3]  & 0xff )<< 8 + readBytes[4];
            if(val> 32768){
                val = -(65536 - val);
            }
            return val;

        }else{
            return -1111;
        }

    }

    /**
     * 校验接收数据
     * @param readBytes 从蓝牙读取的数据
     * @return
     */
    public static boolean CRCCheck(byte[] readBytes) {
        if(readBytes.length < 3){
            return false;
        }else{
            byte[] realBuffer = new byte[readBytes.length-2];
            //// EasyLog.print("======================== CRCCheck-> "+realBuffer.toString());
            //开始生成CRC校验码
            System.arraycopy(readBytes,0,realBuffer ,0,readBytes.length-2);
            int crcResult = getCrc16(realBuffer);
            //// EasyLog.print("======================== crcResult -> "+crcResult );
            //String sourcestr = byte2HexString(readBytes[readBytes.length -2 ]) + byte2HexString(readBytes[readBytes.length -1 ]);
            //String crcstr =  byte4HexString(((crcResult & 0xff) << 8) | (crcResult >> 8));
            int rawcrc = FormatUtil.byteArrayToIntBigEndian(new byte[]{readBytes[readBytes.length-1],readBytes[readBytes.length-2]},0,2);
            //// EasyLog.print("========================boot  crcResult: " + crcResult);
            //// EasyLog.print("========================boot  rawcrc: " + rawcrc);
            if(crcResult == rawcrc){
                //// EasyLog.print("========================boot  rawcrc res true: ");
                return true;
            }else{
                return false;
            }

        }

    }
    public static int readDecodeUpgade(byte[] readBytes, byte[] copysend, String feedback, int datanum) {
        if(!RecodeUtil.CRCCheck(readBytes)){
            // This frame of data did not pass CRCCheck!

            return 2;
        }else{
            if(((readBytes[readBytes.length - 1] &0xff) ==0x9f)&&((readBytes[readBytes.length - 2] &0xff )== 0xbe))
            {
                return 2;
            }
        }
        if(datanum != readBytes.length){
            if(feedback.equals("Load") || feedback.equals("Check") || feedback.equals("Erase")){
                // This frame of data has the wrong length!
                return 3;
            }else if(feedback.equals("Reset")){
                return 1;
            }else {
                //Failed: Timeout or Invalid feedback, Length of Feedback is Wrong!
                return 999;
            }
        }

        if(feedback.equals("Erase")){
           /* try{
                Thread.sleep(1500);
            }catch (Exception e){

            }*/

        }


        //  for DC start
        if (feedback.equals("Check") && !(( (readBytes[0]&0xff) == 0x50) && ((readBytes[1]&0xff) == 0x10) && (0xaa == (readBytes[12]&0xff)) && (0xaa == (readBytes[13]&0xff)))){
            return 4;
        }
        if (feedback.equals("Erase") && !(((readBytes[0] &0xff)== 0x50) && ((readBytes[1]&0xff) == 0x30) && (0xaa == (readBytes[3]&0xff)) )){
            return 4;
        }
        if (feedback.equals("Load") && !(((readBytes[0]&0xff) == 0x50) && ((readBytes[1]&0xff) == 0x32) && (readBytes[2] == copysend[2]) && (copysend[3] == readBytes[3]) && (copysend[4] ==readBytes[4]) && (copysend[5] == readBytes[5]))) {
            return 4;
        }
        if (feedback.equals("Reset")){
            /*try{
                Thread.sleep(1000);
            }catch (Exception e){

            }*/
        }
        //  for DC end

        // for PFC start
        if (feedback.equals("PFCCheck") && !(((readBytes[0]&0xff) == 0x50) && ((readBytes[1]&0xff) == 0x10) && ((readBytes[13]&0xff) == 0xaa)) && (copysend[2] == readBytes[2]) && (copysend[3] == readBytes[3]) && (copysend[4] == readBytes[4]) && (copysend[5] == readBytes[5]))
        {
            return 5;
        }
        if (feedback.equals("PFCLoad") && !(((readBytes[0]&0xff) == 0x50) && ((readBytes[1]&0xff) == 0x32) && (0xaa == (readBytes[6]&0xff)) && (copysend[2] == readBytes[2]) && (copysend[3] == readBytes[3]) && (copysend[4] == readBytes[4]) && (copysend[5] == readBytes[5]))){
            return 5;
        }
        // for PFC end

        if(feedback.equals("Modify") || feedback.equals("Control") && copysend.equals(readBytes)){
            if(copysend.equals(readBytes)){
                return 6;
            }
            return 1;
        }
        // succeed
        return 1;

    }


    /**
     * 转换8位 16进制字符串小写
     * @param input
     * @return
     */
    public static String byte8HexString(int  input){
        String hex = Integer.toHexString(input & 0xFFFFFFFF);
        if (hex.length() == 1) {
            hex = "0000000" + hex;
        }else if(hex.length() ==2){
            hex = "000000" + hex;
        }else if(hex.length() ==3){
            hex = "00000" + hex;
        }else if(hex.length() ==4){
            hex = "0000" + hex;
        }else if(hex.length() ==5){
            hex = "000" + hex;
        }else if(hex.length() ==6){
            hex = "00" + hex;
        }else if(hex.length() ==7){
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 转换4位 16进制字符串小写
     * @param input
     * @return
     */
    public static String byte4HexString(int  input){
        String hex = Integer.toHexString(input & 0xFFFF);
        if (hex.length() == 1) {
            hex = "000" + hex;
        }else if(hex.length() ==2){
            hex = "00" + hex;
        }else if(hex.length() ==3){
            hex = "0" + hex;
        }
        return hex;
    }

    //int 转化为字节数组
    public static byte[] intTobyte(int num)
    {
        return new byte[] {(byte)((num>>24)&0xff),(byte)((num>>16)&0xff),(byte)((num>>8)&0xff),(byte)(num&0xff)};
    }

    public static byte[] intTobyteTwo(int num)
    {
        return new byte[] {(byte)((num>>8)&0xff),(byte)(num&0xff)};
    }
    //字节数组转化为int
    public static int byteArrayToInt(byte[] arr)
    {
        return (arr[0]&0xff)<<24|(arr[1]&0xff)<<16|(arr[2]&0xff)<<8|(arr[3]&0xff);
    }


    /**
     * byte转换2位 16进制字符串小写
     * @param oneByte
     * @return
     */
    public static String byte2HexString(byte oneByte){
        String hex = Integer.toHexString(oneByte & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    public static String int2HexString(int oneByte){
        String hex = Integer.toHexString(oneByte & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    /**
     * 获取验证码byte数组，基于Modbus CRC16的校验算法
     * https://blog.csdn.net/qq_34356024/article/details/78205530
     */
    public static int getCrc16(byte[] arr_buff) {
        int len = arr_buff.length;

        // 预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {
            // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));
            for (j = 0; j < 8; j++) {
                // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 0x0001) > 0) {
                    // 如果移出位为 1, CRC寄存器与多项式A001进行异或
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else
                    // 如果移出位为 0,再次右移一位
                    crc = crc >> 1;
            }
        }
        return crc;
    }


    public static String GetRealValues(String highadd, String lowadd, int receivData) {
        String realValue = "";
        switch (highadd.toLowerCase()){
            case "33":
                switch (lowadd.toLowerCase()){
                    case "01":
                    case "02":
                    case "4f":
                        realValue =  realValue+receivData;
                        break;
                    case "09":
                    case "0c": //Buck12V 输出功率
                    case "0e":
                    case "18":
                    case "1a":
                    case "3b": //USBC功率显示
                    case "3c": //USB功率显示
                    case "42": //交流输出功率
                        realValue = (float)receivData /10 + "";
                        break;
                    case "10":
                    case "11":
                        realValue = (float)receivData /100 + "";
                        break;
                    case "0f":
                        realValue = receivData + "%";
                        break;
                    case "4d":
                    case "4e":
                        realValue = (float)receivData * 10 + "";
                        break;
                    case "50":
                        int year = 2000 + receivData >>9;
                        int month = (receivData >> 5 )& 0x0f;
                        int date = receivData & 0x1f;
                        realValue = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", date);
                        break;
                    case "3a":
                        realValue = realValue+ receivData + '%';
                        break;
                    default:
                        realValue =  realValue+receivData;
                }
                break;
            case "20":
                break;
            default:
                realValue =  realValue+receivData;

        }
        return realValue;
    }

    /**
     * 通过MAC地址生成SN码
     * @param str
     * @return
     */
    public static String codeSN(String str){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("NA7550");
        for(int i =6; i<str.length(); i++){
            if(str.charAt(i) != ':'){
                stringBuffer.append((str.charAt(i)));
            }

        }
        return stringBuffer.toString();
    }

    /**
     * 两个字节数组合并
     * https://blog.csdn.net/ch_kexin/article/details/123889661
     * @param bt1
     * @param bt2
     * @return
     */
    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }


    /**
     * 多个字节数组合并
     * https://blog.csdn.net/u014411752/article/details/71247243?spm=1001.2101.3001.6661.1&utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-1-71247243-blog-123889661.pc_relevant_vip_default&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7ERate-1-71247243-blog-123889661.pc_relevant_vip_default&utm_relevant_index=1
     * @param values
     * @return
     */
    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

}
