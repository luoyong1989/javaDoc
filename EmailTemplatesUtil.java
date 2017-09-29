

import com.keruyun.store.base.entity.mongo.ConsultationRecord;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ly on 2017/9/29.
 */
public class EmailTemplatesUtil {
    public final static String CONSULTATION = "<html lang=\"en\">" +
            "<body>" +
            "    <h3>尊敬的{developerName}:</h3>" +
            "    <b>    你有如下咨询信息，请及时处理</b><br>" +
            "    <b>    咨询品牌: {brandName}</b><br>" +
            "    <b>    咨询门店: {shopName}</b><br>" +
            "    <b>    咨询来源: {source}</b><br>" +
            "    <b>    联系人: {contact}</b><br>" +
            "    <b>    性别: {gender}</b><br>" +
            "    <b>    联系电话: {tel}</b><br>" +
            "    <b>    电子邮箱: {email}</b><br>" +
            "    <b>    咨询描述: {describe}</b><br>" +
            "    <b>    咨询时间: {serverCreateTime}</b><br><p>" +
            "    <b>来自客如云应用市场</b><br>" +
            "</body>" +
            "</html>";

    private final static String DATE_CLASS_NAME = "class java.util.Date";
    private final static char PREFIX = '{';
    private final static char SUFFIX = '}';
    private final static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String DEF_REGEX = "\\{(.+?)\\}";

    /**
     * 使用 Matcher 进行创建
     * @param template
     * @param data
     * @param regex
     * @return
     */
    public static String render(String template, Object data, String regex) {
        if (StringUtils.isBlank(template)) {
            return "";
        }
        if (StringUtils.isBlank(regex)) {
            return template;
        }
        if (data == null) {
            return template;
        }
        try {
            StringBuffer sb = new StringBuffer();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(template);
            while (matcher.find()) {
                String name = matcher.group(1);// 键名
                String value = getValue(name, data);// 键值
                if (value == null) {
                    value = "";
                }
                matcher.appendReplacement(sb, value);
            }
            matcher.appendTail(sb);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return template;

    }

    private static String getValue(String name, Object record) {
        String v = "--";
        try {
            Method m = record.getClass().getMethod("get" + getMethodName(name));
            Object o = m.invoke(record);
            if (null != o) {
                String type = o.getClass().toString();
                if (type.equals(DATE_CLASS_NAME)) {
                    Date date = (Date) o;
                    v = sf.format(date);
                } else {
                    v = String.valueOf(o);
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return v;
        }
        return v;
    }


    /**
     * 使用 char 替换创建
     * @param record   数据源
     * @param template 模板
     * @return
     */
    public static String convert(Object record, final String template) {
        String result = template;
        List<String> list = getField(template);
        for (String s : list) {
            String v = getValue(s,record);
            result = result.replace(getKey(s), v);
        }
        return result;
    }

    private static String getKey(String name) {
        return PREFIX + name + SUFFIX;
    }


    /**
     * 首字母大写
     *
     * @param fildeName
     * @return
     */
    private static String getMethodName(String fildeName) {
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }

    private static List<String> getField(final String str) {
        int[] index = {0};
        List<String> list = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == PREFIX) {
                index[0] = i + 1;
                String string = createString(index, str);
                list.add(string);
                i = index[0];
            }
        }
        return list;
    }

    private static String createString(int[] index, String str) {
        String s = "";
        for (int i = index[0]; i < str.length(); i++) {
            char c = str.charAt(i);
            index[0] = i;
            if (c == SUFFIX) {
                break;
            }
            s += c;
        }
        return s;
    }


    //------TEST---------

    private static ConsultationRecord getR() {
        ConsultationRecord record = new ConsultationRecord();
        record.setContact("15680892978");
        record.setGender("男");
        record.setTel("13489099888");
        record.setEmail("13489099888@163.com");
        record.setDescribe("大爷快来帮帮我啊 ，啊啊啊啊啊啊啊啊啊 啊");
        record.setUser(2479L);
        record.setUserName("gly");
        record.setShopIdenty(22222l);
        record.setBrandName("dddddd");
        record.setShopName("22ddfdf");
        record.setSource("WEB");
        record.setBrandId(22222l);
        record.setAppId("appid1");
        record.setDeveloperEmail("luoyong_cd@keruyun.com");
        record.setDeveloperName("罗勇");
        record.setServerCreateTime(new Date());
        record.setServerUpdateTime(new Date());
        return record;
    }

    public static void main(String[] args) {
        ConsultationRecord record = getR();
        Long t1 = System.currentTimeMillis();
        String fs = render(CONSULTATION,record,DEF_REGEX);
        Long t2 = System.currentTimeMillis();
        System.out.println("render Time  = " + (t2 - t1));


        t1 = System.currentTimeMillis();
        fs = convert(record,CONSULTATION);
        t2 = System.currentTimeMillis();
        System.out.println("convert Time  = " + (t2 - t1));

    }
}
