import com.aliyun.oss.OSSClient;
import com.mysql.jdbc.PreparedStatement;
import com.opt.model.Page;
import com.opt.model.Student;
import com.opt.service.impl.StudentServiceImpl;
import com.opt.util.thirdparty.AliyunOSSClientUtil;
import com.opt.util.thirdparty.OSSUtil;
import com.opt.util.RandomStudent;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static com.opt.util.safe.OSSClientConstants.BACKET_NAME;
import static com.opt.util.safe.OSSClientConstants.FOLDER;


public class TestAction {

    static Logger logger = Logger.getLogger(TestAction.class);

    public static void main(String[] args) {

//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationcontext.xml");
//        UserServiceImpl userService = (UserServiceImpl) applicationContext.getBean("userService");
//        MemCachedClient memCachedClient = (MemCachedClient) applicationContext.getBean("memcachedClient");
//
//        long iphone = 18638292925L;
//        logger.info(userService.findByPhone(iphone));

//        memCachedClient.set("test2","im value2");
//
//        logger.info(memCachedClient.get("test2"));
        long diff = System.currentTimeMillis();
        long min = ((diff / (60 * 1000)) - (diff / (24 * 60 * 60 * 1000) * 24 * 60) - ((diff / (60 * 60 * 1000) - diff / (24 * 60 * 60 * 1000) * 24 * 60 * 24) * 60));
        logger.info(min);
        logger.info(System.currentTimeMillis());
        logger.info(3*60*1000);
        logger.info(System.currentTimeMillis()/(1000*60)+ ":::" +3*60*1000);
    }

    //测试
    @Test
    public void testUpload() {
        //初始化OSSClient
        OSSClient ossClient= AliyunOSSClientUtil.getOSSClient();
        //上传文件
//        String files="D:\\image\\22.jpg,D:\\image\\23.jpg,D:\\image\\24.jpg";
        String files="F:\\picture\\head_ico\\23.png";
        String[] file=files.split(",");
        for(String filename:file){
            //System.out.println("filename:"+filename);
            File filess=new File(filename);
            Map map = AliyunOSSClientUtil.uploadObject2OSS(ossClient, filess, BACKET_NAME, FOLDER);
            String md5key = (String) map.get("MD5Key");
            String key = (String) map.get("key");
            logger.info("上传后的文件MD5数字唯一签名:" + md5key);
            logger.info("上传后的文件key:" + key);
            //上传后的文件MD5数字唯一签名:40F4131427068E08451D37F02021473A
            String url = AliyunOSSClientUtil.getUrl(ossClient,BACKET_NAME,key);
            logger.info(url);
        }

    }


    @Test
    public void jedisTest(){

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationcontext.xml");
        JedisPool jedisPool = (JedisPool) applicationContext.getBean("jedisPool");
        StudentServiceImpl studentService = (StudentServiceImpl) applicationContext.getBean("studentService");
//        ShardedJedisPool shardedJedisPool = (ShardedJedisPool) applicationContext.getBean("shardedJedisPool");
//        ShardedJedis shardedJedis = shardedJedisPool.getResource();

        Page<Student> page = studentService.findByPage(1,5);
        Jedis jedis = jedisPool.getResource();
//        jedis.set("zhang1","张强");
        jedis.set("page1", String.valueOf(page));
        System.out.println(jedis.get("page1"));

    }



    @Test
    public void insertCrud(){

//        String url = "jdbc:mysql://47.98.50.21/jnshu?characterEncoding=UTF-8";
        String url = "jdbc:mysql://127.0.0.1:3306/jnshu?characterEncoding=UTF-8";

        String user = "root";
        String pwd = "zhangqiang";
        StringBuffer sql = new StringBuffer("insert into jnshu_students (stuName,stuPhoto,sex,age,school,office,recommend,pro_id) values ");
        RandomStudent ranStudent = new RandomStudent();

        Connection connection = null;
        PreparedStatement pstm = null;


        try {
            connection = DriverManager.getConnection(url,user,pwd);

            for (int i=0;i<205608;i++){
                if(i>0)sql.append(",");
                sql.append("('" + ranStudent.getNameBuilder().toString() + "'," +
                        "'/stat/images/7-task8.png'," +
                        "1," +
                        ranStudent.getAge(15,40) + ",'" +
                        ranStudent.getSchool() + "','" +
                        ranStudent.getPro() + "','" +
                        ranStudent.getPro() + "'," +
                        "1)"
                );
            }


            pstm = (PreparedStatement) connection.prepareStatement(sql.toString());

            pstm.executeUpdate();
//            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (connection != null) connection.close();
                if (pstm != null) pstm.close();
            }catch (SQLException e) {
                    e.printStackTrace();
            }
        }

    }



    @Test
    public void upload() {

//        需要拷贝的文件
        String input = "C:\\Users\\GhostSugar\\20180611_162654.mp4";
//        写入的文件
        String Output = "F:\\1.mp4";

//        文件输入流
        FileInputStream in = null;
//        文件输出流
        FileOutputStream out = null;

//        缓冲输入输出流 是FileInputStream和FileOutputStream的包装类
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;

        try {

            in = new FileInputStream(input);
            out = new FileOutputStream(Output);

            bin = new BufferedInputStream(in);
            bout = new BufferedOutputStream(out);

            int data;
//            read读取内容 读取不到的时候返回-1
            while ((data = bin.read())!= -1){
//                输出data长度的文件
                bout.write(data);
            }

        }catch (IOException e){
            e.fillInStackTrace();
        }finally {
            try {
                if(bin!=null)bin.close();
                if (bout!=null)bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void writeByteToFile() throws IOException{
        String hello= new String( "hello word!");
        byte[] byteArray= hello.getBytes();

        File file= new File( "d:\\a.txt");
        OutputStream os= new FileOutputStream( file);
        os.write( byteArray);
        os.close();
    }

    @Test
    public void readByteFromFile() throws IOException{
        File file= new File( "d:\\a.txt");
        byte[] byteArray= new byte[( int) file.length()];
        InputStream is= new FileInputStream( file);
        int size= is.read( byteArray);
        System. out.println( "大小:"+size +";内容:" +new String(byteArray));
        is.close();
    }


    @Test
    public void writeCharToFile() throws IOException{
        String hello= new String( "hello word!");
        File file= new File( "d:/test.txt");
        Writer os= new FileWriter(file);
        os.write( hello);
        os.close();
    }

    @Test
    public void ossTest(){
        OSSUtil ossUpd = new OSSUtil();
        File file = new File("C:\\Users\\GhostSugar\\Desktop\\QQ20180621101024.png");
        ossUpd.OSSUpd(file);
    }



}
