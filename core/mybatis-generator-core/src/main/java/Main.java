

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;


import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import javax.sql.DataSource;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.mybatis.dynamic.sql.SqlBuilder.isLessThan;

/**
 * @ClassName Main
 * @Author minyang
 * @Date 2020/4/24
 **/
public class Main {
    public static String fileName = "";

    public static void main(String[] args) {
        init();


    }

    //通过反射获取相关内容
    public static void ss() throws ClassNotFoundException {
        System.out.println(fileName);
        Class<?> aClass = Class.forName(fileName);
//        Class<SmoGroupInfoMapper> smoGroupInfoMapperClass = SmoGroupInfoMapper.class;
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        System.out.println(aClass.getName());
        Method[] methods = aClass.getMethods();
        for (Method m : methods) {
            String[] parameterNames = u.getParameterNames(m);
            if (parameterNames == null) {

            } else {
                Arrays.asList(parameterNames).forEach((x) -> System.out.println(x));
            }

            Parameter[] parameters = m.getParameters();
            for (Parameter parameter : parameters) {
                System.out.println(parameter.getName());

            }
            AnnotatedType annotatedReturnType = m.getAnnotatedReturnType();
            String name = m.getName();
            System.out.println("method name :" + name);
            System.out.println("return :" + annotatedReturnType.getType().getTypeName());

            Type[] genericParameterTypes = m.getGenericParameterTypes();
            for (Type genericParameterType : genericParameterTypes) {
                String typeName = genericParameterType.getTypeName();
                System.out.println(typeName);

            }

        }
    }

    //初始化数据结构
    public static void init() {
        try {
            List<String> warnings = new ArrayList<String>();
            boolean overwrite = true;
            String pathName = "D:\\generator\\core\\mybatis-generator-core\\src\\main\\resources\\MBG.xml";
            File configFile = new File(pathName);
            ConfigurationParser cp = new ConfigurationParser(warnings);
            org.mybatis.generator.config.Configuration config = cp.parseConfiguration(configFile);

            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);


            List<GeneratedJavaFile> generatedJavaFiles = myBatisGenerator.getGeneratedJavaFiles();
            for (GeneratedJavaFile javaFile : generatedJavaFiles) {
                if (javaFile.getFileName().contains("Mapper")) {
                    fileName = javaFile.getTargetPackage() + "." + javaFile.getFileName();
                    fileName = fileName.replace(".java", "");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void get() {
        Properties properties = new Properties();
        properties.setProperty("driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("url", "jdbc:mysql://dev.mysql.gaodunwangxiao.com:3306/smo?serverTimezone=Asia/Shanghai");
        properties.setProperty("username", "smo_user");
        properties.setProperty("password", "smo_user");
        PooledDataSourceFactory pooledDataSourceFactory = new PooledDataSourceFactory();
        pooledDataSourceFactory.setProperties(properties);
        DataSource dataSource = pooledDataSourceFactory.getDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);


        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        configuration.setCacheEnabled(true);
        configuration.setLazyLoadingEnabled(false);
        configuration.setAggressiveLazyLoading(true);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        SqlSession sqlSession = sqlSessionFactory.openSession();

    }
}

