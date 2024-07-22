package com.hjq.demo.util;


import com.hjq.http.EasyLog;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class CommonUtil {
    public static boolean isValidEmail(String email) {
        if ((email != null) && (!email.isEmpty())) {
            return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
        }
        return false;
    }

    /**
     * 正则表达式校验密码必须是包含大小写字母、数字、特殊符号的8位以上组合
     * @param pwd
     * @return
     */
    public static boolean isValidPassword(String pwd) {
        if ((pwd != null) && (!pwd.isEmpty())) {
            return Pattern.matches("^(?![A-Za-z0-9]+$)(?![a-z0-9\\\\W]+$)(?![A-Za-z\\\\W]+$)(?![A-Z0-9\\\\W]+$)[a-zA-Z0-9\\\\W]{10,}$", pwd);
        }
        return false;
    }

    /**
     * https://blog.csdn.net/timchen525/article/details/75042670
     * 解决Integer.parseInt(String str)的异常（NumberFormatException）方法
     * @param s
     * @return
     */
    public static Integer StringToInt(String s){
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    public static int inArrayIndex(List<String> stringArray, String str, boolean caseInsensetive)
    {
        if(stringArray== null || stringArray.size()==0){
            return -1;
        }
        if (str == null) str = "";
        if (str.equals("")){
            return -1;
        }
        if (caseInsensetive)
        {
            for (int i = 0; i < stringArray.size(); i++)
            {
                // // EasyLog.print("======================================================="+ str.toLowerCase() + ", " + stringArray.get(i).toLowerCase());
                if (str.toLowerCase().startsWith(stringArray.get(i).toLowerCase()))
                    return i;
            }
        }
        else
        {
            for (int i = 0; i < stringArray.size(); i++)
            {
                if (str.startsWith(stringArray.get(i)))
                    return i;
            }
        }
        return -1;
    }

    /**
     *  实际应用中推荐使用Hutool工具类中的StrUtil类下方法（isEmpty、isNotEmpty、isBlank、isNotBlank）进行判null或者判""，对应源码如下：
     *  https://blog.csdn.net/xiaocui1995/article/details/129700035
      * @param obj
     * @return
     */
    public static boolean isEmptyIfStr(Object obj) {
        if (null == obj) {
            return true;
        } else if (obj instanceof CharSequence) {
            return 0 == ((CharSequence)obj).length();
        } else {
            return false;
        }
    }




}
