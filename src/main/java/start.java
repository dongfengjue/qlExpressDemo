import com.chenbing.model.Model;
import com.chenbing.model.RefreshData;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class start {

    public static void main(String[] args) throws Exception {
        Map<Field,Object> fieldsMap= new HashMap<Field, Object>();
        Map<String, Object> contentMap = new HashMap<String, Object>();

        Model model = new Model();
        model.setA1(1.0);
        model.setA2(2.0);
        doAllFields(contentMap, fieldsMap, model);

        List<Field> fieldList = new ArrayList<Field>(fieldsMap.keySet());


        // 排序
        for (Field field: fieldsMap.keySet()) {
            if (field.isAnnotationPresent(RefreshData.class)) {
                RefreshData refreshData = field.getAnnotation(RefreshData.class);
                String value = refreshData.value();
                sort(fieldList, field, value);
            }
        }

        // 逐条运算结果 并set回去
        for (Field field:fieldList){
            if (field.isAnnotationPresent(RefreshData.class)){

                    RefreshData refreshData = field.getAnnotation(RefreshData.class);
                    String value = refreshData.value();

                    Object object = executeExpress(value, contentMap);
                    field.set(fieldsMap.get(field), object);
                    contentMap.put(field.getName(), object);

                }
        }

        System.out.println("-----"+ model.toString());
    }

    public static Object executeExpress(String value , Map<String,Object> contentMap) throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        for(String str:contentMap.keySet()){
            context.put(str,contentMap.get(str));
        }
        Object r = runner.execute(value, context, null, true, false);
        System.out.println(r);
        return r;
    }
        public static void sort( List<Field> fieldList ,Field fatherField , String annotationStr){
            int fatherIndex = getIndex(fieldList, fatherField);
            for (int i = 0 ; i < fieldList.size();i++){
             // 如果父属性在前面 和子属性互换位置
            if(annotationStr.contains (fieldList.get(i).getName()) && (i > fatherIndex)){
                Field tempField = fieldList.get(i);
                fieldList.set(i, fieldList.get(fatherIndex));
                fieldList.set (fatherIndex, tempField);
                fatherIndex = i;
                }
            }
        }
        public static int getIndex(List<Field> fieldList,Field field){
            for(int i= 0;i <fieldList.size();i++){
                if (field.equals (fieldList. get(i))){
                    return i;
                }
            }
            return fieldList.size() - 1;
        }
        public static void doAllFields(Map<String, Object> map, Map <Field, Object> fieldsMap, Object obj) throws Exception{
            System.out. println("---- "+obj.getClass());
            Class c = obj.getClass();
            while (c!= null) {

                Field[] fields = c.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object object = field.get(obj);

                    String type = field.getGenericType().toString();
                    Boolean isStopType = field.getType().isPrimitive()
                            || type.equals(String.class.toString())
                            || type.equals(Integer.class.toString())
                            || type.equals(Boolean.class.toString())
                            || type.equals(Byte.class.toString())
                            || type.equals(Long.class.toString())
                            || type.equals(Character.class.toString())
                            || type.equals(Short.class.toString())
                            || type.equals(Float.class.toString())
                            || field.getGenericType() instanceof ParameterizedType
                            || type.equals(Double.class.toString());

                    if (isStopType) {
                        map.put(field.getName(), object);
                        fieldsMap.put(field, obj);

                    } else if (field.getType().getName().startsWith("com.chenbing.model")) {

                        if (null == object) {
                            object = field.getType().newInstance();
                        }
                        doAllFields(map, fieldsMap, object);
                    }
                }
                c = c.getSuperclass();
            }





    }


}
