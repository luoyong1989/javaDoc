

import com.keruyun.store.base.entity.mongo.ConsultationRecord;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ly on 2017/9/29.
 */
public class EmailTemplatesUtil {
    private final static Map<String, String> TEMPLATES;
    private final static String DATE_CLASS_NAME = "class java.util.Date";
    private final static char PREFIX = '{';
    private final static char SUFFIX = '}';
    private final static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static String ROOT_PATH = "/templates/email/";
    private final static  String DEF_REGEX = "\\{(.+?)\\}";

    static {
        TEMPLATES = getTemplates();
    }

    /**
     * 使用 Matcher 进行创建
     *
     * @param templateName
     * @param data
     * @param regex
     * @return
     */
    public static String render(String templateName, Object data, String regex) {
        String template = getTemplateByName(templateName);
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


    /**
     * 使用 char 替换创建
     *
     * @param record   数据源
     * @param templateName 模板
     * @return
     */
    public static String convert(Object record, final String templateName) {
        String result = getTemplateByName(templateName);
        List<String> list = getField(result);
        for (String s : list) {
            String v = getValue(s, record);
            result = result.replace(getKey(s), v);
        }
        return result;
    }

    private static String getTemplateByName(String name){
        if (!TEMPLATES.containsKey(name)){
            throw new NullPointerException("没有找到对应的模板："+name);
        }
        return TEMPLATES.get(name);
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

    private static Map<String, String> getTemplates() {
        Map<String, String> map = new HashMap<>();
        String rootPath = EmailTemplatesUtil.class.getResource(ROOT_PATH).getPath();
        File file = new File(rootPath);
        File[] files = file.listFiles();
        for (File f : files) {
            String name = f.getName();
            name = name.substring(0, name.indexOf("."));
            map.put(name, getStr(f));
        }
        return map;
    }

    private static String getStr(File file) {
        BufferedReader reader = null;
        try {
            FileInputStream stream = new FileInputStream(file);
            StringBuilder builder = new StringBuilder();
            String temp;
            reader = new BufferedReader(new InputStreamReader(stream));
            while ((temp = reader.readLine()) != null) {
                builder.append(temp);
            }
            return builder.toString();
        } catch (Exception e) {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException ex) {
            }
        }
        return null;
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
//        getTemplates();
        /*
        String str = getStr();
        System.out.println("str = " + str);
        */



        /*
        ConsultationRecord record = getR();
        Long t1 = System.currentTimeMillis();
        String fs = render(CONSULTATION,record,DEF_REGEX);
        Long t2 = System.currentTimeMillis();
        System.out.println("render Time  = " + (t2 - t1));


        t1 = System.currentTimeMillis();
        fs = convert(record,CONSULTATION);
        t2 = System.currentTimeMillis();
        System.out.println("convert Time  = " + (t2 - t1));
        */

    }
}
