package com.biubiu.servlet;

import com.biubiu.annotation.*;
import com.biubiu.controller.UserController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张海彪
 * @create 2019-02-22 18:26
 */
@WebServlet(name = "dispatchServlet", urlPatterns = "/*", loadOnStartup = 1,
        initParams = {@WebInitParam(name = "base-package", value = "com.biubiu")})
public class DispatcherServlet extends HttpServlet {

    //扫描的基包
    private String basePackage;

    //基包下面所有的带包路径全限定类名
    private List<String> packageNames = new ArrayList<>();

    //注解实例化 注解上的名称：实例化对象
    private Map<String, Object> instanceMap = new HashMap<>();

    //带包路径的全限定名称：注解上的名称
    private Map<String, String> nameMap = new HashMap<>();

    //URL地址和方法的映射关系 SpringMVC就是方法调用链
    private Map<String, Method> urlMethodMap = new HashMap<>();

    //Method和全限定类名映射关系 主要是为了通过Method找到该方法的对象利用反射执行
    private Map<Method, String> methodClassMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        basePackage = config.getInitParameter("base-package");
        try {
            //1、扫描基包得到全部的带包路径权限定类名
            scanBasePackage(basePackage);
            //2、把带有@Controller，@Service，@Repository的类实例化放入MAP中，KEY为注解上的名称
            instanceMap();
            //3、Spring IOC注入
            springIOC();
            //4、完成URL地址与方法的映射关系
            handleUrlMethodMap();
        } catch (Exception e) {
            throw new RuntimeException("init dispatcherServlet error, [" + e.getMessage() + "]");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = url.replaceAll(contextPath, "");

        //通过path找到Method
        Method method = urlMethodMap.get(path);
        if (method != null) {
            //通过Method拿到Controller对象，准备反射执行
            String className = methodClassMap.get(method);
            String controllerName = nameMap.get(className);
            UserController userController = (UserController) instanceMap.get(controllerName);
            method.setAccessible(true);
            try {
                method.invoke(userController);
            } catch (Exception e) {
                throw new RuntimeException("method invoke error");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void scanBasePackage(String basePackage) {
        //为了得到基包下面的URL路径需要对basePackage做转换：将.替换为/
        URL url = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.", "/"));
        if (url == null) throw new RuntimeException("incorrect basePackage");
        File basePackageFile = new File(url.getPath());
        System.out.println("scan: " + basePackageFile);
        File[] childFiles = basePackageFile.listFiles();
        if (childFiles == null) return;
        for (File file : childFiles) {
            if (file.isDirectory()) {
                scanBasePackage(basePackage + "." + file.getName());
            } else if (file.isFile()) {
                packageNames.add(basePackage + "." + file.getName().split("\\.")[0]);
            }
        }
    }

    private void instanceMap() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (packageNames.size() < 1) return;
        for (String packageName : packageNames) {
            Class clz = Class.forName(packageName);
            if (clz.isAnnotationPresent(Controller.class)) {
                Controller controller = (Controller) clz.getAnnotation(Controller.class);
                String controllerName = controller.value();
                nameMap.put(packageName, controllerName);
                instanceMap.put(controllerName, clz.newInstance());
                System.out.println("Controller : " + packageName + " , value : " + controllerName);

            } else if (clz.isAnnotationPresent(Service.class)) {
                Service service = (Service) clz.getAnnotation(Service.class);
                String serviceName = service.value();
                nameMap.put(packageName, serviceName);
                instanceMap.put(serviceName, clz.newInstance());
                System.out.println("Service : " + packageName + " , value : " + serviceName);

            } else if (clz.isAnnotationPresent(Repository.class)) {
                Repository repository = (Repository) clz.getAnnotation(Repository.class);
                String repositoryName = repository.value();
                nameMap.put(packageName, repositoryName);
                instanceMap.put(repositoryName, clz.newInstance());
                System.out.println("Repository : " + packageName + " , value : " + repositoryName);
            }
        }
    }

    private void springIOC() throws IllegalAccessException {
        //控制反转，实体类依赖注入的域的实例化控制权交给容器
        for (Map.Entry<String, Object> entry : instanceMap.entrySet()) {
            Object value = entry.getValue();
            Field[] fields = value.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Qualifier.class)) {
                    String name = field.getAnnotation(Qualifier.class).value();
                    field.setAccessible(true);
                    field.set(entry.getValue(), instanceMap.get(name));
                }
            }
        }
    }

    private void handleUrlMethodMap() throws ClassNotFoundException {
        if (packageNames.size() < 1) return;
        for (String packageName : packageNames) {
            Class clz = Class.forName(packageName);
            if (clz.isAnnotationPresent(Controller.class)) {
                Method[] methods = clz.getMethods();
                StringBuilder baseUrl = new StringBuilder();
                if (clz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = (RequestMapping) clz.getAnnotation(RequestMapping.class);
                    baseUrl.append(requestMapping.value());
                }
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        baseUrl.append(requestMapping.value());
                        urlMethodMap.put(baseUrl.toString(), method);
                        methodClassMap.put(method, packageName);
                    }
                }
            }
        }
    }

}
