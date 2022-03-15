import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@WebServlet("*.do")
public class DispatcaherServlet中央控制器 extends  ViewBaseServlet{

    private Map<String,Object> beanmap = new HashMap<>();

    public DispatcaherServlet中央控制器() throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {

//      private Map<String, Object> beanmapu = new HashMap<>();

        /*
        * 解析applicationContext.xml文件
        *
        * */
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("applicationContext.xml");
        //创建DocumentBuilderFactory类为后面的解析做铺垫；
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        //创建DocumentBuilder对象为后面的解析做铺垫；
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        //获取Document对象来解析当前流；
        Document document = documentBuilder.parse("applicationContext.xml");
        //获取applicationContext.xml所有的bean节点；
        NodeList bean = document.getElementsByTagName("bean");
        for (int i = 0; i < bean.getLength(); i++) {
            //获取bean里面的所有元素
            Node item = bean.item(i);
            //判断节点是否是元素节点
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                //将item强转为元素节点(Element)为元素节点
                Element item1 = (Element) item;
                //获取applicationContext.xml中id
                String beanid = item1.getAttribute("id");
               //获取applicationContext.xml中class
                String clazz = item1.getAttribute("class");
                //对应calss的实例对象
                Object beanobj = Class.forName(clazz).newInstance();
                //放入到beanmap中；
                Object put = beanmap.put(beanid, beanobj);


            }
        }

    }

    @Override
    /*
    service为服务器的配置
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置编码
        req.setCharacterEncoding("UTF-8");
        //假设servletPath是/hello.do
        String servletPath = req.getServletPath();//注：servletPath就是相对于是id，从而获取value；
        //获取索引
        int i = servletPath.lastIndexOf(".do");
        //把/hello.do变成hello；
        String substring = servletPath.substring(1, i);
        //相当于value；
        Object controllerBeanObj = beanmap.get(servletPath);
        //获取operate的数据
        String operate = req.getParameter("operate");//注：调用的方法根据operate这个值确定，operate相当于name；
        if (operate == null) {
            operate="index";
        }
        //获取当前类的所有方法
        //方式一
        try {
            Method method = controllerBeanObj.getClass().getDeclaredMethod("operate",HttpServletRequest.class,HttpServletResponse.class);
            if (method != null) {
             method.invoke(this, req, resp);
            }else {
                throw new RuntimeException("operate为非法值");
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //方式二
//        Method[] method = controllerBeanObj.getClass().getDeclaredMethods();
//        for (Method m :method
//             ) {
//            //获取方法名称
//            String name = m.getName();
//            if (operate == name) {
//                try {
//                    m.invoke(this,req,resp);
//                    return;
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            throw new RuntimeException("operate为非法值");
//
//        }




    }
}
